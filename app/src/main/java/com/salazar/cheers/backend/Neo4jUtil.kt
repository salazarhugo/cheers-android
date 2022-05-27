package com.salazar.cheers.backend

import com.google.firebase.functions.FirebaseFunctions
import com.google.gson.Gson
import com.salazar.cheers.data.Result
import com.salazar.cheers.data.entities.Story
import com.salazar.cheers.internal.Event
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


object Neo4jUtil {

    fun toMap(obj: Any): Map<String, Any> {
        val map: MutableMap<String, Any> = HashMap()
        for (field in obj.javaClass.declaredFields) {
            field.isAccessible = true
            try {
                map[field.name] = field[obj]
            } catch (e: Exception) {
            }
        }
        return map
    }

    fun updateUser(
        username: String = "",
        name: String = "",
        bio: String = "",
        website: String = "",
        profilePictureUrl: String = "",
    ) {
        val data = hashMapOf(
            "username" to username,
            "name" to name,
            "bio" to bio,
            "website" to website,
            "profilePictureUrl" to profilePictureUrl,
        )

        FirebaseFunctions.getInstance("europe-west2")
            .getHttpsCallable("updateUser")
            .call(data)
            .continueWith { task ->
                val result = task.result?.data as String
                result
            }
    }


    suspend fun getPostLikes(postId: String): Result<List<User>> {
        return Result.Success(emptyList())
    }

    suspend fun queryFriends(query: String): List<User> {
        return emptyList()
    }
}