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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.model.MovieEntity
import com.example.ui.components.ChannelCard
import com.example.ui.components.PosterCard
import com.example.ui.components.PrimaryButton
import com.example.ui.components.SecondaryButton
import com.example.ui.components.tvFocusable
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.Dimens
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.Shapes
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.Typography
import com.example.ui.viewmodel.AppViewModel
import com.example.ui.viewmodel.ScreenRoute

@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val selectedProfile by viewModel.selectedProfile.collectAsState()
    val favoriteChannels by viewModel.favoriteChannels.collectAsState()
    val topRatedMovies by viewModel.topRatedMovies.collectAsState()
    val topRatedSeries by viewModel.topRatedSeries.collectAsState()
    val recentProgress by viewModel.recentProgress.collectAsState()

    val featuredMovie = topRatedMovies.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = Dimens.Space64)
    ) {
        // Top App Bar / Profile Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.Space20, vertical = Dimens.Space16),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkNavyCard)
                            .border(1.dp, GlassBorder, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Tv,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(Dimens.Space12))

                    Column {
                        Text(
                            text = "نور IPTV",
                            style = Typography.titleLarge.copy(color = GoldPrimary, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "مرحباً، ${selectedProfile?.name ?: "المستخدم"}",
                            style = Typography.bodyMedium.copy(color = TextSecondary)
                        )
                    }
                }

                Row {
                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenRoute.Search) },
                        modifier = Modifier.size(Dimens.MinTouchTarget)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = TextPrimary
                        )
                    }

                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenRoute.Settings) },
                        modifier = Modifier.size(Dimens.MinTouchTarget)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                            tint = TextPrimary
                        )
                    }
                }
            }
        }

        // Hero Spotlight Banner
        if (featuredMovie != null) {
            item {
                HeroBanner(
                    movie = featuredMovie,
                    onPlayClick = { viewModel.playMovie(featuredMovie) },
                    onDetailsClick = { viewModel.openMovieDetail(featuredMovie) }
                )
            }
        }

        // Quick Category Nav Shortcuts
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.Space20, vertical = Dimens.Space16),
                horizontalArrangement = Arrangement.spacedBy(Dimens.Space12)
            ) {
                QuickNavCard(
                    title = "القنوات المباشرة",
                    icon = Icons.Filled.LiveTv,
                    onClick = { viewModel.navigateTo(ScreenRoute.LiveTv) },
                    modifier = Modifier.weight(1f)
                )
                QuickNavCard(
                    title = "الأفلام (VOD)",
                    icon = Icons.Filled.Movie,
                    onClick = { viewModel.navigateTo(ScreenRoute.Movies) },
                    modifier = Modifier.weight(1f)
                )
                QuickNavCard(
                    title = "المسلسلات",
                    icon = Icons.Filled.Tv,
                    onClick = { viewModel.navigateTo(ScreenRoute.Series) },
                    modifier = Modifier.weight(1f)
                )
                QuickNavCard(
                    title = "دليل البرامج",
                    icon = Icons.Filled.CalendarMonth,
                    onClick = { viewModel.navigateTo(ScreenRoute.Epg) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Continue Watching Row (if any)
        if (recentProgress.isNotEmpty()) {
            item {
                SectionTitle(title = "متابعة المشاهدة")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = Dimens.Space20),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.Space16)
                ) {
                    items(recentProgress) { item ->
                        val progressPercent = if (item.durationMs > 0) item.positionMs.toFloat() / item.durationMs else 0.5f
                        PosterCard(
                            title = item.title,
                            posterUrl = item.posterUrl,
                            progress = progressPercent,
                            onClick = {
                                // Resume
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(Dimens.Space24))
            }
        }

        // Favorite Channels Row
        if (favoriteChannels.isNotEmpty()) {
            item {
                SectionTitle(title = "القنوات المفضلة")
                Column(
                    modifier = Modifier.padding(horizontal = Dimens.Space20),
                    verticalArrangement = Arrangement.spacedBy(Dimens.Space8)
                ) {
                    favoriteChannels.take(4).forEach { channel ->
                        ChannelCard(
                            channelNumber = channel.num,
                            name = channel.name,
                            logoUrl = channel.streamIcon,
                            currentProgram = "البث المباشر المتاح على مدار الساعة",
                            isFavorite = true,
                            onToggleFavorite = { viewModel.toggleChannelFavorite(channel) },
                            onClick = { viewModel.playChannel(channel) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(Dimens.Space24))
            }
        }

        // Top Rated Movies Row
        if (topRatedMovies.isNotEmpty()) {
            item {
                SectionTitle(title = "أفلام ننصح بها")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = Dimens.Space20),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.Space16)
                ) {
                    items(topRatedMovies) { movie ->
                        PosterCard(
                            title = movie.name,
                            posterUrl = movie.streamIcon,
                            rating = movie.rating,
                            year = movie.year,
                            onClick = { viewModel.openMovieDetail(movie) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(Dimens.Space24))
            }
        }

        // Top Rated Series Row
        if (topRatedSeries.isNotEmpty()) {
            item {
                SectionTitle(title = "أبرز المسلسلات")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = Dimens.Space20),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.Space16)
                ) {
                    items(topRatedSeries) { series ->
                        PosterCard(
                            title = series.name,
                            posterUrl = series.cover,
                            rating = series.rating,
                            year = series.year,
                            onClick = { viewModel.openSeriesDetail(series) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HeroBanner(
    movie: MovieEntity,
    onPlayClick: () -> Unit,
    onDetailsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.Space20)
            .height(240.dp)
            .clip(Shapes.large)
            .border(1.dp, GlassBorder, Shapes.large)
    ) {
        AsyncImage(
            model = movie.streamIcon,
            contentDescription = movie.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x990B0F1A),
                            Color(0xFA0B0F1A)
                        )
                    )
                )
        )

        // Text & Action Buttons
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.Space20),
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(GoldPrimary)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "مميز",
                        style = Typography.labelSmall.copy(color = DarkNavyBg, fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(modifier = Modifier.width(Dimens.Space8))
                Text(
                    text = "⭐ ${movie.rating}",
                    style = Typography.labelSmall.copy(color = StatusWarning, fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(Dimens.Space4))

            Text(
                text = movie.name,
                style = Typography.headlineMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold),
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(Dimens.Space12))

            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.Space12)) {
                PrimaryButton(
                    text = "تشغيل الآن",
                    icon = Icons.Filled.PlayArrow,
                    onClick = onPlayClick,
                    modifier = Modifier.height(44.dp)
                )
                SecondaryButton(
                    text = "التفاصيل",
                    icon = Icons.Filled.Info,
                    onClick = onDetailsClick,
                    modifier = Modifier.height(44.dp)
                )
            }
        }
    }
}

@Composable
fun QuickNavCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .tvFocusable(shape = Shapes.medium, onEnterClick = onClick)
            .clip(Shapes.medium)
            .background(DarkNavyCard)
            .border(1.dp, GlassBorder, Shapes.medium)
            .clickable(onClick = onClick)
            .padding(vertical = Dimens.Space12, horizontal = Dimens.Space8),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = GoldPrimary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(Dimens.Space8))
        Text(
            text = title,
            style = Typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            maxLines = 1
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = Typography.titleLarge.copy(color = TextPrimary, fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(start = Dimens.Space20, end = Dimens.Space20, bottom = Dimens.Space12)
    )
}
