package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(Dimens.RadiusSmall),
    medium = RoundedCornerShape(Dimens.RadiusMedium),
    large = RoundedCornerShape(Dimens.RadiusLarge),
    extraLarge = RoundedCornerShape(Dimens.RadiusXLarge)
)

val TicketShape = RoundedCornerShape(Dimens.RadiusXLarge)
val PillShape = RoundedCornerShape(Dimens.RadiusPill)
val FocusShape = RoundedCornerShape(Dimens.RadiusMedium)
