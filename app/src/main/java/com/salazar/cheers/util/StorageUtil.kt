package com.salazar.cheers.util

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.*


object StorageUtil {
    private val storageInstance: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

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
    ): UploadTask {
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

}