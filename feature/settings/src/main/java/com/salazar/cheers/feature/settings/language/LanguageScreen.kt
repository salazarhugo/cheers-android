package com.salazar.cheers.feature.settings.language

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.salazar.cheers.Language
import com.salazar.cheers.core.ui.item.SettingTitle
import com.salazar.cheers.core.ui.ui.Toolbar
import com.salazar.cheers.feature.settings.R

@Composable
fun LanguageScreen(
    language: Language,
    onLanguageChange: (Language) -> Unit,
    onBackPressed: () -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(onBackPressed = onBackPressed, title = stringResource(id = R.string.language))
        },
    ) {
        Column(
            modifier = Modifier.padding(it),
        ) {
            LanguagesSection(
                language = language,
                onLanguageChange = onLanguageChange
            )
        }
    }
}

@Composable
fun LanguagesSection(
    language: Language,
    onLanguageChange: (Language) -> Unit,
) {
    SettingTitle(title = stringResource(id = com.salazar.cheers.feature.settings.R.string.language))
    val radioOptions = Language.values()

    Column(Modifier.selectableGroup()) {
        radioOptions.forEach {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (it == language),
                        onClick = { onLanguageChange(it) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = (it == language),
                    onClick = null
                )
                Text(
                    text = it.name,
//                            style = MaterialTheme.typography.bodyMedium.merge(),
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}


