package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.view.WindowManager
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import com.example.player.PlayerManager
import com.example.player.ResizeMode
import com.example.player.TrackInfo
import com.example.ui.components.ChannelCard
import com.example.ui.components.LiveBadge
import com.example.ui.components.PrimaryButton
import com.example.ui.components.tvFocusable
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.Dimens
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.Shapes
import com.example.ui.theme.StatusError
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.Typography
import com.example.ui.viewmodel.AppViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val media by viewModel.activeMedia.collectAsState()
    val channels by viewModel.liveChannels.collectAsState()

    val playerManager = remember { PlayerManager(context) }

    val isPlaying by playerManager.isPlaying.collectAsState()
    val isBuffering by playerManager.isBuffering.collectAsState()
    val currentPosition by playerManager.currentPosition.collectAsState()
    val duration by playerManager.duration.collectAsState()
    val errorMessage by playerManager.errorMessage.collectAsState()
    val audioTracks by playerManager.audioTracks.collectAsState()
    val subtitleTracks by playerManager.subtitleTracks.collectAsState()
    val resizeMode by playerManager.resizeMode.collectAsState()
    val playbackSpeed by playerManager.playbackSpeed.collectAsState()

    var showControls by remember { mutableStateOf(true) }
    var isControlsLocked by remember { mutableStateOf(false) }
    var showChannelDrawer by remember { mutableStateOf(false) }
    var showAudioDialog by remember { mutableStateOf(false) }
    var showSubtitleDialog by remember { mutableStateOf(false) }
    var showAspectDialog by remember { mutableStateOf(false) }
    var showSpeedDialog by remember { mutableStateOf(false) }
    var showSleepTimerDialog by remember { mutableStateOf(false) }

    // Volume & Brightness Gesture states
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    var gestureIndicatorText by remember { mutableStateOf<String?>(null) }

    // Keep screen on while player is active
    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            playerManager.release()
        }
    }

    // Play url on change
    LaunchedEffect(media?.streamUrl) {
        media?.streamUrl?.let { url ->
            playerManager.playUrl(url)
        }
    }

    // Auto-hide controls after 4 seconds of inactivity
    LaunchedEffect(showControls, isControlsLocked) {
        if (showControls && !isControlsLocked) {
            delay(4000)
            showControls = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("player_screen")
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if (!isControlsLocked) {
                            showControls = !showControls
                        } else {
                            // If locked, tapping briefly shows the unlock button
                            showControls = true
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                // Swipe gestures for volume, brightness and seek
                detectDragGestures(
                    onDragEnd = {
                        gestureIndicatorText = null
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val isRightSide = change.position.x > size.width / 2
                        if (isRightSide) {
                            // Volume control
                            val currentVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                            val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                            val delta = if (dragAmount.y < 0) 1 else -1
                            val newVol = (currentVol + delta).coerceIn(0, maxVol)
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVol, 0)
                            gestureIndicatorText = "الصوت: ${(newVol.toFloat() / maxVol * 100).toInt()}%"
                        } else {
                            // Brightness control
                            val window = (context as? Activity)?.window
                            val params = window?.attributes
                            val currentBrightness = if (params?.screenBrightness ?: -1f < 0f) 0.5f else params?.screenBrightness ?: 0.5f
                            val newBrightness = (currentBrightness - (dragAmount.y / size.height)).coerceIn(0.05f, 1f)
                            params?.screenBrightness = newBrightness
                            window?.attributes = params
                            gestureIndicatorText = "السطوع: ${(newBrightness * 100).toInt()}%"
                        }
                    }
                )
            }
    ) {
        // ExoPlayer Surface View
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = playerManager.exoPlayer
                    useController = false
                    keepScreenOn = true
                }
            },
            update = { playerView ->
                playerView.resizeMode = when (resizeMode) {
                    ResizeMode.FIT -> androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
                    ResizeMode.FILL -> androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FILL
                    ResizeMode.ZOOM -> androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    ResizeMode.FIXED_16_9 -> androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
                    ResizeMode.FIXED_4_3 -> androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Buffering Spinner
        if (isBuffering) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = GoldPrimary,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(56.dp)
                )
            }
        }

        // Gesture HUD indicator
        if (gestureIndicatorText != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xCC0E1422))
                    .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = Dimens.Space24, vertical = Dimens.Space12)
            ) {
                Text(
                    text = gestureIndicatorText ?: "",
                    style = Typography.titleMedium.copy(color = GoldPrimary, fontWeight = FontWeight.Bold)
                )
            }
        }

        // Error message banner
        if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkNavyCard)
                    .border(1.dp, StatusError, RoundedCornerShape(8.dp))
                    .padding(horizontal = Dimens.Space16, vertical = Dimens.Space8)
            ) {
                Text(
                    text = errorMessage ?: "",
                    style = Typography.bodyMedium.copy(color = StatusError)
                )
            }
        }

        // Controls Overlay
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xB3000000),
                                Color.Transparent,
                                Color(0xCC000000)
                            )
                        )
                    )
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(horizontal = Dimens.Space20, vertical = Dimens.Space16),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.navigateBack() },
                            modifier = Modifier
                                .size(Dimens.MinTouchTarget)
                                .clip(CircleShape)
                                .background(Color(0x660E1422))
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.width(Dimens.Space12))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (media?.isLive == true) {
                                    LiveBadge(modifier = Modifier.padding(end = 8.dp))
                                }
                                Text(
                                    text = media?.title ?: "البث المباشر",
                                    style = Typography.titleLarge.copy(color = TextPrimary, fontWeight = FontWeight.Bold)
                                )
                            }
                            if (media?.channelNumber != null) {
                                Text(
                                    text = "قناة رقم: ${media?.channelNumber}",
                                    style = Typography.labelSmall.copy(color = GoldPrimary)
                                )
                            }
                        }
                    }

                    // Top Action Icons
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Quick Channel Drawer Toggle
                        if (media?.isLive == true) {
                            IconButton(onClick = { showChannelDrawer = !showChannelDrawer }) {
                                Icon(Icons.Filled.Menu, "Channel Drawer", tint = GoldPrimary)
                            }
                        }

                        // Picture in Picture
                        IconButton(onClick = {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                (context as? Activity)?.enterPictureInPictureMode(
                                    android.app.PictureInPictureParams.Builder().build()
                                )
                            }
                        }) {
                            Icon(Icons.Filled.PictureInPicture, "PiP", tint = TextPrimary)
                        }

                        // Lock Controls Toggle
                        IconButton(onClick = { isControlsLocked = !isControlsLocked }) {
                            Icon(
                                imageVector = if (isControlsLocked) Icons.Filled.Lock else Icons.Filled.LockOpen,
                                contentDescription = "Lock",
                                tint = if (isControlsLocked) GoldPrimary else TextPrimary
                            )
                        }
                    }
                }

                // Center Controls (Play/Pause, Rewind, Forward)
                if (!isControlsLocked) {
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.Space24),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Rewind 10s
                        IconButton(
                            onClick = { playerManager.seekRewind(10000L) },
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(Color(0x660E1422))
                                .border(1.dp, GlassBorder, CircleShape)
                        ) {
                            Icon(Icons.Filled.Replay10, "Rewind 10s", tint = TextPrimary, modifier = Modifier.size(28.dp))
                        }

                        // Big Play / Pause
                        IconButton(
                            onClick = { playerManager.togglePlayPause() },
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = DarkNavyBg,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        // Forward 10s
                        IconButton(
                            onClick = { playerManager.seekForward(10000L) },
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(Color(0x660E1422))
                                .border(1.dp, GlassBorder, CircleShape)
                        ) {
                            Icon(Icons.Filled.Forward10, "Forward 10s", tint = TextPrimary, modifier = Modifier.size(28.dp))
                        }
                    }
                }

                // Bottom Controls
                if (!isControlsLocked) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(horizontal = Dimens.Space20, vertical = Dimens.Space16)
                    ) {
                        // Progress bar (if not live or has duration)
                        if (duration > 0 && media?.isLive != true) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = formatTime(currentPosition),
                                    style = Typography.labelSmall.copy(color = TextSecondary)
                                )

                                Slider(
                                    value = currentPosition.toFloat(),
                                    onValueChange = { playerManager.seekTo(it.toLong()) },
                                    valueRange = 0f..duration.toFloat(),
                                    colors = SliderDefaults.colors(
                                        thumbColor = GoldPrimary,
                                        activeTrackColor = GoldPrimary,
                                        inactiveTrackColor = Color(0x66FFFFFF)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = Dimens.Space12)
                                )

                                Text(
                                    text = formatTime(duration),
                                    style = Typography.labelSmall.copy(color = TextSecondary)
                                )
                            }
                        }

                        // Bottom Actions (Audio, Subtitles, Aspect Ratio, Speed, Sleep Timer)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { showAudioDialog = true }) {
                                Icon(Icons.Filled.Audiotrack, "Audio", tint = TextPrimary)
                            }

                            IconButton(onClick = { showSubtitleDialog = true }) {
                                Icon(Icons.Filled.Subtitles, "Subtitles", tint = TextPrimary)
                            }

                            IconButton(onClick = { showAspectDialog = true }) {
                                Icon(Icons.Filled.AspectRatio, "Aspect Ratio", tint = TextPrimary)
                            }

                            IconButton(onClick = { showSpeedDialog = true }) {
                                Icon(Icons.Filled.Speed, "Playback Speed", tint = TextPrimary)
                            }

                            IconButton(onClick = { showSleepTimerDialog = true }) {
                                Icon(Icons.Filled.Timer, "Sleep Timer", tint = TextPrimary)
                            }
                        }
                    }
                }
            }
        }

        // Side Quick-Channel Drawer for Fast Zapping
        AnimatedVisibility(
            visible = showChannelDrawer,
            enter = slideInHorizontally { -it },
            exit = slideOutHorizontally { -it },
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(320.dp)
                    .background(Color(0xF00B0F1A))
                    .border(1.dp, GlassBorder)
                    .padding(Dimens.Space16)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "قائمة القنوات السريعة",
                            style = Typography.titleMedium.copy(color = GoldPrimary, fontWeight = FontWeight.Bold)
                        )
                        IconButton(onClick = { showChannelDrawer = false }) {
                            Icon(Icons.Filled.ArrowBack, "Close Drawer", tint = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(Dimens.Space12))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(Dimens.Space8),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(channels) { channel ->
                            ChannelCard(
                                channelNumber = channel.num,
                                name = channel.name,
                                logoUrl = channel.streamIcon,
                                currentProgram = null,
                                isFavorite = channel.isFavorite,
                                onClick = {
                                    viewModel.playChannel(channel)
                                    showChannelDrawer = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Audio Track Dialog
        if (showAudioDialog) {
            AlertDialog(
                onDismissRequest = { showAudioDialog = false },
                containerColor = DarkNavyCard,
                title = { Text("المسار الصوتي", color = GoldPrimary) },
                text = {
                    Column {
                        if (audioTracks.isEmpty()) {
                            Text("المسار الصوتي الافتراضي فقط", color = TextSecondary)
                        } else {
                            audioTracks.forEach { track ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            playerManager.selectAudioTrack(track)
                                            showAudioDialog = false
                                        }
                                        .padding(vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = track.label,
                                        color = if (track.isSelected) GoldPrimary else TextPrimary,
                                        fontWeight = if (track.isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAudioDialog = false }) { Text("إغلاق", color = GoldPrimary) }
                }
            )
        }

        // Subtitles Dialog
        if (showSubtitleDialog) {
            AlertDialog(
                onDismissRequest = { showSubtitleDialog = false },
                containerColor = DarkNavyCard,
                title = { Text("الترجمة والشرح", color = GoldPrimary) },
                text = {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    playerManager.selectSubtitleTrack(null)
                                    showSubtitleDialog = false
                                }
                                .padding(vertical = 10.dp)
                        ) {
                            Text("إيقاف الترجمة (Off)", color = TextPrimary)
                        }

                        subtitleTracks.forEach { track ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        playerManager.selectSubtitleTrack(track)
                                        showSubtitleDialog = false
                                    }
                                    .padding(vertical = 10.dp)
                            ) {
                                Text(
                                    text = track.label,
                                    color = if (track.isSelected) GoldPrimary else TextPrimary,
                                    fontWeight = if (track.isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSubtitleDialog = false }) { Text("إغلاق", color = GoldPrimary) }
                }
            )
        }

        // Aspect Ratio Dialog
        if (showAspectDialog) {
            AlertDialog(
                onDismissRequest = { showAspectDialog = false },
                containerColor = DarkNavyCard,
                title = { Text("نسبة العرض والشاشة", color = GoldPrimary) },
                text = {
                    Column {
                        listOf(
                            ResizeMode.FIT to "ملاءمة الشاشة (Fit)",
                            ResizeMode.FILL to "ملء كامل الشاشة (Fill)",
                            ResizeMode.ZOOM to "تكبير وقص الأطراف (Zoom)",
                            ResizeMode.FIXED_16_9 to "شاشة عريضة (16:9)",
                            ResizeMode.FIXED_4_3 to "شاشة تقليدية (4:3)"
                        ).forEach { (mode, title) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        playerManager.setResizeMode(mode)
                                        showAspectDialog = false
                                    }
                                    .padding(vertical = 10.dp)
                            ) {
                                Text(
                                    text = title,
                                    color = if (resizeMode == mode) GoldPrimary else TextPrimary,
                                    fontWeight = if (resizeMode == mode) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAspectDialog = false }) { Text("إغلاق", color = GoldPrimary) }
                }
            )
        }

        // Speed Dialog
        if (showSpeedDialog) {
            AlertDialog(
                onDismissRequest = { showSpeedDialog = false },
                containerColor = DarkNavyCard,
                title = { Text("سرعة التشغيل", color = GoldPrimary) },
                text = {
                    Column {
                        listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        playerManager.setPlaybackSpeed(speed)
                                        showSpeedDialog = false
                                    }
                                    .padding(vertical = 10.dp)
                            ) {
                                Text(
                                    text = "${speed}x",
                                    color = if (playbackSpeed == speed) GoldPrimary else TextPrimary,
                                    fontWeight = if (playbackSpeed == speed) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSpeedDialog = false }) { Text("إغلاق", color = GoldPrimary) }
                }
            )
        }

        // Sleep Timer Dialog
        if (showSleepTimerDialog) {
            AlertDialog(
                onDismissRequest = { showSleepTimerDialog = false },
                containerColor = DarkNavyCard,
                title = { Text("مؤقت النوم", color = GoldPrimary) },
                text = {
                    Column {
                        listOf(15, 30, 45, 60, 90, 120).forEach { minutes ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        scope.launch {
                                            delay(minutes * 60 * 1000L)
                                            playerManager.exoPlayer.pause()
                                        }
                                        showSleepTimerDialog = false
                                    }
                                    .padding(vertical = 10.dp)
                            ) {
                                Text(
                                    text = "إيقاف بعد $minutes دقيقة",
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSleepTimerDialog = false }) { Text("إلغاء", color = TextSecondary) }
                }
            )
        }
    }
}

private fun formatTime(millis: Long): String {
    val totalSeconds = (millis / 1000).toInt()
    val seconds = totalSeconds % 60
    val minutes = (totalSeconds / 60) % 60
    val hours = totalSeconds / 3600
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
