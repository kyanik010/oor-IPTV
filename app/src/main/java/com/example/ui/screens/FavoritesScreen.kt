package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.ChannelCard
import com.example.ui.components.PosterCard
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.Dimens
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.Typography
import com.example.ui.viewmodel.AppViewModel

@Composable
fun FavoritesScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val favoriteChannels by viewModel.favoriteChannels.collectAsState()
    val topRatedMovies by viewModel.topRatedMovies.collectAsState()
    val topRatedSeries by viewModel.topRatedSeries.collectAsState()

    val favoriteMovies = topRatedMovies.filter { it.isFavorite }
    val favoriteSeries = topRatedSeries.filter { it.isFavorite }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .testTag("favorites_screen")
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
                text = "المفضلة (Favorites)",
                style = Typography.titleLarge.copy(color = GoldPrimary, fontWeight = FontWeight.Bold)
            )
        }

        if (favoriteChannels.isEmpty() && favoriteMovies.isEmpty() && favoriteSeries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Dimens.Space24),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = StatusWarning,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(Dimens.Space12))
                    Text(
                        text = "قائمتك المفضلة فارغة حالياً",
                        style = Typography.bodyMedium.copy(color = TextSecondary)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = Dimens.Space48)
            ) {
                if (favoriteChannels.isNotEmpty()) {
                    item {
                        SectionTitle(title = "القنوات المفضلة (${favoriteChannels.size})")
                    }
                    items(favoriteChannels) { channel ->
                        ChannelCard(
                            channelNumber = channel.num,
                            name = channel.name,
                            logoUrl = channel.streamIcon,
                            currentProgram = null,
                            isFavorite = true,
                            onToggleFavorite = { viewModel.toggleChannelFavorite(channel) },
                            onClick = { viewModel.playChannel(channel) },
                            modifier = Modifier.padding(horizontal = Dimens.Space16, vertical = Dimens.Space4)
                        )
                    }
                    item { Spacer(modifier = Modifier.height(Dimens.Space16)) }
                }

                if (favoriteMovies.isNotEmpty()) {
                    item {
                        SectionTitle(title = "الأفلام المفضلة (${favoriteMovies.size})")
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = Dimens.Space16),
                            horizontalArrangement = Arrangement.spacedBy(Dimens.Space12)
                        ) {
                            items(favoriteMovies) { movie ->
                                PosterCard(
                                    title = movie.name,
                                    posterUrl = movie.streamIcon,
                                    rating = movie.rating,
                                    year = movie.year,
                                    onClick = { viewModel.openMovieDetail(movie) }
                                )
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(Dimens.Space16)) }
                }

                if (favoriteSeries.isNotEmpty()) {
                    item {
                        SectionTitle(title = "المسلسلات المفضلة (${favoriteSeries.size})")
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = Dimens.Space16),
                            horizontalArrangement = Arrangement.spacedBy(Dimens.Space12)
                        ) {
                            items(favoriteSeries) { series ->
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
    }
}
