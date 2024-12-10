package com.salazar.cheers.core.ui.modifier

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.components.shimmer.PlaceholderHighlight
import com.salazar.cheers.core.ui.components.shimmer.placeholder
import com.salazar.cheers.core.ui.components.shimmer.shimmer

fun Modifier.cheersShimmer(
    isLoading: Boolean,
    backgroundColor: Color = Color.Unspecified,
    shape: Shape = RoundedCornerShape(8.dp),
    showShimmerAnimation: Boolean = true
): Modifier = composed {
    val highlight = if (showShimmerAnimation) {
        PlaceholderHighlight.shimmer(highlightColor = MaterialTheme.colorScheme.onTertiary)
    } else {
        null
    }
    val specifiedBackgroundColor = backgroundColor.takeOrElse { Color(0xFFDBD6D1).copy(0.6f) }
    Modifier.placeholder(
        color = specifiedBackgroundColor,
        visible = isLoading,
        shape = shape,
        highlight = highlight
    )
}

@Composable
fun ShimmerShape(
    width: Dp,
    modifier: Modifier = Modifier,
    height: Dp = 12.dp,
    shape: Shape = RoundedCornerShape(8.dp),
    showShimmerAnimation: Boolean = true,
) {
    Spacer(
        modifier = modifier
            .size(width, height)
            .cheersShimmer(
                isLoading = true,
                shape = shape,
                showShimmerAnimation = showShimmerAnimation,
            ),
    )
}
