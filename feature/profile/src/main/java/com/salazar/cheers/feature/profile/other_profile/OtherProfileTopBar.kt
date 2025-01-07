package com.salazar.cheers.feature.profile.other_profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.salazar.cheers.core.model.cheersUser
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.theme.Roboto
import com.salazar.cheers.core.ui.ui.Username


@Composable
internal fun OtherProfileTopBar(
    username: String,
    verified: Boolean,
    premium: Boolean,
    modifier: Modifier = Modifier,
    onCopyUrl: () -> Unit = {},
    onManageFriendship: () -> Unit = {},
    onBackPressed: () -> Unit = {},
) {
    val openDialog = remember { mutableStateOf(false) }

    TopAppBar(
        modifier = modifier,
        title = {
            Username(
                username = username,
                verified = verified,
                premium = premium,
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = Roboto
                ),
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back button")
            }
        },
        actions = {
            IconButton(
                onClick = { openDialog.value = true },
            ) {
                Icon(Icons.Default.MoreVert, null)
            }
        }
    )
    if (openDialog.value)
        MoreDialog(
            openDialog = openDialog.value,
            onCopyUrl = onCopyUrl,
            onManageFriendship = onManageFriendship,
            onDismissRequest = {
                openDialog.value = false
            }
        )
}

@ComponentPreviews
@Composable
private fun OtherProfileTopBarPreview() {
    val user = cheersUser
    CheersPreview {
        OtherProfileTopBar(
            username = user.username,
            verified = user.verified,
            premium = user.premium,
            modifier = Modifier,
        )
    }
}