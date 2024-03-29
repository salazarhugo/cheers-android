package com.salazar.cheers.feature.profile.other_profile

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.message.MessageComponent

@Composable
fun UserNotFoundMessage(
    modifier: Modifier = Modifier,
) {
    MessageComponent(
        modifier = modifier.padding(16.dp),
        title = "User not found",
        subtitle = "The account may be disabled, deleted, banned, or suspended."
    )
}

@ComponentPreviews
@Composable
private fun UserNotFoundMessagePreview() {
    CheersPreview {
        UserNotFoundMessage(
            modifier = Modifier.padding(16.dp),
        )
    }
}


