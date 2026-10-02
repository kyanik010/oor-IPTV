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
import androidx.compose.material.icons.filled.Search
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
import com.example.ui.components.AppTextField
import com.example.ui.components.ChannelCard
import com.example.ui.components.PosterCard
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.Dimens
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.Typography
import com.example.ui.viewmodel.AppViewModel

@Composable
fun SearchScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val query by viewModel.searchQuery.collectAsState()
    val results by viewModel.searchResults.collectAsState()
    val (channels, movies, series) = results

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .testTag("search_screen")
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
                text = "البحث الشامل",
                style = Typography.titleLarge.copy(color = GoldPrimary, fontWeight = FontWeight.Bold)
            )
        }

        // Input
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.Space16, vertical = Dimens.Space8)
        ) {
            AppTextField(
                value = query,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = "ابحث عن قناة، فيلم، أو مسلسل...",
                leadingIcon = Icons.Filled.Search,
                testTag = "global_search_input"
            )
        }

        if (query.length < 2) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Dimens.Space24),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "أدخل حرفين على الأقل للبحث السريع في المحتوى",
                    style = Typography.bodyMedium.copy(color = TextSecondary)
                )
            }
        } else if (channels.isEmpty() && movies.isEmpty() && series.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Dimens.Space24),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "لم يتم العثور على أي نتائج مطابقة لـ \"$query\"",
                    style = Typography.bodyMedium.copy(color = TextSecondary)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = Dimens.Space48)
            ) {
                // Channels
                if (channels.isNotEmpty()) {
                    item {
                        SectionTitle(title = "القنوات (${channels.size})")
                    }
                    items(channels) { channel ->
                        ChannelCard(
                            channelNumber = channel.num,
                            name = channel.name,
                            logoUrl = channel.streamIcon,
                            currentProgram = null,
                            isFavorite = channel.isFavorite,
                            onToggleFavorite = { viewModel.toggleChannelFavorite(channel) },
                            onClick = { viewModel.playChannel(channel) },
                            modifier = Modifier.padding(horizontal = Dimens.Space16, vertical = Dimens.Space4)
                        )
                    }
                    item { Spacer(modifier = Modifier.height(Dimens.Space16)) }
                }

                // Movies
                if (movies.isNotEmpty()) {
                    item {
                        SectionTitle(title = "الأفلام (${movies.size})")
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = Dimens.Space16),
                            horizontalArrangement = Arrangement.spacedBy(Dimens.Space12)
                        ) {
                            items(movies) { movie ->
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

                // Series
                if (series.isNotEmpty()) {
                    item {
                        SectionTitle(title = "المسلسلات (${series.size})")
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = Dimens.Space16),
                            horizontalArrangement = Arrangement.spacedBy(Dimens.Space12)
                        ) {
                            items(series) { s ->
                                PosterCard(
                                    title = s.name,
                                    posterUrl = s.cover,
                                    rating = s.rating,
                                    year = s.year,
                                    onClick = { viewModel.openSeriesDetail(s) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
