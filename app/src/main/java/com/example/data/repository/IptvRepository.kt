package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.model.AccountEntity
import com.example.data.model.AccountType
import com.example.data.model.CategoryEntity
import com.example.data.model.ChannelEntity
import com.example.data.model.ContentType
import com.example.data.model.EpisodeEntity
import com.example.data.model.EpgProgramEntity
import com.example.data.model.MovieEntity
import com.example.data.model.ProfileEntity
import com.example.data.model.SeriesEntity
import com.example.data.model.WatchProgressEntity
import com.example.data.parser.M3uParser
import com.example.data.remote.XtreamApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.UUID
import java.util.concurrent.TimeUnit

class IptvRepository(context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val dao = db.iptvDao()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://example.com/") // Dummy root; dynamic URLs used in methods
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    private val xtreamApi = retrofit.create(XtreamApiService::class.java)

    // Flow getters
    val activeAccount: Flow<AccountEntity?> = dao.getActiveAccount()
    val profiles: Flow<List<ProfileEntity>> = dao.getAllProfiles()
    val selectedProfile: Flow<ProfileEntity?> = dao.getSelectedProfile()
    val favoriteChannels: Flow<List<ChannelEntity>> = dao.getFavoriteChannels()
    val topRatedMovies: Flow<List<MovieEntity>> = dao.getTopRatedMovies()
    val topRatedSeries: Flow<List<SeriesEntity>> = dao.getTopRatedSeries()

    fun getCategories(type: ContentType): Flow<List<CategoryEntity>> = dao.getCategoriesByType(type)
    fun getChannelsByCategory(categoryId: String): Flow<List<ChannelEntity>> = dao.getChannelsByCategory(categoryId)
    fun getMoviesByCategory(categoryId: String): Flow<List<MovieEntity>> = dao.getMoviesByCategory(categoryId)
    fun getSeriesByCategory(categoryId: String): Flow<List<SeriesEntity>> = dao.getSeriesByCategory(categoryId)
    fun getEpisodes(seriesId: Int, seasonNum: Int): Flow<List<EpisodeEntity>> = dao.getEpisodes(seriesId, seasonNum)
    fun getRecentWatchProgress(profileId: String): Flow<List<WatchProgressEntity>> = dao.getRecentWatchProgress(profileId)

    suspend fun initializeDefaultProfilesIfEmpty() = withContext(Dispatchers.IO) {
        val defaultProfiles = listOf(
            ProfileEntity(id = "p_main", name = "المستخدم الرئيسي", avatarRes = "avatar_gold", isSelected = true),
            ProfileEntity(id = "p_family", name = "العائلة", avatarRes = "avatar_family"),
            ProfileEntity(id = "p_kids", name = "الأطفال", avatarRes = "avatar_kids", isKids = true)
        )
        dao.insertProfiles(defaultProfiles)
    }

    suspend fun selectProfile(profileId: String) = withContext(Dispatchers.IO) {
        dao.selectProfile(profileId)
    }

    suspend fun createProfile(name: String, isKids: Boolean, pinCode: String?) = withContext(Dispatchers.IO) {
        val newProfile = ProfileEntity(
            id = UUID.randomUUID().toString(),
            name = name,
            avatarRes = if (isKids) "avatar_kids" else "avatar_gold",
            pinCode = pinCode,
            isKids = isKids,
            isSelected = false
        )
        dao.insertProfile(newProfile)
    }

    suspend fun toggleFavoriteChannel(streamId: Int, currentFav: Boolean) = withContext(Dispatchers.IO) {
        dao.setChannelFavorite(streamId, !currentFav)
    }

    suspend fun toggleFavoriteMovie(streamId: Int, currentFav: Boolean) = withContext(Dispatchers.IO) {
        dao.setMovieFavorite(streamId, !currentFav)
    }

    suspend fun toggleFavoriteSeries(seriesId: Int, currentFav: Boolean) = withContext(Dispatchers.IO) {
        dao.setSeriesFavorite(seriesId, !currentFav)
    }

    suspend fun saveWatchProgress(progress: WatchProgressEntity) = withContext(Dispatchers.IO) {
        dao.saveWatchProgress(progress)
    }

    suspend fun searchAll(query: String): Triple<List<ChannelEntity>, List<MovieEntity>, List<SeriesEntity>> = withContext(Dispatchers.IO) {
        val channels = dao.searchChannels(query)
        val movies = dao.searchMovies(query)
        val series = dao.searchSeries(query)
        Triple(channels, movies, series)
    }

    // Xtream login and sync
    suspend fun loginXtream(server: String, user: String, pass: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            var formattedServer = server.trim()
            if (!formattedServer.startsWith("http://") && !formattedServer.startsWith("https://")) {
                formattedServer = "http://$formattedServer"
            }
            if (!formattedServer.endsWith("/")) {
                formattedServer = "$formattedServer/"
            }

            val playerApiUrl = "${formattedServer}player_api.php"
            val authResponse = xtreamApi.authenticate(playerApiUrl, user, pass)

            if (authResponse.userInfo?.status?.equals("Active", ignoreCase = true) != false) {
                // Save account
                val account = AccountEntity(
                    id = "xtream_main",
                    type = AccountType.XTREAM,
                    serverUrl = formattedServer,
                    username = user,
                    password = pass,
                    playlistName = "Xtream Account",
                    isActive = true
                )
                dao.clearAccounts()
                dao.insertAccount(account)

                // Fetch categories and initial channels
                syncXtreamContent(formattedServer, user, pass)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Account is not active: ${authResponse.userInfo.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // M3U Login and sync
    suspend fun loginM3u(playlistUrl: String, playlistName: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val parsed = M3uParser.parseFromUrl(playlistUrl.trim())

            val account = AccountEntity(
                id = "m3u_main",
                type = AccountType.M3U,
                playlistUrl = playlistUrl.trim(),
                playlistName = playlistName.ifBlank { "M3U Playlist" },
                isActive = true
            )

            dao.clearAccounts()
            dao.insertAccount(account)

            // Insert into Room
            dao.insertCategories(parsed.categories)
            dao.insertChannels(parsed.channels)
            dao.insertMovies(parsed.movies)
            dao.insertSeries(parsed.series)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun syncXtreamContent(server: String, user: String, pass: String) {
        val playerApiUrl = "${server}player_api.php"
        try {
            val liveCats = xtreamApi.getLiveCategories(playerApiUrl, user, pass)
            val vodCats = xtreamApi.getVodCategories(playerApiUrl, user, pass)
            val seriesCats = xtreamApi.getSeriesCategories(playerApiUrl, user, pass)

            val catEntities = ArrayList<CategoryEntity>()
            liveCats.forEach {
                catEntities.add(CategoryEntity(it.categoryId, it.categoryName, it.parentId, ContentType.LIVE))
            }
            vodCats.forEach {
                catEntities.add(CategoryEntity(it.categoryId, it.categoryName, it.parentId, ContentType.MOVIE))
            }
            seriesCats.forEach {
                catEntities.add(CategoryEntity(it.categoryId, it.categoryName, it.parentId, ContentType.SERIES))
            }
            dao.insertCategories(catEntities)

            // Fetch live streams
            val liveStreams = xtreamApi.getLiveStreams(playerApiUrl, user, pass)
            val channelEntities = liveStreams.map {
                ChannelEntity(
                    streamId = it.streamId,
                    num = it.num ?: 0,
                    name = it.name,
                    streamIcon = it.streamIcon,
                    epgChannelId = it.epgChannelId,
                    categoryId = it.categoryId ?: "0",
                    streamUrl = "${server}live/$user/$pass/${it.streamId}.m3u8"
                )
            }
            dao.insertChannels(channelEntities)
        } catch (e: Exception) {
            // Log or fallback
        }
    }

    // Load initial curated sample streams for instant demo and testing
    suspend fun seedDemoContentIfEmpty() = withContext(Dispatchers.IO) {
        val demoCategories = listOf(
            CategoryEntity("cat_news", "أخبار (News)", 0, ContentType.LIVE),
            CategoryEntity("cat_sports", "رياضة (Sports)", 0, ContentType.LIVE),
            CategoryEntity("cat_cinema", "أفلام وسينما (Cinema)", 0, ContentType.LIVE),
            CategoryEntity("cat_kids", "أطفال وكرتون (Kids)", 0, ContentType.LIVE),
            CategoryEntity("cat_vod_action", "أفلام أكشن (Action VOD)", 0, ContentType.MOVIE),
            CategoryEntity("cat_vod_drama", "أفلام دراما (Drama VOD)", 0, ContentType.MOVIE),
            CategoryEntity("cat_series_top", "مسلسلات مميزة (Featured Series)", 0, ContentType.SERIES)
        )
        dao.insertCategories(demoCategories)

        val demoChannels = listOf(
            ChannelEntity(
                streamId = 101,
                num = 1,
                name = "الجزيرة الإخبارية HD",
                streamIcon = "https://images.unsplash.com/photo-1585829365295-ab7cd400c167?w=120",
                epgChannelId = "aljazeera",
                categoryId = "cat_news",
                streamUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
                isFavorite = true
            ),
            ChannelEntity(
                streamId = 102,
                num = 2,
                name = "العربية الحدث HD",
                streamIcon = "https://images.unsplash.com/photo-1504711434969-e33886168f5c?w=120",
                epgChannelId = "alarabiya",
                categoryId = "cat_news",
                streamUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
                isFavorite = true
            ),
            ChannelEntity(
                streamId = 103,
                num = 3,
                name = "بي إن سبورتس الإخبارية (beIN Sports News)",
                streamIcon = "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=120",
                epgChannelId = "beinsports",
                categoryId = "cat_sports",
                streamUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
                isFavorite = true
            ),
            ChannelEntity(
                streamId = 104,
                num = 4,
                name = "قناة دبي الرياضية (Dubai Sports 1)",
                streamIcon = "https://images.unsplash.com/photo-1461896836934-ffe607ba8211?w=120",
                epgChannelId = "dubaisports",
                categoryId = "cat_sports",
                streamUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
            ),
            ChannelEntity(
                streamId = 105,
                num = 5,
                name = "روتانا سينما HD",
                streamIcon = "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=120",
                epgChannelId = "rotana",
                categoryId = "cat_cinema",
                streamUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
                isFavorite = true
            ),
            ChannelEntity(
                streamId = 106,
                num = 6,
                name = "سبيستون للأطفال (Spacetoon TV)",
                streamIcon = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=120",
                epgChannelId = "spacetoon",
                categoryId = "cat_kids",
                streamUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
            )
        )
        dao.insertChannels(demoChannels)

        val demoMovies = listOf(
            MovieEntity(
                streamId = 201,
                name = "The Dark Knight",
                streamIcon = "https://images.unsplash.com/photo-1509281373149-e957c6296406?w=400",
                rating = 9.0,
                year = "2024",
                genre = "Action, Thriller",
                plot = "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests.",
                durationSecs = 9120,
                categoryId = "cat_vod_action",
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                isFavorite = true
            ),
            MovieEntity(
                streamId = 202,
                name = "Interstellar",
                streamIcon = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=400",
                rating = 8.7,
                year = "2023",
                genre = "Sci-Fi, Adventure",
                plot = "A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival.",
                durationSecs = 10140,
                categoryId = "cat_vod_action",
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                isFavorite = true
            ),
            MovieEntity(
                streamId = 203,
                name = "Oppenheimer",
                streamIcon = "https://images.unsplash.com/photo-1440404653325-ab127d49abc1?w=400",
                rating = 8.9,
                year = "2024",
                genre = "Biography, Drama",
                plot = "The story of American scientist J. Robert Oppenheimer and his role in the development of the atomic bomb.",
                durationSecs = 10800,
                categoryId = "cat_vod_drama",
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"
            )
        )
        dao.insertMovies(demoMovies)

        val demoSeries = listOf(
            SeriesEntity(
                seriesId = 301,
                name = "Game of Thrones",
                cover = "https://images.unsplash.com/photo-1514306191717-452ec28c7814?w=400",
                rating = 9.2,
                year = "2024",
                genre = "Fantasy, Drama",
                plot = "Nine noble families fight for control over the lands of Westeros.",
                categoryId = "cat_series_top",
                isFavorite = true
            ),
            SeriesEntity(
                seriesId = 302,
                name = "Breaking Bad",
                cover = "https://images.unsplash.com/photo-1578328819058-b69f3a3b0f6b?w=400",
                rating = 9.5,
                year = "2023",
                genre = "Crime, Drama",
                plot = "A chemistry teacher diagnosed with inoperable lung cancer turns to manufacturing methamphetamine.",
                categoryId = "cat_series_top",
                isFavorite = true
            )
        )
        dao.insertSeries(demoSeries)

        // Seed sample episodes
        val demoEpisodes = listOf(
            EpisodeEntity(
                episodeId = "ep_301_1",
                seriesId = 301,
                seasonNum = 1,
                episodeNum = 1,
                title = "Winter Is Coming",
                durationSecs = 3600,
                plot = "Ned Stark is torn between his family and an old friend when asked to serve at the side of King Robert Baratheon.",
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
            ),
            EpisodeEntity(
                episodeId = "ep_301_2",
                seriesId = 301,
                seasonNum = 1,
                episodeNum = 2,
                title = "The Kingsroad",
                durationSecs = 3400,
                plot = "While Bran recovers from his fall, Ned, only accompanied by his daughters, heads south to King's Landing.",
                streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"
            )
        )
        dao.insertEpisodes(demoEpisodes)

        // Seed demo watch progress
        dao.saveWatchProgress(
            WatchProgressEntity(
                contentId = "201",
                profileId = "p_main",
                title = "The Dark Knight",
                posterUrl = "https://images.unsplash.com/photo-1509281373149-e957c6296406?w=400",
                contentType = ContentType.MOVIE,
                positionMs = 4500000L,
                durationMs = 9120000L
            )
        )
    }
}
