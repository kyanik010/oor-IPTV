package com.example.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class ResizeMode {
    FIT, FILL, ZOOM, FIXED_16_9, FIXED_4_3
}

data class TrackInfo(
    val id: String,
    val label: String,
    val language: String?,
    val isSelected: Boolean,
    val groupIndex: Int,
    val trackIndex: Int
)

@OptIn(UnstableApi::class)
class PlayerManager(private val context: Context) {

    private val trackSelector = DefaultTrackSelector(context)

    // Load control for fast IPTV channel zapping and buffer size
    private val loadControl = DefaultLoadControl.Builder()
        .setBufferDurationsMs(
            2000,   // Min buffer: 2 seconds
            15000,  // Max buffer: 15 seconds
            1000,   // Buffer for playback start: 1 second
            1500    // Buffer for re-buffer: 1.5 seconds
        )
        .build()

    val exoPlayer: ExoPlayer = ExoPlayer.Builder(context)
        .setTrackSelector(trackSelector)
        .setLoadControl(loadControl)
        .setMediaSourceFactory(DefaultMediaSourceFactory(context))
        .build()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isBuffering = MutableStateFlow(false)
    val isBuffering: StateFlow<Boolean> = _isBuffering.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _audioTracks = MutableStateFlow<List<TrackInfo>>(emptyList())
    val audioTracks: StateFlow<List<TrackInfo>> = _audioTracks.asStateFlow()

    private val _subtitleTracks = MutableStateFlow<List<TrackInfo>>(emptyList())
    val subtitleTracks: StateFlow<List<TrackInfo>> = _subtitleTracks.asStateFlow()

    private val _resizeMode = MutableStateFlow(ResizeMode.FIT)
    val resizeMode: StateFlow<ResizeMode> = _resizeMode.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private var retryCount = 0
    private val maxRetries = 3
    private var currentUrl: String? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    private var progressJob: Job? = null

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                _isPlaying.value = playing
            }

            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_BUFFERING -> _isBuffering.value = true
                    Player.STATE_READY -> {
                        _isBuffering.value = false
                        _duration.value = exoPlayer.duration.coerceAtLeast(0L)
                        retryCount = 0
                        _errorMessage.value = null
                    }
                    Player.STATE_ENDED -> {
                        _isBuffering.value = false
                    }
                    Player.STATE_IDLE -> {
                        _isBuffering.value = false
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                _isBuffering.value = false
                if (retryCount < maxRetries && currentUrl != null) {
                    retryCount++
                    scope.launch {
                        delay((1000L * retryCount))
                        playUrl(currentUrl!!)
                    }
                } else {
                    _errorMessage.value = "فشل تشغيل البث: ${error.localizedMessage ?: "خطأ في الشبكة أو الرابط"}"
                }
            }

            override fun onTracksChanged(tracks: Tracks) {
                updateAvailableTracks(tracks)
            }
        })

        // Poll position
        progressJob = scope.launch {
            while (isActive) {
                if (exoPlayer.isPlaying) {
                    _currentPosition.value = exoPlayer.currentPosition.coerceAtLeast(0L)
                    _duration.value = exoPlayer.duration.coerceAtLeast(0L)
                }
                delay(500)
            }
        }
    }

    fun playUrl(url: String, startPositionMs: Long = 0L) {
        currentUrl = url
        _errorMessage.value = null
        val mediaItem = MediaItem.fromUri(url)
        exoPlayer.setMediaItem(mediaItem)
        if (startPositionMs > 0L) {
            exoPlayer.seekTo(startPositionMs)
        }
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    fun togglePlayPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }

    fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs)
    }

    fun seekForward(deltaMs: Long = 10000L) {
        val newPos = (exoPlayer.currentPosition + deltaMs).coerceAtMost(exoPlayer.duration)
        exoPlayer.seekTo(newPos)
    }

    fun seekRewind(deltaMs: Long = 10000L) {
        val newPos = (exoPlayer.currentPosition - deltaMs).coerceAtLeast(0L)
        exoPlayer.seekTo(newPos)
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackSpeed.value = speed
        exoPlayer.setPlaybackSpeed(speed)
    }

    fun setResizeMode(mode: ResizeMode) {
        _resizeMode.value = mode
    }

    fun selectAudioTrack(trackInfo: TrackInfo) {
        val trackGroup = trackSelector.currentMappedTrackInfo?.getTrackGroups(trackInfo.groupIndex)?.get(trackInfo.trackIndex)
        trackSelector.parameters = trackSelector.buildUponParameters()
            .setOverrideForType(
                TrackSelectionOverride(trackGroup ?: return, trackInfo.trackIndex)
            )
            .build()
    }

    fun selectSubtitleTrack(trackInfo: TrackInfo?) {
        if (trackInfo == null) {
            trackSelector.parameters = trackSelector.buildUponParameters()
                .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, true)
                .build()
        } else {
            val trackGroup = trackSelector.currentMappedTrackInfo?.getTrackGroups(trackInfo.groupIndex)?.get(trackInfo.trackIndex)
            if (trackGroup != null) {
                trackSelector.parameters = trackSelector.buildUponParameters()
                    .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, false)
                    .setOverrideForType(TrackSelectionOverride(trackGroup, trackInfo.trackIndex))
                    .build()
            }
        }
    }

    private fun updateAvailableTracks(tracks: Tracks) {
        val audioList = ArrayList<TrackInfo>()
        val subList = ArrayList<TrackInfo>()

        var groupIdx = 0
        for (group in tracks.groups) {
            val type = group.type
            for (trackIdx in 0 until group.length) {
                val format = group.getTrackFormat(trackIdx)
                val isSelected = group.isTrackSelected(trackIdx)
                val label = format.label ?: format.language ?: "Track ${trackIdx + 1}"
                val trackInfo = TrackInfo(
                    id = "${groupIdx}_$trackIdx",
                    label = label,
                    language = format.language,
                    isSelected = isSelected,
                    groupIndex = groupIdx,
                    trackIndex = trackIdx
                )
                if (type == C.TRACK_TYPE_AUDIO) {
                    audioList.add(trackInfo)
                } else if (type == C.TRACK_TYPE_TEXT) {
                    subList.add(trackInfo)
                }
            }
            groupIdx++
        }
        _audioTracks.value = audioList
        _subtitleTracks.value = subList
    }

    fun release() {
        progressJob?.cancel()
        exoPlayer.release()
    }
}
