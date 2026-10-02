package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.components.PrimaryButton
import com.example.ui.components.QualityBadge
import com.example.ui.components.SecondaryButton
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

@Composable
fun MovieDetailScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val movie = viewModel.selectedMovie.collectAsState().value
    val scrollState = rememberScrollState()

    if (movie == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(DarkNavyBg),
            contentAlignment = Alignment.Center
        ) {
            Text("لم يتم العثور على الفيلم", color = TextSecondary)
        }
        return
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .testTag("movie_detail_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Backdrop Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
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
                                    Color(0x660B0F1A),
                                    Color(0xCC0B0F1A),
                                    DarkNavyBg
                                )
                            )
                        )
                )

                // Back Button
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

            // Info Content
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
                    QualityBadge(quality = "4K ULTRA HD")

                    if (movie.rating > 0.0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = StatusWarning,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = String.format("%.1f", movie.rating),
                                style = Typography.titleMedium.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.Space12))

                Text(
                    text = movie.name,
                    style = Typography.displaySmall.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(Dimens.Space8))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!movie.year.isNullOrBlank()) {
                        Text(
                            text = movie.year,
                            style = Typography.bodyMedium.copy(color = TextSecondary)
                        )
                        Spacer(modifier = Modifier.width(Dimens.Space12))
                    }

                    if (!movie.genre.isNullOrBlank()) {
                        Text(
                            text = "•  ${movie.genre}",
                            style = Typography.bodyMedium.copy(color = GoldPrimary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.Space20))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.Space12)
                ) {
                    PrimaryButton(
                        text = "تشغيل الفيلم الآن",
                        icon = Icons.Filled.PlayArrow,
                        onClick = { viewModel.playMovie(movie) },
                        modifier = Modifier.weight(1f),
                        testTag = "play_movie_button"
                    )

                    SecondaryButton(
                        text = if (movie.isFavorite) "في المفضلة" else "إضافة للمفضلة",
                        icon = if (movie.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        onClick = { viewModel.toggleMovieFavorite(movie) },
                        modifier = Modifier.weight(1f),
                        testTag = "fav_movie_button"
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.Space24))

                // Synopsis
                Text(
                    text = "نبذة عن الفيلم (Storyline)",
                    style = Typography.titleMedium.copy(color = GoldPrimary, fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(Dimens.Space8))

                Text(
                    text = movie.plot ?: "استمتع بمشاهدة هذا الفيلم الرائع بأعلى دقة متوفرة مع صوت نقي وترجمة متزامنة.",
                    style = Typography.bodyLarge.copy(color = TextSecondary, lineHeight = 24.sp)
                )

                Spacer(modifier = Modifier.height(Dimens.Space48))
            }
        }
    }
}
