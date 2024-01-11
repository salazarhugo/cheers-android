package com.salazar.cheers.core.util

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.UUID


object StorageUtil {
    private val storageInstance: FirebaseStorage by lazy { FirebaseStorage.getInstance() }
    private val storyBucket: FirebaseStorage by lazy { FirebaseStorage.getInstance("gs://cheers-stories") }
    private val chatBucket: FirebaseStorage by lazy { FirebaseStorage.getInstance("gs://cheers-chat-bucket") }

    // 5 MB
    private const val IMAGE_SIZE_LIMIT = 5000000

    // 4 GB
    private const val VIDEO_SIZE_LIMIT = 4000000000

    val currentUserRefChat: StorageReference
        get() = storyBucket.reference
            .child(
                FirebaseAuth.getInstance().currentUser?.uid
                    ?: throw NullPointerException("UID is null.")
            )

    val currentUserRefStory: StorageReference
        get() = storyBucket.reference
            .child(
                FirebaseAuth.getInstance().currentUser?.uid
                    ?: throw NullPointerException("UID is null.")
            )

    val currentUserRef: StorageReference
        get() = storageInstance.reference
            .child(
                FirebaseAuth.getInstance().currentUser?.uid
                    ?: throw NullPointerException("UID is null.")
            )

    fun uploadProfilePhoto(imageBytes: ByteArray): Task<Uri> {
        val ref =
            currentUserRef.child("profilePictures/${FirebaseAuth.getInstance().currentUser?.uid!!}")
        return ref.putBytes(imageBytes)
            .continueWithTask {
                ref.downloadUrl
            }
    }

    fun uploadProfileBanner(imageBytes: ByteArray): Task<Uri> {
        val ref =
            currentUserRef.child("profileBanners/${FirebaseAuth.getInstance().currentUser?.uid!!}")
        return ref.putBytes(imageBytes)
            .continueWithTask {
                ref.downloadUrl
            }
    }

    fun uploadMessageImage(
        imageBytes: ByteArray,
    ): Task<Uri> {
        val ref = currentUserRefChat.child("messages/${UUID.randomUUID()}")
        return ref.putBytes(imageBytes)
            .continueWithTask {
                ref.downloadUrl
            }
    }

    suspend fun uploadStoryImage(
        imageBytes: ByteArray,
    ): Uri {
        val ref = currentUserRefStory.child("stories/${UUID.randomUUID()}")
        return ref.putBytes(imageBytes).continueWithTask { ref.downloadUrl }
            .addOnSuccessListener {
                Log.d("Cloud", it.toString())
            }
            .addOnFailureListener {
                Log.d("Cloud", it.toString())
            }
            .await()
    }

    suspend fun uploadPostImage(
        imageBytes: ByteArray,
    ): Uri {
        val ref = currentUserRef.child("posts/${UUID.randomUUID()}")
        return ref.putBytes(imageBytes).continueWithTask { ref.downloadUrl }.await()
    }

    fun uploadPostVideo(
        videoUri: Uri,
//        onSuccess: (videoUrl: String) -> Unit
    ): UploadTask? {
        if (videoUri.toFile().length() > VIDEO_SIZE_LIMIT)
            return null
        val ref = currentUserRef.child("posts/${UUID.randomUUID()}")
        return ref.putFile(videoUri)
//            .addOnSuccessListener {
//                ref.downloadUrl.addOnSuccessListener { downloadUri ->
//                    onSuccess(downloadUri.toString())
//                }
//            }
    }

    fun pathToReference(path: String): StorageReference? {
        return try {
            storageInstance.getReference(path)
        } catch (e: Exception) {
            null
        }
    }

    fun getSnapchatBanner(context: Context) {
        try {
            val storageRef = storageInstance.reference
            val pathReference = storageRef.child("snapchat/add-friend.jpg")
            val file = File("${context.filesDir}/snapchat-add-friend.jpg")
            pathReference.getFile(file) // Not safe
        } catch (e: Exception) {
        }
    }

    fun uploadEventImage(
        imageBytes: ByteArray,
    ): Task<Uri> {
        val ref = currentUserRef.child("events/${UUID.randomUUID()}")
        return ref.putBytes(imageBytes)
            .continueWithTask {
                ref.downloadUrl
            }
    }
}