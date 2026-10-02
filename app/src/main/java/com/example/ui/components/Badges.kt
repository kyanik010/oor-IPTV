package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavyCardHover
import com.example.ui.theme.Dimens
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.PillShape
import com.example.ui.theme.Shapes
import com.example.ui.theme.StatusLive
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.Typography

@Composable
fun LiveBadge(
    modifier: Modifier = Modifier,
    text: String = "LIVE"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "live_pulse_alpha"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(StatusLive)
            .padding(horizontal = Dimens.Space8, vertical = Dimens.Space2),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .alpha(alpha)
                    .background(Color.White, shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = Typography.labelSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun QualityBadge(
    quality: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xCC0E1422))
            .border(width = 0.5.dp, color = GoldPrimary, shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = quality,
            style = Typography.labelSmall.copy(
                color = GoldPrimary,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun CategoryChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .tvFocusable(shape = PillShape, onEnterClick = onClick)
            .clip(PillShape)
            .background(if (isSelected) GoldPrimary else DarkNavyCard)
            .border(
                width = 1.dp,
                color = if (isSelected) GoldPrimary else GlassBorder,
                shape = PillShape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.Space16, vertical = Dimens.Space8),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = Typography.labelMedium.copy(
                color = if (isSelected) DarkNavyCard else TextPrimary,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        )
    }
}
