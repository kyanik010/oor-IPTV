package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AccountEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.ChannelEntity
import com.example.data.model.ContentType
import com.example.data.model.EpisodeEntity
import com.example.data.model.EpgProgramEntity
import com.example.data.model.MovieEntity
import com.example.data.model.ProfileEntity
import com.example.data.model.SeriesEntity
import com.example.data.model.WatchProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IptvDao {

    // --- Accounts ---
    @Query("SELECT * FROM accounts WHERE isActive = 1 LIMIT 1")
    fun getActiveAccount(): Flow<AccountEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AccountEntity)

    @Query("DELETE FROM accounts")
    suspend fun clearAccounts()

    @Query("DELETE FROM categories")
    suspend fun clearCategories()

    @Query("DELETE FROM channels")
    suspend fun clearAllChannels()

    @Query("DELETE FROM movies")
    suspend fun clearAllMovies()

    @Query("DELETE FROM series")
    suspend fun clearAllSeries()

    @Query("DELETE FROM episodes")
    suspend fun clearAllEpisodes()

    // --- Profiles ---
    @Query("SELECT * FROM profiles")
    fun getAllProfiles(): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM profiles WHERE isSelected = 1 LIMIT 1")
    fun getSelectedProfile(): Flow<ProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfiles(profiles: List<ProfileEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileEntity)

    @Query("UPDATE profiles SET isSelected = CASE WHEN id = :profileId THEN 1 ELSE 0 END")
    suspend fun selectProfile(profileId: String)

    // --- Categories ---
    @Query("SELECT * FROM categories WHERE type = :type ORDER BY categoryName ASC")
    fun getCategoriesByType(type: ContentType): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Query("DELETE FROM categories WHERE type = :type")
    suspend fun clearCategoriesByType(type: ContentType)

    // --- Channels ---
    @Query("SELECT * FROM channels WHERE categoryId = :categoryId AND isHidden = 0 ORDER BY num ASC, name ASC")
    fun getChannelsByCategory(categoryId: String): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE isFavorite = 1 AND isHidden = 0 ORDER BY num ASC, name ASC")
    fun getFavoriteChannels(): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE isHidden = 0 ORDER BY num ASC, name ASC LIMIT :limit OFFSET :offset")
    suspend fun getChannelsPaged(limit: Int, offset: Int): List<ChannelEntity>

    @Query("SELECT * FROM channels WHERE name LIKE '%' || :query || '%' AND isHidden = 0 LIMIT 100")
    suspend fun searchChannels(query: String): List<ChannelEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannels(channels: List<ChannelEntity>)

    @Query("UPDATE channels SET isFavorite = :isFavorite WHERE streamId = :streamId")
    suspend fun setChannelFavorite(streamId: Int, isFavorite: Boolean)

    @Query("DELETE FROM channels")
    suspend fun clearChannels()

    // --- Movies ---
    @Query("SELECT * FROM movies WHERE categoryId = :categoryId ORDER BY name ASC")
    fun getMoviesByCategory(categoryId: String): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies ORDER BY rating DESC LIMIT 50")
    fun getTopRatedMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE isFavorite = 1")
    fun getFavoriteMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE name LIKE '%' || :query || '%' LIMIT 100")
    suspend fun searchMovies(query: String): List<MovieEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Query("UPDATE movies SET isFavorite = :isFavorite WHERE streamId = :streamId")
    suspend fun setMovieFavorite(streamId: Int, isFavorite: Boolean)

    @Query("DELETE FROM movies")
    suspend fun clearMovies()

    // --- Series ---
    @Query("SELECT * FROM series WHERE categoryId = :categoryId ORDER BY name ASC")
    fun getSeriesByCategory(categoryId: String): Flow<List<SeriesEntity>>

    @Query("SELECT * FROM series ORDER BY rating DESC LIMIT 50")
    fun getTopRatedSeries(): Flow<List<SeriesEntity>>

    @Query("SELECT * FROM series WHERE isFavorite = 1")
    fun getFavoriteSeries(): Flow<List<SeriesEntity>>

    @Query("SELECT * FROM series WHERE name LIKE '%' || :query || '%' LIMIT 100")
    suspend fun searchSeries(query: String): List<SeriesEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeries(series: List<SeriesEntity>)

    @Query("UPDATE series SET isFavorite = :isFavorite WHERE seriesId = :seriesId")
    suspend fun setSeriesFavorite(seriesId: Int, isFavorite: Boolean)

    @Query("DELETE FROM series")
    suspend fun clearSeries()

    // --- Episodes ---
    @Query("SELECT * FROM episodes WHERE seriesId = :seriesId AND seasonNum = :seasonNum ORDER BY episodeNum ASC")
    fun getEpisodes(seriesId: Int, seasonNum: Int): Flow<List<EpisodeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisodes(episodes: List<EpisodeEntity>)

    // --- EPG ---
    @Query("SELECT * FROM epg_programs WHERE channelId = :channelId AND stopTimestamp >= :now ORDER BY startTimestamp ASC LIMIT 5")
    suspend fun getUpcomingPrograms(channelId: String, now: Long): List<EpgProgramEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpgPrograms(programs: List<EpgProgramEntity>)

    // --- Watch Progress ---
    @Query("SELECT * FROM watch_progress WHERE profileId = :profileId ORDER BY lastUpdatedTimestamp DESC LIMIT 20")
    fun getRecentWatchProgress(profileId: String): Flow<List<WatchProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWatchProgress(progress: WatchProgressEntity)

    @Query("DELETE FROM watch_progress WHERE contentId = :contentId AND profileId = :profileId")
    suspend fun deleteWatchProgress(contentId: String, profileId: String)
}
