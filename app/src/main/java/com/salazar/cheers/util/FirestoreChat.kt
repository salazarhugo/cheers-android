package com.salazar.cheers.util

import android.util.Log
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

    const val TAG = "Firestore"

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

    suspend fun getChatChannel(channelId: String): Flow<ChatChannel> = callbackFlow {
        val channelCol = chatChannelsCollectionRef
            .document(channelId)

        val subscription = channelCol.addSnapshotListener { doc, e ->
            if (e != null) {
                Log.e(TAG, "Listen failed", e)
                return@addSnapshotListener
            }

            if (doc == null || !doc.exists())
                return@addSnapshotListener

            val channel = doc.toObject(ChatChannel::class.java)!!
            trySend(channel).isSuccess
        }

        awaitClose {
            subscription.remove()
        }
    }

    suspend fun getChatChannels(): Flow<List<ChatChannel>> = callbackFlow {
        val channelCol = chatChannelsCollectionRef
            .whereArrayContains("members", FirebaseAuth.getInstance().currentUser!!.uid)
            .orderBy("recentMessageTime", Query.Direction.DESCENDING)

        val subscription = channelCol.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e(TAG, "Users listener error.", e)
                return@addSnapshotListener
            }
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

        val subscription = messagesDocument.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e(TAG, "Users listener error.", e)
                return@addSnapshotListener
            }
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

    fun seenLastMessage(channelId: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

        chatChannelsCollectionRef.document(channelId).update(
            mapOf("recentMessage.seenBy" to FieldValue.arrayUnion(currentUserId)),
        )
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
                    "id" to doc.id,
                    "text" to (message as TextMessage).text,
                    "senderId" to message.senderId,
                    "senderUsername" to message.senderUsername,
                    "senderProfilePictureUrl" to message.senderProfilePictureUrl,
                    "time" to FieldValue.serverTimestamp(),
                    "seenBy" to listOf(currentUserId)
                ),
                "recentMessageTime", FieldValue.serverTimestamp()
            )
            MessageType.IMAGE -> chatChannelsCollectionRef.document(channelId).update(
                "recentMessage",
                mapOf(
                    "id" to doc.id,
                    "text" to "sent an image",
                    "senderId" to (message as ImageMessage).senderId,
                    "senderUsername" to message.senderUsername,
                    "senderProfilePictureUrl" to message.senderProfilePictureUrl,
                    "time" to FieldValue.serverTimestamp(),
                    "seenBy" to listOf(currentUserId)
                ),
                "recentMessageTime", FieldValue.serverTimestamp()
            )
        }
    }

    fun unsendMessage(channelId: String, messageId: String) {
        chatChannelsCollectionRef.document(channelId).collection("messages").document(messageId)
            .delete()
    }

    fun likeMessage(channelId: String, messageId: String) {
        chatChannelsCollectionRef
            .document(channelId)
            .collection("messages")
            .document(messageId)
            .update("likedBy", FieldValue.arrayUnion(FirebaseAuth.getInstance().currentUser?.uid!!))
    }

    fun unlikeMessage(channelId: String, messageId: String) {
        chatChannelsCollectionRef
            .document(channelId)
            .collection("messages")
            .document(messageId)
            .update(
                "likedBy",
                FieldValue.arrayRemove(FirebaseAuth.getInstance().currentUser?.uid!!)
            )
    }

}