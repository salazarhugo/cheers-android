package com.salazar.cheers.util

import android.net.Uri
import androidx.core.net.toFile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.*


object StorageUtil {
    private val storageInstance: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    // 5 MB
    private const val IMAGE_SIZE_LIMIT = 5000000
    // 4 GB
    private const val VIDEO_SIZE_LIMIT = 4000000000

    val currentUserRef: StorageReference
        get() = storageInstance.reference
            .child(
                FirebaseAuth.getInstance().currentUser?.uid
                    ?: throw NullPointerException("UID is null.")
            )

    fun uploadProfilePhoto(
        imageBytes: ByteArray,
        onSuccess: (downloadUrl: String) -> Unit
    ) {
        if (imageBytes.size > IMAGE_SIZE_LIMIT)
            return
        val ref = currentUserRef.child("profilePictures/${UUID.nameUUIDFromBytes(imageBytes)}")
        ref.putBytes(imageBytes)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUri ->
                    onSuccess(downloadUri.toString())
                }
            }
    }

    fun uploadMessageImage(
        imageBytes: ByteArray,
        onSuccess: (downloadUrl: String) -> Unit,
    ) {
        if (imageBytes.size > IMAGE_SIZE_LIMIT)
            return
        val ref = currentUserRef.child("messages/${UUID.randomUUID()}")
        ref.putBytes(imageBytes)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUri ->
                    onSuccess(downloadUri.toString())
                }
            }
    }

    fun uploadPostImage2(
        imageBytes: ByteArray,
    ): UploadTask {
        val ref = currentUserRef.child("posts/${UUID.randomUUID()}")
        return ref.putBytes(imageBytes)
    }

    fun uploadPostImage(
        imageBytes: ByteArray,
        onSuccess: (downloadUrl: String) -> Unit,
    ) {
        if (imageBytes.size > IMAGE_SIZE_LIMIT)
            return
        val ref = currentUserRef.child("posts/${UUID.randomUUID()}")
        ref.putBytes(imageBytes)
            .continueWithTask {
                ref.downloadUrl
            }
            .addOnSuccessListener { downloadUri ->
                onSuccess(downloadUri.toString())
            }
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

    fun uploadEventImage(
        imageBytes: ByteArray,
        onSuccess: (downloadUrl: String) -> Unit,
    ) {
        val ref = currentUserRef.child("events/${UUID.randomUUID()}")
        ref.putBytes(imageBytes)
            .continueWithTask {
                ref.downloadUrl
            }
            .addOnSuccessListener { downloadUri ->
                onSuccess(downloadUri.toString())
            }
    }
}