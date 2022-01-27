package com.salazar.cheers.util

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.salazar.cheers.backend.Neo4jUtil
import com.salazar.cheers.internal.User
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


object FirestoreUtil {

    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document(
            "users/${
                FirebaseAuth.getInstance().uid
                    ?: throw NullPointerException("UID is null.")
            }"
        )

    fun checkIfUserExists(
        onComplete: (exists: Boolean) -> Unit,
    ) {
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            onComplete(documentSnapshot.exists())
            return@addOnSuccessListener
        }
    }

    fun initCurrentUserIfFirstTime(
        acct: GoogleSignInAccount? = null,
        email: String = "",
        username: String,
        onComplete: (exists: Boolean) -> Unit,
    ) {
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                onComplete(true)
                return@addOnSuccessListener
            }

            val id = FirebaseAuth.getInstance().currentUser!!.uid
            val fullName = if (acct != null) "${acct.givenName} ${acct.familyName}" else ""

            val newUser = User().copy(
                id = id,
                fullName = fullName,
                username = username,
                profilePictureUrl = acct?.photoUrl.toString(),
                email = FirebaseAuth.getInstance().currentUser?.email ?: email,
            )
            currentUserDocRef.set(newUser)
            GlobalScope.launch {
                Neo4jUtil.addUser(newUser)
            }
            onComplete(false)
        }.addOnFailureListener {
            Log.e("Firestore", it.toString())
        }
    }


    fun getCurrentUserDocumentLiveData(): LiveData<User> {
        val currentUser: MutableLiveData<User> = MutableLiveData()
        currentUserDocRef.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                Log.e("FIRESTORE", "Users listener error.", firebaseFirestoreException)
                return@addSnapshotListener
            }
            if (documentSnapshot != null) {
                val user = documentSnapshot.toObject(User::class.java)
                if (user != null) {
                    user.fullName = "${user.firstName} ${user.lastName}"
                    currentUser.value = user
                }
            }
        }
        return currentUser
    }

    fun updateCurrentUser(
        name: String = "",
        bio: String = "",
        profilePicturePath: String? = null,
        username: String = "",
        website: String = ""
    ) {
        val userFieldMap = mutableMapOf<String, Any>()
        if (name.isNotBlank()) userFieldMap["name"] = name
        if (username.isNotBlank()) userFieldMap["username"] = username
        if (bio.isNotBlank()) userFieldMap["bio"] = bio
        if (website.isNotBlank()) userFieldMap["website"] = website
        if (profilePicturePath != null)
            userFieldMap["profilePicturePath"] = profilePicturePath
        currentUserDocRef.update(userFieldMap)

    }

    fun updateName(firstName: String, lastName: String) {
        if (firstName.isBlank() && lastName.isBlank())
            return

        val updates = mutableMapOf<String, String>()

        if (firstName.isNotBlank())
            updates["firstName"] = firstName

        if (lastName.isNotBlank())
            updates["lastName"] = lastName

        currentUserDocRef
            .update(updates as Map<String, String>)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
    }

    fun setDarkMode(darkMode: Boolean) {
        currentUserDocRef.update("darkMode", darkMode)
            .addOnSuccessListener { }
            .addOnFailureListener { }
    }

    //region FCM
    fun getFCMRegistrationTokens(onComplete: (tokens: MutableList<String>) -> Unit) {
        currentUserDocRef.get().addOnSuccessListener {
            val user = it.toObject(User::class.java)!!
//            onComplete(user.registrationTokens)
        }
    }

    fun setFCMRegistrationTokens(registrationTokens: MutableList<String>) {
        currentUserDocRef.update(mapOf("registrationTokens" to registrationTokens))
    }
    //endregion FCM

}