
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Reply
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import com.salazar.cheers.core.model.ChatMessage
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.data.chat.models.mockMessage1

@Composable
fun OnMessageLongClickDialog(
    expanded: Boolean,
    msg: ChatMessage,
    offset: DpOffset,
    onUnsendMessage: (String) -> Unit = {},
    onCopyText: (String) -> Unit,
    onSaveReply: () -> Unit = {},
    onLike: (String) -> Unit = {},
    onUnlike: (String) -> Unit = {},
    onDismiss: () -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        offset = offset,
        onDismissRequest = onDismiss,
    ) {
        DropdownMenuItem(
            text = { Text("Reply") },
            onClick = { /* Handle edit! */ },
            leadingIcon = { Icon(Icons.AutoMirrored.Outlined.Reply, contentDescription = null) }
        )
        if (msg.isSender) {
            DropdownMenuItem(
                text = { Text("Delete") },
                onClick = {
                    onDismiss()
                    onUnsendMessage(msg.id)
                },
                leadingIcon = {
                    Icon(Icons.Outlined.DeleteOutline, contentDescription = null)
                }
            )
        }
        DropdownMenuItem(
            text = { Text("Copy") },
            onClick = {
                val text = msg.text
                onCopyText(text)
                onDismiss()
            },
            leadingIcon = {
                Icon(Icons.Outlined.ContentCopy, contentDescription = null)
            }
        )
    }
}


@Preview
@Composable
private fun ChatMessageDialogPreview() {
    CheersPreview(
    ) {
        OnMessageLongClickDialog(
            expanded = true,
            offset = DpOffset.Zero,
            msg = mockMessage1,
            onDismiss = {},
            onUnsendMessage = {},
            onCopyText = {},
            onSaveReply = {}
        )
    }
}