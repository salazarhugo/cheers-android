package com.salazar.cheers.util

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.functions.FirebaseFunctions
import com.salazar.cheers.backend.Neo4jUtil
import com.salazar.cheers.internal.Comment
import com.salazar.cheers.internal.Source
import com.salazar.cheers.internal.User
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch


object FirestoreUtil {

    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val stripeCustomerDocRef: DocumentReference
        get() = firestoreInstance.document(
            "stripe_customers/${
                FirebaseAuth.getInstance().uid
                    ?: throw NullPointerException("UID is null.")
            }"
        )
    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document(
            "users/${
                FirebaseAuth.getInstance().uid
                    ?: throw NullPointerException("UID is null.")
            }"
        )

    fun sendGift(
        receiverId: String,
        price: Int = 50
    ): Task<HashMap<*, *>> {
        // Create the arguments to the callable function.
        val data = hashMapOf(
            "receiverId" to receiverId,
            "price" to price,
        )

        return FirebaseFunctions.getInstance("europe-west2")
            .getHttpsCallable("sendGift")
            .call(data)
            .continueWith { task ->
                val result = task.result?.data as HashMap<*, *>
                result
            }
    }


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
        name: String = "",
        onComplete: (exists: Boolean) -> Unit,
    ) {
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                onComplete(true)
                return@addOnSuccessListener
            }

            val id = FirebaseAuth.getInstance().currentUser!!.uid
//            val fullName = if (acct != null) "${acct.givenName} ${acct.familyName}" else ""
            val profilePicturePath = if (acct?.photoUrl != null) acct.photoUrl.toString() else ""

            val newUser = User().copy(
                id = id,
                name = name,
                username = username,
                profilePictureUrl = profilePicturePath,
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

    fun getComments(postId: String): Flow<List<Comment>> = callbackFlow {

        val commentsDocument = firestoreInstance
            .collection("comments")
            .whereEqualTo("postId", postId)
            .orderBy("created", Query.Direction.DESCENDING)

        val subscription = commentsDocument.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e(FirestoreChat.TAG, "Users listener error.", e)
                return@addSnapshotListener
            }
            val items = mutableListOf<Comment>()
            snapshot!!.forEach {
                items.add(it.toObject(Comment::class.java))
                return@forEach
            }
            this.trySend(items).isSuccess
        }

        awaitClose {
            subscription.remove()
        }
    }

    fun addComment(comment: Comment) {
        val commentDoc = firestoreInstance.collection("comments")
            .document()
        commentDoc.set(comment.copy(id = commentDoc.id))
    }

    fun listenSources() = callbackFlow<List<Source>> {

        val sourcesRef = stripeCustomerDocRef
            .collection("sources")

        val subscription = sourcesRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e(FirestoreChat.TAG, "Users listener error.", e)
                return@addSnapshotListener
            }

            val sources = mutableListOf<Source>()
            snapshot!!.forEach {
                sources.add(it.toObject(Source::class.java))
            }

            this.trySend(sources).isSuccess
        }

        awaitClose {
            subscription.remove()
        }
    }

    fun getUserCoins() = callbackFlow<Int> {

        val subscription = currentUserDocRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e(FirestoreChat.TAG, "Users listener error.", e)
                return@addSnapshotListener
            }

            val coins = snapshot?.data?.get("coins") ?: return@addSnapshotListener

            this.trySend((coins as Long).toInt()).isSuccess
        }

        awaitClose {
            subscription.remove()
        }
    }


    suspend fun listenUser(): Flow<User> = callbackFlow {
        val subscription = currentUserDocRef.addSnapshotListener { doc, e ->
            if (e != null) {
                Log.e(TAG, "Listen failed", e)
                return@addSnapshotListener
            }

            if (doc == null || !doc.exists())
                return@addSnapshotListener

            val pro = doc.toObject(User::class.java)!!
            trySend(pro).isSuccess
        }

        awaitClose {
            subscription.remove()
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
                currentUser.value = user
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

    fun updateName(
        firstName: String,
        lastName: String
    ) {
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

    fun addFCMRegistrationToken(token: String) {
        currentUserDocRef.update("registrationTokens", FieldValue.arrayUnion(token))
    }
//endregion FCM

}