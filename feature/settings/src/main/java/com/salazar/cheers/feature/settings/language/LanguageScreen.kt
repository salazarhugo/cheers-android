package com.salazar.cheers.feature.settings.language

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.Language
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
            LazyColumn {
                languagesSection(
                    language = language,
                    onLanguageChange = onLanguageChange
                )
            }
        }
    }
}

fun LazyListScope.languagesSection(
    language: Language,
    onLanguageChange: (Language) -> Unit,
) {
    item {
        SettingTitle(
            title = stringResource(id = R.string.language),
        )
    }

    val radioOptions = Language.entries.toTypedArray().toList()

    items(
        items = radioOptions,
    ) {
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
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            RadioButton(
                selected = (it == language),
                onClick = null
            )
            Column {
                Text(
                    text = it.value,
                )
                Text(
                    text = it.nameInEnglish,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}


