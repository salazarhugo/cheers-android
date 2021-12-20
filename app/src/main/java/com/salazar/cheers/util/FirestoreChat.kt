package com.salazar.cheers.util

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.salazar.cheers.internal.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*
import kotlin.collections.ArrayList


@OptIn(ExperimentalCoroutinesApi::class)
object FirestoreChat {

    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document(
            "users/${
                FirebaseAuth.getInstance().uid
                    ?: throw NullPointerException("UID is null.")
            }"
        )
    private val chatChannelsCollectionRef = firestoreInstance.collection("chatChannels")

    fun getOrCreateChatChannel(otherUser: User, onComplete: (channelId: String) -> Unit) {
        currentUserDocRef.collection("engagedChatChannels")
            .document(otherUser.id).get().addOnSuccessListener {
                if (it.exists()) {
                    onComplete(it["channelId"] as String)
                    return@addOnSuccessListener
                }
                val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

                val newChannel = chatChannelsCollectionRef.document()
                newChannel.set(
                    ChatChannel(
                        newChannel.id,
                        "Channel 1",
                        listOf(currentUserId, otherUser.id),
                        User(),
                        Timestamp.now(),
                        "",
                        TextMessage(),
                        ChatChannelType.DIRECT
                    )
                )

                currentUserDocRef.collection("engagedChatChannels")
                    .document(otherUser.id)
                    .set(mapOf("channelId" to newChannel.id))

                firestoreInstance.collection("users") //ADDS channelId for the otherUser
                    .document(otherUser.id)
                    .collection("engagedChatChannels")
                    .document(currentUserId)
                    .set(mapOf("channelId" to newChannel.id))

                onComplete(newChannel.id)
            }
    }

    suspend fun getChatChannels(): Flow<List<ChatChannel>> = callbackFlow {
        val channelCol = chatChannelsCollectionRef
            .whereArrayContains("members", FirebaseAuth.getInstance().currentUser!!.uid)
            .orderBy("recentMessageTime", Query.Direction.DESCENDING)

        val subscription = channelCol.addSnapshotListener { snapshot, _ ->
            val chatChannels = ArrayList<ChatChannel>()
            snapshot!!.forEach {
                chatChannels.add(it.toObject(ChatChannel::class.java))
            }
            this.trySend(chatChannels).isSuccess
        }

        awaitClose {
            subscription.remove()
        }
    }

    fun getChatMessages(channelId: String): Flow<List<Message>> = callbackFlow {

        val messagesDocument = chatChannelsCollectionRef
            .document(channelId)
            .collection("messages")
            .orderBy("time", Query.Direction.DESCENDING)

        val subscription = messagesDocument.addSnapshotListener { snapshot, _ ->
            val items = mutableListOf<Message>()
            snapshot!!.forEach {
                if (it["type"] == MessageType.TEXT)
                    items.add(it.toObject(TextMessage::class.java))
                else
                    items.add(it.toObject(ImageMessage::class.java))
                return@forEach
            }
            this.trySend(items).isSuccess
        }

        awaitClose {
            subscription.remove()
        }
    }

    fun sendMessage(message: Message, channelId: String) {
        if (message is TextMessage && message.text.isBlank())
            return

        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        val doc = chatChannelsCollectionRef.document(channelId).collection("messages").document()
        message.id = doc.id
        doc.set(message)

        chatChannelsCollectionRef.document(channelId).update(mapOf("seenBy" to FieldValue.delete()))
        when (message.type) {
            MessageType.TEXT -> chatChannelsCollectionRef.document(channelId).update(
                "recentMessage",
                mapOf(
                    "text" to (message as TextMessage).text,
                    "senderId" to message.senderId,
                    "senderUsername" to message.senderUsername,
                    "senderProfilePicturePath" to message.senderProfilePicturePath,
                    "recipientId" to message.recipientId,
                    "time" to FieldValue.serverTimestamp(),
                    "seenBy" to listOf(currentUserId)
                ),
                "recentMessageTime", FieldValue.serverTimestamp()
            )
            MessageType.IMAGE -> chatChannelsCollectionRef.document(channelId).update(
                "recentMessage",
                mapOf(
                    "text" to "sent an image",
                    "senderId" to (message as ImageMessage).senderId,
                    "senderUsername" to message.senderUsername,
                    "senderProfilePicturePath" to message.senderProfilePicturePath,
                    "recipientId" to message.recipientId,
                    "time" to FieldValue.serverTimestamp(),
                    "seenBy" to listOf(currentUserId)
                ),
                "recentMessageTime", FieldValue.serverTimestamp()
            )
        }
    }

    fun deleteMessage(channelId: String, messageId: String) {
        chatChannelsCollectionRef.document(channelId).collection("messages").document(messageId)
            .delete().addOnSuccessListener {

            }
    }

}