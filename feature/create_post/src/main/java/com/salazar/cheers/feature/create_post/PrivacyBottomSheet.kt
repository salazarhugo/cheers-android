package com.salazar.cheers.feature.create_post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.Privacy
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.theme.GreySheet
import kotlinx.coroutines.launch

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
        containerColor = if (!isSystemInDarkTheme()) MaterialTheme.colorScheme.surface else GreySheet,
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        onDismissRequest = onDismiss,
        windowInsets = WindowInsets(0,0,0,0),
        content = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                Text(
                    text = "Event privacy",
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
                PrivacyItem(
                    privacy = it,
                    selected = it == privacy,
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
    onSelectPrivacy: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable { onSelectPrivacy() }
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(privacy.icon, null)
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(privacy.title)
                Text(privacy.subtitle)
            }
        }
        Checkbox(
            checked = selected,
            onCheckedChange = { onSelectPrivacy() },
        )
    }
}

@ComponentPreviews
@Composable
private fun PrivacyBottomSheetPreview() {
    CheersPreview {
        PrivacyBottomSheet(
            privacy = Privacy.PUBLIC,
            modifier = Modifier,
        )
    }
}
