package com.salazar.cheers.core.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.annotations.ComponentPreviews

@Composable
fun PostCaption(
    caption: String,
    modifier: Modifier = Modifier,
    onUserClicked: (username: String) -> Unit = {},
    onPostClicked: () -> Unit = {},
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
        modifier = modifier,
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

@ComponentPreviews
@Composable
private fun PostCaptionPreview() {
    CheersPreview {
        PostCaption(
            caption = "\uD83D\uDCE2 Invest in a warehouse with showroom in Agios Nicolaos, Larnaka!",
            modifier = Modifier.padding(16.dp),
        )
    }
}
