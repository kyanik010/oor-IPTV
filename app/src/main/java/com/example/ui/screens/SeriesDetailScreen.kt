package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.model.EpisodeEntity
import com.example.ui.components.CategoryChip
import com.example.ui.components.SecondaryButton
import com.example.ui.components.tvFocusable
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.Dimens
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.Shapes
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.Typography
import com.example.ui.viewmodel.AppViewModel

@Composable
fun SeriesDetailScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val series = viewModel.selectedSeries.collectAsState().value
    var selectedSeason by remember { mutableIntStateOf(1) }
    var episodes by remember { mutableStateOf<List<EpisodeEntity>>(emptyList()) }

    if (series == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(DarkNavyBg),
            contentAlignment = Alignment.Center
        ) {
            Text("لم يتم العثور على المسلسل", color = TextSecondary)
        }
        return
    }

    LaunchedEffect(series.seriesId, selectedSeason) {
        viewModel.repository.getEpisodes(series.seriesId, selectedSeason).collect {
            episodes = it
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .testTag("series_detail_screen")
    ) {
        // Backdrop & Poster Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                AsyncImage(
                    model = series.cover,
                    contentDescription = series.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0x660B0F1A), Color(0xCC0B0F1A), DarkNavyBg)
                            )
                        )
                )

                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier
                        .padding(start = Dimens.Space16, top = Dimens.Space24)
                        .size(Dimens.MinTouchTarget)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x990E1422))
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
            }
        }

        // Details
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.Space20)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = series.name,
                        style = Typography.displaySmall.copy(color = TextPrimary, fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f)
                    )

                    if (series.rating > 0.0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = StatusWarning,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = String.format("%.1f", series.rating),
                                style = Typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.Space8))

                Row {
                    if (!series.year.isNullOrBlank()) {
                        Text(series.year, style = Typography.bodyMedium.copy(color = TextSecondary))
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                    if (!series.genre.isNullOrBlank()) {
                        Text("• ${series.genre}", style = Typography.bodyMedium.copy(color = GoldPrimary))
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.Space12))

                Text(
                    text = series.plot ?: "تابع جميع حلقات ومواسم هذا المسلسل المشوق بجودة فائقة.",
                    style = Typography.bodyMedium.copy(color = TextSecondary)
                )

                Spacer(modifier = Modifier.height(Dimens.Space20))

                SecondaryButton(
                    text = if (series.isFavorite) "في المفضلة" else "إضافة إلى المفضلة",
                    icon = if (series.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    onClick = { viewModel.toggleSeriesFavorite(series) },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "fav_series_button"
                )

                Spacer(modifier = Modifier.height(Dimens.Space24))

                // Seasons Tab Selector
                Text(
                    text = "المواسم والحلقات",
                    style = Typography.titleMedium.copy(color = GoldPrimary, fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(Dimens.Space12))

                Row(horizontalArrangement = Arrangement.spacedBy(Dimens.Space8)) {
                    listOf(1, 2, 3).forEach { seasonNum ->
                        CategoryChip(
                            text = "الموسم $seasonNum",
                            isSelected = selectedSeason == seasonNum,
                            onClick = { selectedSeason = seasonNum }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.Space16))
            }
        }

        // Episodes list
        if (episodes.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.Space24),
                    contentAlignment = Alignment.Center
                ) {
                    Text("لا توجد حلقات متاحة لهذا الموسم حالياً", color = TextSecondary)
                }
            }
        } else {
            items(episodes) { episode ->
                EpisodeItem(
                    episode = episode,
                    onClick = { viewModel.playEpisode(episode, series.name) },
                    modifier = Modifier.padding(horizontal = Dimens.Space20, vertical = Dimens.Space8)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(Dimens.Space48))
        }
    }
}

@Composable
fun EpisodeItem(
    episode: EpisodeEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .tvFocusable(shape = Shapes.medium, onEnterClick = onClick)
            .clip(Shapes.medium)
            .background(DarkNavyCard)
            .border(1.dp, GlassBorder, Shapes.medium)
            .clickable(onClick = onClick)
            .padding(Dimens.Space16),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(Shapes.small)
                    .background(DarkNavySurface)
                    .border(1.dp, GlassBorder, Shapes.small),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Play",
                    tint = GoldPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(Dimens.Space16))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "الحلقة ${episode.episodeNum}: ${episode.title}",
                    style = Typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!episode.plot.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(Dimens.Space2))
                    Text(
                        text = episode.plot,
                        style = Typography.bodySmall.copy(color = TextSecondary),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (episode.durationSecs > 0) {
                Text(
                    text = "${episode.durationSecs / 60} دقيقة",
                    style = Typography.labelSmall.copy(color = TextSecondary),
                    modifier = Modifier.padding(start = Dimens.Space8)
                )
            }
        }
    }
}
