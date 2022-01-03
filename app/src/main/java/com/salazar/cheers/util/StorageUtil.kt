package com.salazar.cheers.util

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.*


object StorageUtil {
    private val storageInstance: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    private val currentUserRef: StorageReference
        get() = storageInstance.reference
            .child(
                FirebaseAuth.getInstance().currentUser?.uid
                    ?: throw NullPointerException("UID is null.")
            )

    fun uploadProfilePhoto(
        imageBytes: ByteArray,
        onSuccess: (imagePath: String) -> Unit
    ) {
        val ref = currentUserRef.child("profilePictures/${UUID.nameUUIDFromBytes(imageBytes)}")
        ref.putBytes(imageBytes)
            .addOnSuccessListener {
                onSuccess(ref.path)
            }
    }

    fun uploadMessageImageBeta(
        imageBytes: ByteArray,
    ): UploadTask {
        val ref = currentUserRef.child("messages/${UUID.nameUUIDFromBytes(imageBytes)}")
        return ref.putBytes(imageBytes)
    }

    fun uploadMessageImage(
        imageBytes: ByteArray,
        onSuccess: (imagePath: String) -> Unit
    ) {
        val ref = currentUserRef.child("messages/${UUID.nameUUIDFromBytes(imageBytes)}")
        ref.putBytes(imageBytes)
            .addOnSuccessListener {
                onSuccess(ref.path)
            }
    }

    fun uploadPostImage(
        imageBytes: ByteArray,
        onSuccess: (imagePath: String) -> Unit
    ) {
        val ref = currentUserRef.child("posts/${UUID.nameUUIDFromBytes(imageBytes)}")
        ref.putBytes(imageBytes)
            .addOnSuccessListener {
                onSuccess(ref.path)
            }
    }

    fun uploadPostVideo(
        videoUri: Uri,
        onSuccess: (videoPath: String) -> Unit
    ) {
        val ref = currentUserRef.child("posts/${Calendar.getInstance().timeInMillis}")
        ref.putFile(videoUri)
            .addOnSuccessListener {
                onSuccess(ref.path)
            }
    }

    fun pathToReference(path: String): StorageReference? {
        return try {
            storageInstance.getReference(path)
        } catch (e: Exception) {
            null
        }
    }

}