package com.salazar.cheers.feature.parties.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.SymbolAnnotationType
import com.salazar.cheers.core.ui.messageFormatter
import com.salazar.cheers.core.util.Utils.clickableIf


@Preview
@Composable
fun PartyDescriptionPreview() {
    PartyDescription(
        description = "Fake description",
        onUserClicked = {},
    )
}

@Composable
fun PartyDescription(
    modifier: Modifier = Modifier,
    description: String,
    onUserClicked: (String) -> Unit,
    onDescriptionClick: (() -> Unit)? = null,
) {
    if (description.isBlank()) return
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .clickableIf(onDescriptionClick != null) {
                if (onDescriptionClick != null) {
                    onDescriptionClick()
                }
            }
            .then(modifier),
    ) {
        Text(
            "What to expect",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        val styledDescription = messageFormatter(
            text = description,
            primary = false,
        )

        ClickableText(
            text = styledDescription,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier.padding(vertical = 8.dp),
            onClick = {
                styledDescription
                    .getStringAnnotations(start = it, end = it)
                    .firstOrNull()
                    ?.let { annotation ->
                        when (annotation.tag) {
                            SymbolAnnotationType.LINK.name -> uriHandler.openUri(annotation.item)
                            SymbolAnnotationType.PERSON.name -> onUserClicked(annotation.item)
                            else -> Unit
                        }
                    }
            }
        )
    }
}

