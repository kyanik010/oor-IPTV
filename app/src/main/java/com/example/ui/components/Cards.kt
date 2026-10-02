package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavyCardHover
import com.example.ui.theme.Dimens
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.Shapes
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.TicketShape
import com.example.ui.theme.Typography

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = Shapes.medium,
    onClick: (() -> Unit)? = null,
    testTag: String = "glass_card",
    content: @Composable () -> Unit
) {
    val baseModifier = modifier
        .testTag(testTag)
        .tvFocusable(shape = shape, onEnterClick = onClick)
        .clip(shape)
        .background(DarkNavyCard)
        .border(width = 1.dp, color = GlassBorder, shape = shape)
        .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)

    Box(modifier = baseModifier) {
        content()
    }
}

@Composable
fun TicketCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(TicketShape)
            .background(DarkNavyCard)
            .border(width = 1.5.dp, color = GlassBorder, shape = TicketShape)
            .padding(Dimens.Space24)
    ) {
        content()
    }
}

@Composable
fun ChannelCard(
    channelNumber: Int,
    name: String,
    logoUrl: String?,
    currentProgram: String?,
    progress: Float = 0f,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit = {},
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "channel_card"
) {
    Box(
        modifier = modifier
            .testTag(testTag)
            .fillMaxWidth()
            .height(Dimens.ChannelCardHeight)
            .tvFocusable(shape = Shapes.medium, onEnterClick = onClick)
            .clip(Shapes.medium)
            .background(DarkNavyCard)
            .border(width = 1.dp, color = GlassBorderSubtle, shape = Shapes.medium)
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.Space12, vertical = Dimens.Space8),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Channel Number
            Text(
                text = String.format("%03d", channelNumber),
                style = Typography.labelMedium.copy(
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.width(36.dp)
            )

            // Channel Logo / Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkNavyBg)
                    .border(width = 0.5.dp, color = GlassBorder, shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (!logoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = logoUrl,
                        contentDescription = name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.LiveTv,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(Dimens.Space12))

            // Channel Name and Current Program EPG
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = name,
                    style = Typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (!currentProgram.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = currentProgram,
                        style = Typography.bodyMedium.copy(color = TextSecondary),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (progress in 0.01f..0.99f) {
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = GoldPrimary,
                            trackColor = DarkNavyBg,
                        )
                    }
                }
            }

            // Favorite star button
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier.size(Dimens.MinTouchTarget)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) StatusWarning else TextTertiary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
fun PosterCard(
    title: String,
    posterUrl: String?,
    rating: Double? = null,
    quality: String = "HD",
    year: String? = null,
    progress: Float? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "poster_card"
) {
    Column(
        modifier = modifier
            .width(Dimens.PosterWidth)
            .tvFocusable(shape = Shapes.medium, onEnterClick = onClick)
            .clip(Shapes.medium)
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .clip(Shapes.medium)
                .background(DarkNavyCard)
                .border(width = 1.dp, color = GlassBorder, shape = Shapes.medium)
        ) {
            if (!posterUrl.isNullOrBlank()) {
                AsyncImage(
                    model = posterUrl,
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Movie,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            // Top Badges (Quality & Rating)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.Space8),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                QualityBadge(quality = quality)

                if (rating != null && rating > 0.0) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xCC0E1422))
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = StatusWarning,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = String.format("%.1f", rating),
                            style = Typography.labelSmall.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // Bottom Progress bar for Continue Watching
            if (progress != null && progress > 0f) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .height(4.dp),
                    color = GoldPrimary,
                    trackColor = DarkNavyBg
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.Space8))

        Text(
            text = title,
            style = Typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (!year.isNullOrBlank()) {
            Text(
                text = year,
                style = Typography.labelSmall.copy(color = TextSecondary),
                maxLines = 1
            )
        }
    }
}
