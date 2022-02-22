package com.salazar.cheers.ui.settings.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.salazar.cheers.components.share.Toolbar
import com.salazar.cheers.data.entities.Theme

@Composable
fun ThemeScreen(
    theme: Theme,
    onThemeChange: (Theme) -> Unit,
    onBackPressed: () -> Unit,
) {
    Scaffold(
        topBar = { Toolbar(onBackPressed = onBackPressed, title = "Theme") },
    ) {
        Column {
            val radioOptions = listOf(Theme.LIGHT, Theme.DARK, Theme.SYSTEM)

            Column(Modifier.selectableGroup()) {
                radioOptions.forEach { text ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = (text == theme),
                                onClick = { onThemeChange(text) },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = text.displayName,
//                            style = MaterialTheme.typography.bodyMedium.merge(),
                            modifier = Modifier.padding(start = 16.dp)
                        )
                        RadioButton(
                            selected = (text == theme),
                            onClick = null
                        )
                    }
                }
            }
        }
    }
}