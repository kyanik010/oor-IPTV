package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.model.ChannelEntity
import com.example.ui.components.tvFocusable
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.Dimens
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.Shapes
import com.example.ui.theme.StatusLive
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.Typography
import com.example.ui.viewmodel.AppViewModel

@Composable
fun EpgScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val channels by viewModel.liveChannels.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .testTag("epg_screen")
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.Space16, vertical = Dimens.Space12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateBack() },
                modifier = Modifier.size(Dimens.MinTouchTarget)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            Spacer(modifier = Modifier.width(Dimens.Space8))

            Text(
                text = "دليل البرامج الإلكتروني (EPG Guide)",
                style = Typography.titleLarge.copy(color = GoldPrimary, fontWeight = FontWeight.Bold)
            )
        }

        // Timeline header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkNavySurface)
                .padding(horizontal = Dimens.Space16, vertical = Dimens.Space8),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(StatusLive)
            )
            Spacer(modifier = Modifier.width(Dimens.Space8))
            Text(
                text = "البرامج الحالية والقادمة (الآن)",
                style = Typography.labelMedium.copy(color = GoldPrimary, fontWeight = FontWeight.Bold)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = Dimens.Space16, vertical = Dimens.Space12),
            verticalArrangement = Arrangement.spacedBy(Dimens.Space12)
        ) {
            items(channels) { channel ->
                EpgChannelRow(
                    channel = channel,
                    onPlay = { viewModel.playChannel(channel) }
                )
            }
        }
    }
}

@Composable
fun EpgChannelRow(
    channel: ChannelEntity,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .tvFocusable(shape = Shapes.medium, onEnterClick = onPlay)
            .clip(Shapes.medium)
            .background(DarkNavyCard)
            .border(1.dp, GlassBorder, Shapes.medium)
            .clickable(onClick = onPlay)
            .padding(Dimens.Space12)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Channel Logo
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(DarkNavyBg),
                    contentAlignment = Alignment.Center
                ) {
                    if (!channel.streamIcon.isNullOrBlank()) {
                        AsyncImage(
                            model = channel.streamIcon,
                            contentDescription = channel.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.LiveTv,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(Dimens.Space12))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = channel.name,
                        style = Typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1
                    )
                    Text(
                        text = "البرنامج الحالي: النشرة الإخبارية المباشرة والموجز الرياضي",
                        style = Typography.bodySmall.copy(color = TextSecondary),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = onPlay,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(GoldPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Play",
                        tint = DarkNavyBg,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.Space8))

            // Time timeline bar
            LinearProgressIndicator(
                progress = { 0.45f },
                color = GoldPrimary,
                trackColor = DarkNavyBg,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
            )

            Spacer(modifier = Modifier.height(Dimens.Space4))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "10:00 - 11:30 (متبقي 45 دقيقة)",
                    style = Typography.labelSmall.copy(color = GoldPrimary)
                )
                Text(
                    text = "التالي: الاستوديو التحليلي",
                    style = Typography.labelSmall.copy(color = TextSecondary)
                )
            }
        }
    }
}
