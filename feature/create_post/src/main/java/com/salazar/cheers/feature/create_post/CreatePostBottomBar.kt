package com.salazar.cheers.feature.create_post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.Privacy
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.ui.ButtonWithLoading

@Composable
fun CreatePostBottomBar(
    privacy: Privacy,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
    onSelectPrivacy: (Privacy) -> Unit = {},
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PrivacyComponent(
            privacy = privacy,
            onSelectPrivacy = onSelectPrivacy,
        )
        ButtonWithLoading(
            text = "Post",
            isLoading = isLoading,
            shape = CircleShape,
            onClick = onClick,
            enabled = enabled,
        )
    }
}

@ComponentPreviews
@Composable
private fun CreatePostBottomBarPreview() {
    CheersPreview {
        CreatePostBottomBar(
            privacy = Privacy.FRIENDS,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
        )
    }
}
