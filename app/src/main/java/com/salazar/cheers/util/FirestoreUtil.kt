package com.salazar.cheers.util

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.salazar.cheers.internal.Source
import com.salazar.cheers.internal.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow


object FirestoreUtil {
//    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
//    private val currentUserDocRef: DocumentReference
//        get() = firestoreInstance.document(
//            "users/${
//                FirebaseAuth.getInstance().uid
//                    ?: throw NullPointerException("UID is null.")
//            }"
//        )
//
//    fun sendGift(
//        receiverId: String,
//        price: Int = 50
//    ): Task<HashMap<*, *>> {
//        // Create the arguments to the callable function.
//        val data = hashMapOf(
//            "receiverId" to receiverId,
//            "price" to price,
//        )
//
//        return FirebaseFunctions.getInstance("europe-west2")
//            .getHttpsCallable("sendGift")
//            .call(data)
//            .continueWith { task ->
//                val result = task.result?.data as HashMap<*, *>
//                result
//            }
//    }
//
//    fun listenSources() = callbackFlow<List<Source>> {
//
//        val sourcesRef = stripeCustomerDocRef
//            .collection("sources")
//
//        val subscription = sourcesRef.addSnapshotListener { snapshot, e ->
//            if (e != null) {
//                return@addSnapshotListener
//            }
//
//            val sources = mutableListOf<Source>()
//            snapshot!!.forEach {
//                sources.add(it.toObject(Source::class.java))
//            }
//
//            this.trySend(sources).isSuccess
//        }
//
//        awaitClose {
//            subscription.remove()
//        }
//    }
//
//    fun getUserCoins() = callbackFlow<Int> {
//        val subscription = currentUserDocRef.addSnapshotListener { snapshot, e ->
//            if (e != null) {
//                return@addSnapshotListener
//            }
//
//            val coins = snapshot?.data?.get("coins") ?: return@addSnapshotListener
//
//            this.trySend((coins as Long).toInt()).isSuccess
//        }
//
//        awaitClose {
//            subscription.remove()
//        }
//    }
//
//    fun getFCMRegistrationTokens(onComplete: (tokens: MutableList<String>) -> Unit) {
//        currentUserDocRef.get().addOnSuccessListener {
//            val user = it.toObject(User::class.java)!!
////            onComplete(user.registrationTokens)
//        }
//    }
//
//    fun addFCMRegistrationToken(token: String) {
//        currentUserDocRef.update("registrationTokens", FieldValue.arrayUnion(token))
//    }
}