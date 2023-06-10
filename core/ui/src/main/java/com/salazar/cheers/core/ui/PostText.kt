package com.salazar.cheers.core.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp

@Composable
fun PostText(
    caption: String,
    onUserClicked: (username: String) -> Unit,
    onPostClicked: () -> Unit,
) {
    if (caption.isBlank())
        return

    val styledCaption = messageFormatter(
        text = caption,
        primary = false,
    )
    val uriHandler = LocalUriHandler.current

    ClickableText(
        text = styledCaption,
        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, end = 16.dp, start = 16.dp),
        onClick = {
            styledCaption
                .getStringAnnotations(start = it, end = it)
                .firstOrNull()
                .let { annotation ->
                    if (annotation == null)
                        onPostClicked()
                    else
                        when (annotation.tag) {
                            SymbolAnnotationType.LINK.name -> uriHandler.openUri(annotation.item)
                            SymbolAnnotationType.PERSON.name -> onUserClicked(annotation.item)
                            else -> Unit
                        }
                }
        }
    )
}