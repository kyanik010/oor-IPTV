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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.AppTextField
import com.example.ui.components.CategoryChip
import com.example.ui.components.PosterCard
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.Dimens
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.Typography
import com.example.ui.viewmodel.AppViewModel

@Composable
fun MoviesScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val categories by viewModel.movieCategories.collectAsState()
    val selectedCategory by viewModel.selectedMovieCategory.collectAsState()
    val movies by viewModel.movies.collectAsState()
    val topRatedMovies by viewModel.topRatedMovies.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(categories) {
        if (selectedCategory == null && categories.isNotEmpty()) {
            viewModel.selectMovieCategory(categories.first())
        }
    }

    val displayMovies = remember(movies, topRatedMovies, selectedCategory, searchQuery) {
        val base = if (movies.isNotEmpty()) movies else topRatedMovies
        if (searchQuery.isBlank()) {
            base
        } else {
            base.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .testTag("movies_screen")
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
                text = "مكتبة الأفلام (Movies VOD)",
                style = Typography.titleLarge.copy(color = GoldPrimary, fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
        }

        // Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.Space16, vertical = Dimens.Space4)
        ) {
            AppTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = "البحث في الأفلام...",
                leadingIcon = Icons.Filled.Search,
                testTag = "movie_search_input"
            )
        }

        // Category Chips
        if (categories.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = Dimens.Space16, vertical = Dimens.Space8),
                horizontalArrangement = Arrangement.spacedBy(Dimens.Space8)
            ) {
                items(categories) { category ->
                    CategoryChip(
                        text = category.categoryName,
                        isSelected = selectedCategory?.categoryId == category.categoryId,
                        onClick = { viewModel.selectMovieCategory(category) }
                    )
                }
            }
        }

        // Movies Grid
        if (displayMovies.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Dimens.Space24),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Movie,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(Dimens.Space12))
                    Text(
                        text = "لا توجد أفلام متاحة في هذا التصنيف",
                        style = Typography.bodyMedium.copy(color = TextSecondary)
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 130.dp),
                contentPadding = PaddingValues(
                    start = Dimens.Space16,
                    end = Dimens.Space16,
                    top = Dimens.Space8,
                    bottom = Dimens.Space48
                ),
                horizontalArrangement = Arrangement.spacedBy(Dimens.Space12),
                verticalArrangement = Arrangement.spacedBy(Dimens.Space16),
                modifier = Modifier.fillMaxSize()
            ) {
                items(displayMovies) { movie ->
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
    }
}
