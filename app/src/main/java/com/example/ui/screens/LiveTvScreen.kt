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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LiveTv
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.CategoryEntity
import com.example.data.model.ContentType
import com.example.ui.components.AppTextField
import com.example.ui.components.CategoryChip
import com.example.ui.components.ChannelCard
import com.example.ui.components.tvFocusable
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.Dimens
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.Shapes
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.Typography
import com.example.ui.viewmodel.AppViewModel

@Composable
fun LiveTvScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val categories by viewModel.liveCategories.collectAsState()
    val selectedCategory by viewModel.selectedLiveCategory.collectAsState()
    val channels by viewModel.liveChannels.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    // Select first category by default
    LaunchedEffect(categories) {
        if (selectedCategory == null && categories.isNotEmpty()) {
            viewModel.selectLiveCategory(categories.first())
        }
    }

    val filteredChannels = remember(channels, searchQuery) {
        if (searchQuery.isBlank()) {
            channels
        } else {
            channels.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .testTag("live_tv_screen")
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
                text = "القنوات المباشرة (Live TV)",
                style = Typography.titleLarge.copy(color = GoldPrimary, fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
        }

        // Search Bar in Channels
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.Space16, vertical = Dimens.Space4)
        ) {
            AppTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = "البحث في القنوات...",
                leadingIcon = Icons.Filled.Search,
                testTag = "channel_search_input"
            )
        }

        // Category Horizontal Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = Dimens.Space16, vertical = Dimens.Space8),
            horizontalArrangement = Arrangement.spacedBy(Dimens.Space8)
        ) {
            items(categories) { category ->
                CategoryChip(
                    text = category.categoryName,
                    isSelected = selectedCategory?.categoryId == category.categoryId,
                    onClick = { viewModel.selectLiveCategory(category) }
                )
            }
        }

        // Channels List
        if (filteredChannels.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Dimens.Space24),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.LiveTv,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(Dimens.Space12))
                    Text(
                        text = "لا توجد قنوات متاحة في هذا التصنيف",
                        style = Typography.bodyMedium.copy(color = TextSecondary)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Dimens.Space16),
                verticalArrangement = Arrangement.spacedBy(Dimens.Space8),
                contentPadding = PaddingValues(top = Dimens.Space8, bottom = Dimens.Space48)
            ) {
                items(filteredChannels) { channel ->
                    ChannelCard(
                        channelNumber = channel.num,
                        name = channel.name,
                        logoUrl = channel.streamIcon,
                        currentProgram = "بث مباشر عالي الدقة",
                        isFavorite = channel.isFavorite,
                        onToggleFavorite = { viewModel.toggleChannelFavorite(channel) },
                        onClick = { viewModel.playChannel(channel) }
                    )
                }
            }
        }
    }
}
