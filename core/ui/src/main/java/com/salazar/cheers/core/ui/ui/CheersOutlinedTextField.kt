package com.salazar.cheers.core.ui.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults.Container
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ComponentPreviews


@Composable
fun CheersOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    singleLine: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    shape: Shape = MaterialTheme.shapes.medium,
    label: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null
) {
    var searchText by rememberSaveable { mutableStateOf(value) }
    val interactionSource = remember { MutableInteractionSource() }
    val colors = OutlinedTextFieldDefaults.colors(
        disabledContainerColor = MaterialTheme.colorScheme.outline,
        disabledBorderColor = MaterialTheme.colorScheme.outline,
        disabledLabelColor = MaterialTheme.colorScheme.outline,
    )
    val density = LocalDensity.current

    val textColor =
        textStyle.color.takeOrElse {
            MaterialTheme.colorScheme.onSurface
        }

    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))
    BasicTextField(
        modifier = modifier
            .then(
                if (label != null) {
                    Modifier
                        // Merge semantics at the beginning of the modifier chain to ensure
                        // padding is considered part of the text field.
                        .semantics(mergeDescendants = true) {}
                        .padding(top = with(density) { 16.toDp() })
                } else {
                    Modifier
                }
            )
            .defaultMinSize(
                minWidth = OutlinedTextFieldDefaults.MinWidth,
                minHeight = OutlinedTextFieldDefaults.MinHeight
            ),
        value = searchText,
        singleLine = singleLine,
        interactionSource = interactionSource,
        cursorBrush = SolidColor(Color.White),
        onValueChange = { newText ->
            searchText = newText
            onValueChange(newText)
        },
        readOnly = readOnly,
        enabled = enabled,
        textStyle = mergedTextStyle,
    ) { innerTextField ->
        OutlinedTextFieldDefaults.DecorationBox(
            value = searchText,
            innerTextField = innerTextField,
            enabled = enabled,
            singleLine = singleLine,
            interactionSource = interactionSource,
            visualTransformation = VisualTransformation.None,
            label = label,
            placeholder = placeholder,
            colors = colors,
            isError = isError,
            container = {
                Container(
                    enabled = enabled,
                    isError = isError,
                    interactionSource = interactionSource,
                    colors = colors,
                    shape = shape,
                    focusedBorderThickness = 2.dp,
                    unfocusedBorderThickness = 2.dp,
                )
            }
        )
    }
}

@ComponentPreviews
@Composable
private fun CheersOutlinedButtonPreview() {
    CheersPreview {
        CheersOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            label = {
                Text(
                    text = "Name",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            value = "Cheers Official",
            onValueChange = {},
            shape = MaterialTheme.shapes.medium,
        )
    }
}

@ComponentPreviews
@Composable
private fun CheersOutlinedButtonPreview_Disabled() {
    CheersPreview {
        CheersOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            label = {
                Text(
                    text = "Username",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            value = "cheers",
            onValueChange = {},
            enabled = false,
            shape = MaterialTheme.shapes.medium,
        )
    }
}
