import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.salazar.cheers.core.model.ChatMessage

@Composable
fun OnMessageLongClickDialog(
    openDialog: MutableState<Boolean>,
    msg: ChatMessage,
    onUnsendMessage: (String) -> Unit = {},
    onCopyText: (String) -> Unit,
    onSaveReply: () -> Unit = {},
    onLike: (String) -> Unit = {},
    onUnlike: (String) -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = {
            openDialog.value = false
        },
        text = {
            Column {
                val text = msg.text
                Text(
                    text = text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                // TODO FirebaseAuth UID
//                if (msg.senderId == FirebaseAuth.getInstance().currentUser?.uid)
                TextButton(
                    onClick = {
                        openDialog.value = false
                        onUnsendMessage(msg.id)
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Unsend message")
                }
                TextButton(
                    onClick = {
                        val text = msg.text
                        onCopyText(text)
                        openDialog.value = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Copy text")
                }
                TextButton(
                    onClick = { openDialog.value = false },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                ) {
                    Text("Save reply")
                }
                // TODO FirebaseAuth UID
                if (true)
//                if (msg.likedBy.contains(FirebaseAuth.getInstance().currentUser?.uid!!))
                    TextButton(
                        onClick = {
                            onUnlike(msg.id)
                            openDialog.value = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Unlike")
                    }
                else
                    TextButton(
                        onClick = {
                            onLike(msg.id)
                            openDialog.value = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Like")
                    }
            }
        },
        confirmButton = {
        },
    )
}

