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

    fun getOrCreateChatChannel(otherUserId: String, onComplete: (channelId: String) -> Unit) {
        currentUserDocRef.collection("engagedChatChannels")
            .document(otherUserId).get().addOnSuccessListener {
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
                         mutableListOf(currentUserId, otherUserId),
                        Timestamp.now(),
                        "",
                        TextMessage(),
                        ChatChannelType.DIRECT
                    )
                )

                currentUserDocRef.collection("engagedChatChannels")
                    .document(otherUserId)
                    .set(mapOf("channelId" to newChannel.id))

                firestoreInstance.collection("users") //ADDS channelId for the otherUser
                    .document(otherUserId)
                    .collection("engagedChatChannels")
                    .document(currentUserId)
                    .set(mapOf("channelId" to newChannel.id))

                onComplete(newChannel.id)
            }
    }

    fun getChatChannels(): Flow<List<ChatChannel>> = callbackFlow {

        val channelCol = chatChannelsCollectionRef
            .whereArrayContains("members", FirebaseAuth.getInstance().currentUser!!.uid)
//            .orderBy("recentMessage.time", Query.Direction.DESCENDING)

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
            .orderBy("time")

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
                mapOf(
                    "text" to (message as TextMessage).text,
                    "senderId" to message.senderId,
                    "recipientId" to message.recipientId,
                    "time" to message.time,
                    "seenBy" to listOf(currentUserId)
                )
            )
            MessageType.IMAGE -> chatChannelsCollectionRef.document(channelId).update(
                mapOf(
                    "text" to "sent an image",
                    "senderId" to (message as ImageMessage).senderId,
                    "recipientId" to message.recipientId,
                    "time" to message.time,
                    "seenBy" to listOf(currentUserId)
                )
            )
        }
    }

    fun deleteMessage(channelId: String, messageId: String) {
        chatChannelsCollectionRef.document(channelId).collection("messages").document(messageId)
            .delete().addOnSuccessListener {

            }
    }

}