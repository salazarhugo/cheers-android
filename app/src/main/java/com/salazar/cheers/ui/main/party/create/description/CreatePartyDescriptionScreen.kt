package com.salazar.cheers.ui.main.party.create.description

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.ui.Toolbar
import com.salazar.cheers.ui.main.party.create.basicinfo.CreatePartyBasicInfoBottomBar

@Composable
fun CreatePartyDescriptionScreen(
    description: String,
    onDescriptionChange: (String) -> Unit,
    onBackPressed: () -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                onBackPressed = onBackPressed,
                title = "Description",
            )
        },
        bottomBar ={
            CreatePartyBasicInfoBottomBar(
                enabled = description.isNotBlank(),
                onClick = onBackPressed,
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(16.dp)
                .semantics { isTraversalGroup = true },
        ) {
            Text(
                text = "Provide more information about your event so that guests know what to expect.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            DescriptionInput(
                description = description,
                onDescriptionChanged = onDescriptionChange,
            )
        }
    }
}

@Composable
fun DescriptionInput(
    description: String,
    onDescriptionChanged: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = description,
        modifier = Modifier
            .fillMaxWidth(),
        onValueChange = { onDescriptionChanged(it) },
        shape = MaterialTheme.shapes.medium,
        singleLine = false,
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            }
        ),
        placeholder = { Text("Description") },
    )
}


@ScreenPreviews
@Composable
private fun CreatePartyDescriptionScreenPreview() {
    CheersPreview {
        CreatePartyDescriptionScreen(
            description = "",
            onDescriptionChange = {},
            onBackPressed = {},
        )
    }
}