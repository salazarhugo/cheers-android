import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.internal.Message
import com.salazar.cheers.internal.TextMessage

@Composable
fun OnMessageLongClickDialog(
    openDialog: MutableState<Boolean>,
    msg: Message,
    onUnsendMessage: (Message) -> Unit = {},
    onCopyText: (String) -> Unit,
    onSaveReply: () -> Unit = {},
    onLike: (Message) -> Unit = {},
    onUnlike: (Message) -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = {
            openDialog.value = false
        },
        text = {
            Column {
                val text = if (msg is TextMessage) msg.text else ""
                Text(
                    text = text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (msg.senderId == FirebaseAuth.getInstance().currentUser?.uid)
                    TextButton(
                        onClick = {
                            openDialog.value = false
                            onUnsendMessage(msg)
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Unsend message")
                    }
                TextButton(
                    onClick = {
                        val text = if (msg is TextMessage) msg.text else ""
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
                if (msg.likedBy.contains(FirebaseAuth.getInstance().currentUser?.uid!!))
                    TextButton(
                        onClick = {
                            onUnlike(msg)
                            openDialog.value = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Unlike")
                    }
                else
                    TextButton(
                        onClick = {
                            onLike(msg)
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

