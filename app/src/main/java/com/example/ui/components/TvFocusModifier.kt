package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import com.example.ui.theme.Dimens
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.Shapes

/**
 * Modifier to provide visual D-pad focus indicator (gold border, scale animation)
 * and remote click handling for Android TV and mobile navigation.
 */
fun Modifier.tvFocusable(
    shape: Shape = Shapes.medium,
    focusColor: Color = GoldPrimary,
    scaleOnFocus: Float = Dimens.FocusScale,
    onEnterClick: (() -> Unit)? = null
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isFocused) scaleOnFocus else 1f,
        label = "tv_focus_scale"
    )

    this
        .scale(scale)
        .focusable(interactionSource = interactionSource)
        .then(
            if (isFocused) {
                Modifier.border(
                    width = Dimens.FocusBorderWidth,
                    color = focusColor,
                    shape = shape
                )
            } else {
                Modifier
            }
        )
        .onKeyEvent { keyEvent ->
            if (onEnterClick != null &&
                (keyEvent.key == Key.DirectionCenter || keyEvent.key == Key.Enter || keyEvent.key == Key.NumPadEnter)
            ) {
                onEnterClick()
                true
            } else {
                false
            }
        }
}
