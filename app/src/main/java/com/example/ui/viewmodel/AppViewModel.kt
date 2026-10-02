package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.activation.DeviceActivationManager
import com.example.data.model.AccountEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.ChannelEntity
import com.example.data.model.ContentType
import com.example.data.model.DeviceActivationInfo
import com.example.data.model.EpisodeEntity
import com.example.data.model.MovieEntity
import com.example.data.model.ProfileEntity
import com.example.data.model.SeriesEntity
import com.example.data.model.WatchProgressEntity
import com.example.data.repository.IptvRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ScreenRoute {
    object Splash : ScreenRoute()
    object Activation : ScreenRoute()
    object Profiles : ScreenRoute()
    object Home : ScreenRoute()
    object LiveTv : ScreenRoute()
    object Movies : ScreenRoute()
    object Series : ScreenRoute()
    object MovieDetail : ScreenRoute()
    object SeriesDetail : ScreenRoute()
    object Epg : ScreenRoute()
    object Search : ScreenRoute()
    object Favorites : ScreenRoute()
    object Settings : ScreenRoute()
    object Player : ScreenRoute()
}

data class PlayableMedia(
    val id: String,
    val title: String,
    val streamUrl: String,
    val posterUrl: String? = null,
    val isLive: Boolean = true,
    val channelNumber: Int? = null,
    val currentProgram: String? = null
)

class AppViewModel(application: Application) : AndroidViewModel(application) {

    val repository = IptvRepository(application)
    val activationManager = DeviceActivationManager(application)

    // Current Screen
    private val _currentScreen = MutableStateFlow<ScreenRoute>(ScreenRoute.Splash)
    val currentScreen: StateFlow<ScreenRoute> = _currentScreen.asStateFlow()

    // Navigation backstack
    private val backStack = mutableListOf<ScreenRoute>()

    // Current playing media
    private val _activeMedia = MutableStateFlow<PlayableMedia?>(null)
    val activeMedia: StateFlow<PlayableMedia?> = _activeMedia.asStateFlow()

    // Selected Movie / Series for detail views
    private val _selectedMovie = MutableStateFlow<MovieEntity?>(null)
    val selectedMovie: StateFlow<MovieEntity?> = _selectedMovie.asStateFlow()

    private val _selectedSeries = MutableStateFlow<SeriesEntity?>(null)
    val selectedSeries: StateFlow<SeriesEntity?> = _selectedSeries.asStateFlow()

    // Activation State
    val activationInfo: StateFlow<DeviceActivationInfo> = activationManager.activationState

    // Active Account
    val activeAccount: StateFlow<AccountEntity?> = repository.activeAccount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Profiles
    val profiles: StateFlow<List<ProfileEntity>> = repository.profiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedProfile: StateFlow<ProfileEntity?> = repository.selectedProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Live Channels & Categories
    val liveCategories: StateFlow<List<CategoryEntity>> = repository.getCategories(ContentType.LIVE)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedLiveCategory = MutableStateFlow<CategoryEntity?>(null)
    val selectedLiveCategory: StateFlow<CategoryEntity?> = _selectedLiveCategory.asStateFlow()

    private val _liveChannels = MutableStateFlow<List<ChannelEntity>>(emptyList())
    val liveChannels: StateFlow<List<ChannelEntity>> = _liveChannels.asStateFlow()

    // Movies & Categories
    val movieCategories: StateFlow<List<CategoryEntity>> = repository.getCategories(ContentType.MOVIE)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedMovieCategory = MutableStateFlow<CategoryEntity?>(null)
    val selectedMovieCategory: StateFlow<CategoryEntity?> = _selectedMovieCategory.asStateFlow()

    private val _movies = MutableStateFlow<List<MovieEntity>>(emptyList())
    val movies: StateFlow<List<MovieEntity>> = _movies.asStateFlow()

    // Series & Categories
    val seriesCategories: StateFlow<List<CategoryEntity>> = repository.getCategories(ContentType.SERIES)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedSeriesCategory = MutableStateFlow<CategoryEntity?>(null)
    val selectedSeriesCategory: StateFlow<CategoryEntity?> = _selectedSeriesCategory.asStateFlow()

    private val _seriesList = MutableStateFlow<List<SeriesEntity>>(emptyList())
    val seriesList: StateFlow<List<SeriesEntity>> = _seriesList.asStateFlow()

    // Favorites & Recent
    val favoriteChannels: StateFlow<List<ChannelEntity>> = repository.favoriteChannels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val topRatedMovies: StateFlow<List<MovieEntity>> = repository.topRatedMovies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val topRatedSeries: StateFlow<List<SeriesEntity>> = repository.topRatedSeries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _recentProgress = MutableStateFlow<List<WatchProgressEntity>>(emptyList())
    val recentProgress: StateFlow<List<WatchProgressEntity>> = _recentProgress.asStateFlow()

    // Search state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<Triple<List<ChannelEntity>, List<MovieEntity>, List<SeriesEntity>>>(
        Triple(emptyList(), emptyList(), emptyList())
    )
    val searchResults = _searchResults.asStateFlow()

    // UI Loading & Error
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeDefaultProfilesIfEmpty()
            repository.seedDemoContentIfEmpty()
        }

        // Auto observe channels when category changes
        viewModelScope.launch {
            selectedLiveCategory.collect { category ->
                if (category != null) {
                    repository.getChannelsByCategory(category.categoryId).collect { channels ->
                        _liveChannels.value = channels
                    }
                }
            }
        }

        // Auto observe movies when category changes
        viewModelScope.launch {
            selectedMovieCategory.collect { category ->
                if (category != null) {
                    repository.getMoviesByCategory(category.categoryId).collect { movies ->
                        _movies.value = movies
                    }
                }
            }
        }

        // Auto observe series when category changes
        viewModelScope.launch {
            selectedSeriesCategory.collect { category ->
                if (category != null) {
                    repository.getSeriesByCategory(category.categoryId).collect { series ->
                        _seriesList.value = series
                    }
                }
            }
        }
    }

    fun navigateTo(route: ScreenRoute) {
        if (_currentScreen.value != route) {
            backStack.add(_currentScreen.value)
            _currentScreen.value = route
        }
    }

    fun navigateBack(): Boolean {
        if (backStack.isNotEmpty()) {
            _currentScreen.value = backStack.removeAt(backStack.size - 1)
            return true
        }
        return false
    }

    fun selectLiveCategory(category: CategoryEntity) {
        _selectedLiveCategory.value = category
    }

    fun selectMovieCategory(category: CategoryEntity) {
        _selectedMovieCategory.value = category
    }

    fun selectSeriesCategory(category: CategoryEntity) {
        _selectedSeriesCategory.value = category
    }

    fun playChannel(channel: ChannelEntity) {
        _activeMedia.value = PlayableMedia(
            id = channel.streamId.toString(),
            title = channel.name,
            streamUrl = channel.streamUrl,
            posterUrl = channel.streamIcon,
            isLive = true,
            channelNumber = channel.num
        )
        navigateTo(ScreenRoute.Player)
    }

    fun playMovie(movie: MovieEntity) {
        _activeMedia.value = PlayableMedia(
            id = movie.streamId.toString(),
            title = movie.name,
            streamUrl = movie.streamUrl.ifBlank { "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4" },
            posterUrl = movie.streamIcon,
            isLive = false
        )
        navigateTo(ScreenRoute.Player)
    }

    fun playEpisode(episode: EpisodeEntity, seriesName: String) {
        _activeMedia.value = PlayableMedia(
            id = episode.episodeId,
            title = "$seriesName - ${episode.title}",
            streamUrl = episode.streamUrl.ifBlank { "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4" },
            posterUrl = null,
            isLive = false
        )
        navigateTo(ScreenRoute.Player)
    }

    fun openMovieDetail(movie: MovieEntity) {
        _selectedMovie.value = movie
        navigateTo(ScreenRoute.MovieDetail)
    }

    fun openSeriesDetail(series: SeriesEntity) {
        _selectedSeries.value = series
        navigateTo(ScreenRoute.SeriesDetail)
    }

    fun toggleChannelFavorite(channel: ChannelEntity) {
        viewModelScope.launch {
            repository.toggleFavoriteChannel(channel.streamId, channel.isFavorite)
        }
    }

    fun toggleMovieFavorite(movie: MovieEntity) {
        viewModelScope.launch {
            repository.toggleFavoriteMovie(movie.streamId, movie.isFavorite)
        }
    }

    fun toggleSeriesFavorite(series: SeriesEntity) {
        viewModelScope.launch {
            repository.toggleFavoriteSeries(series.seriesId, series.isFavorite)
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (query.length >= 2) {
            viewModelScope.launch {
                _searchResults.value = repository.searchAll(query)
            }
        } else {
            _searchResults.value = Triple(emptyList(), emptyList(), emptyList())
        }
    }

    fun activateCode(code: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = activationManager.activateWithCode(code)
            _isLoading.value = false
            if (result.isSuccess) {
                onResult(true, null)
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "الكود غير صالح")
            }
        }
    }

    fun loginXtream(server: String, user: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.loginXtream(server, user, pass)
            _isLoading.value = false
            if (result.isSuccess) {
                onResult(true, null)
                navigateTo(ScreenRoute.Profiles)
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "فشل الاتصال بالسيرفر")
            }
        }
    }

    fun loginM3u(url: String, name: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.loginM3u(url, name)
            _isLoading.value = false
            if (result.isSuccess) {
                onResult(true, null)
                navigateTo(ScreenRoute.Profiles)
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "فشل تحميل قائمة M3U")
            }
        }
    }

    fun selectProfile(profile: ProfileEntity) {
        viewModelScope.launch {
            repository.selectProfile(profile.id)
            repository.getRecentWatchProgress(profile.id).collect {
                _recentProgress.value = it
            }
        }
        navigateTo(ScreenRoute.Home)
    }

    fun createProfile(name: String, isKids: Boolean, pinCode: String?) {
        viewModelScope.launch {
            repository.createProfile(name, isKids, pinCode)
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
