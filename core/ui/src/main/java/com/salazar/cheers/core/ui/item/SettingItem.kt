package com.salazar.cheers.core.ui.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.R

@Composable
fun SettingRadioButtonItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title)
        RadioButton(
            selected = true,
            onClick = null // null recommended for accessibility with screenreaders
        )
    }
}

@Composable
fun SettingItem(
    title: String,
    icon: ImageVector? = null,
    onClick: () -> Unit = {},
    color: Color = Color.Unspecified,
    trailingContent: @Composable () -> Unit = {},
) {
    val leadingContent: (@Composable () -> Unit)? = when (icon) {
        null -> null
        else -> {
            { Icon(imageVector = icon, contentDescription = null) }
        }
    }

    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        headlineContent = {
            Text(text = title, color = color)
        },
        leadingContent = leadingContent,
        trailingContent = trailingContent,
    )
}

@Composable
fun SettingTitle(
    modifier: Modifier = Modifier,
    title: String
) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier.padding(16.dp),
    )
}

@Preview
@Composable
private fun SettingItemPreview() {
    CheersPreview {
        SettingItem(
            title = stringResource(id = R.string.spotlight),
            icon = Icons.Outlined.Security,
            onClick = {
            },
        )
    }

}