package com.salazar.cheers.backend

import com.google.firebase.functions.FirebaseFunctions
import com.salazar.cheers.data.Result
import com.salazar.cheers.internal.User


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