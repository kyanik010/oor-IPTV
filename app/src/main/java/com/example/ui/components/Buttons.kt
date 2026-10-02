package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavyCardHover
import com.example.ui.theme.Dimens
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GoldGradient
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.Shapes
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.Typography

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    shape: Shape = Shapes.medium,
    testTag: String = "primary_button"
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .testTag(testTag)
            .height(Dimens.ButtonHeight)
            .defaultMinSize(minWidth = Dimens.MinTouchTarget, minHeight = Dimens.MinTouchTarget)
            .tvFocusable(shape = shape, onEnterClick = if (enabled && !isLoading) onClick else null)
            .clip(shape)
            .background(if (enabled) GoldGradient else GoldGradient)
            .clickable(
                enabled = enabled && !isLoading,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = Dimens.Space20),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = DarkNavyBg,
                strokeWidth = 2.5.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = DarkNavyBg,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = Dimens.Space8)
                    )
                }
                Text(
                    text = text,
                    style = Typography.labelLarge.copy(
                        color = DarkNavyBg,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    shape: Shape = Shapes.medium,
    testTag: String = "secondary_button"
) {
    Box(
        modifier = modifier
            .testTag(testTag)
            .height(Dimens.ButtonHeight)
            .defaultMinSize(minWidth = Dimens.MinTouchTarget, minHeight = Dimens.MinTouchTarget)
            .tvFocusable(shape = shape, onEnterClick = onClick)
            .clip(shape)
            .background(DarkNavyCard)
            .border(width = 1.dp, color = GlassBorder, shape = shape)
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.Space20),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(end = Dimens.Space8)
                )
            }
            Text(
                text = text,
                style = Typography.labelLarge.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
fun GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    shape: Shape = Shapes.medium,
    testTag: String = "ghost_button"
) {
    Box(
        modifier = modifier
            .testTag(testTag)
            .height(Dimens.ButtonHeight)
            .defaultMinSize(minWidth = Dimens.MinTouchTarget, minHeight = Dimens.MinTouchTarget)
            .tvFocusable(shape = shape, onEnterClick = onClick)
            .clip(shape)
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.Space16),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier
                        .size(18.dp)
                        .padding(end = Dimens.Space8)
                )
            }
            Text(
                text = text,
                style = Typography.bodyMedium.copy(
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}
