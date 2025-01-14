package com.salazar.cheers.feature.create_post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.Privacy
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.util.Utils.clickableIf
import com.salazar.cheers.core.util.Utils.conditional

@Composable
fun PrivacyBottomSheet(
    privacy: Privacy,
    modifier: Modifier = Modifier,
    privacyState: SheetState = rememberModalBottomSheetState(),
    onSelectPrivacy: (Privacy) -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    ModalBottomSheet(
        modifier = modifier,
        sheetState = privacyState,
//        containerColor = if (!isSystemInDarkTheme()) MaterialTheme.colorScheme.surface else GreySheet,
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        onDismissRequest = onDismiss,
//        contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
        content = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                Text(
                    text = "Privacy",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(12.dp)
                )
                Text(
                    text = "Choose who can see and join this event. You'll be able to invite people later.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }

            Privacy.entries.forEach {
                val enabled = listOf(Privacy.PRIVATE, Privacy.FRIENDS)
                PrivacyItem(
                    privacy = it,
                    selected = it == privacy,
                    enabled = it in enabled,
                    onSelectPrivacy = {
                        onSelectPrivacy(it)
                    }
                )
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.medium,
            ) {
                Text("Done")
            }
        }
    )
}

@Composable
fun PrivacyItem(
    privacy: Privacy,
    selected: Boolean,
    enabled: Boolean,
    onSelectPrivacy: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clickableIf(enabled) {
                onSelectPrivacy()
            }
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier
                .conditional(!enabled) {
                    alpha(0.38f)
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = privacy.icon,
                contentDescription = null,
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = privacy.title,
                )
                Text(
                    text = privacy.subtitle,
                )
            }
        }
        RadioButton(
            selected = selected,
            enabled = enabled,
            onClick = { onSelectPrivacy() },
        )
    }
}

@ComponentPreviews
@Composable
private fun PrivacyBottomSheetPreview() {
    CheersPreview {
        PrivacyBottomSheet(
            privacyState = rememberModalBottomSheetState(),
            privacy = Privacy.PUBLIC,
            modifier = Modifier,
        )
    }
}
