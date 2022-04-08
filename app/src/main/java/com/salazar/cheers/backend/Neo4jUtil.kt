package com.salazar.cheers.backend

import com.google.firebase.functions.FirebaseFunctions
import com.google.gson.Gson
import com.salazar.cheers.data.Result
import com.salazar.cheers.data.entities.StoryResponse
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

    suspend fun addUser(user: User) {
        val data = hashMapOf(
            "user" to toMap(user),
        )

        FirebaseFunctions.getInstance("europe-west2")
            .getHttpsCallable("createUser")
            .call(data)
            .continueWith { task ->
                val result = task.result?.data as String
                result
            }
            .await()
    }

    fun updateProfilePicture(profilePictureUrl: String) {
//        val params: MutableMap<String, Any> = mutableMapOf()
//        params["profilePictureUrl"] = profilePictureUrl
//        params["userId"] = FirebaseAuth.getInstance().uid!!
//
//        write(
//            "MATCH (u:User { id: \$userId }) SET u.profilePictureUrl = \$profilePictureUrl",
//            params
//        )
    }

    suspend fun updateUser(
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

    fun addEvent(event: Event) {
//        try {
//            val params: MutableMap<String, Any> = mutableMapOf()
//            params["userId"] = FirebaseAuth.getInstance().uid!!
//            params["event"] = toMap(event)
//            write(
//                "MATCH (u:User { id: \$userId}) CREATE (e: Event \$event)" +
//                        " SET e += { created: datetime().epochMillis, startDate: datetime(e.startDate), endDate: datetime(e.endDate)} CREATE (u)-[:POSTED]->(e)" +
//                        " WITH e UNWIND e.participants as tagUserId MATCH (u2:User {id: tagUserId}) CREATE (p)-[:WITH]->(u2)",
//                params = params
//            )
//        } catch (e: Exception) {
//            Log.e("HAHA", e.toString())
//        }
    }

    suspend fun addStory(story: StoryResponse) = withContext(Dispatchers.IO) {
        val json =  Gson().toJson(story)

        val data = hashMapOf(
            "story" to json,
        )

        FirebaseFunctions.getInstance("europe-west2")
            .getHttpsCallable("createStory")
            .call(data)
            .continueWith { task ->
                val result = task.result?.data as String
                result
            }
    }

    fun addPost(post: Post) {
        val json =  Gson().toJson(post)

        val data = hashMapOf(
            "post" to json,
        )

        FirebaseFunctions.getInstance("europe-west2")
            .getHttpsCallable("createPost")
            .call(data)
            .continueWith { task ->
                val result = task.result?.data as String
                result
            }
    }

    suspend fun leavePost(postId: String) {
        val data = hashMapOf(
            "postId" to postId,
        )

        FirebaseFunctions.getInstance("europe-west2")
            .getHttpsCallable("leavePost")
            .call(data)
            .continueWith { task ->
                val result = task.result?.data as String
                result
            }
    }

    suspend fun getPostLikes(postId: String): Result<List<User>> {
        return Result.Success(emptyList())
//        return withContext(Dispatchers.IO) {
//            try {
//                val params: MutableMap<String, Any> = mutableMapOf()
//                params["postId"] = postId
//
//                val records = query(
//                    "MATCH (p:Post { id: \$postId})<-[:LIKED]-(u:User) " +
//                            "RETURN properties(u)",
//                    params
//                )
//
//                val users = mutableListOf<User>()
//
//                records.forEach { record ->
//                    val gson = Gson()
//                    val user = gson.fromJson(record.values()[0].toString(), User::class.java)
//                    users.add(user)
//                }
//                return@withContext Result.Success(users.toList())
//            } catch (e: Exception) {
//                return@withContext Result.Error(e)
//            }
//        }
    }

    suspend fun queryFriends(query: String): List<User> {
        return emptyList()
//        return withContext(Dispatchers.IO) {
//            val params: MutableMap<String, Any> = mutableMapOf()
//            params["query"] = query
//            params["userId"] = FirebaseAuth.getInstance().uid!!
//
//            val records = query(
//                "MATCH (u:User)-[:FOLLOWS]->(u2:User) " +
//                        "WHERE u.id = \$userId AND u2.username CONTAINS \$query AND u2.id <> \$userId " +
//                        "RETURN properties(u2) LIMIT 20",
//                params
//            )
//
//            val users = mutableListOf<User>()
//
//            records.forEach { record ->
//                val gson = Gson()
//                val user =
//                    gson.fromJson(record.values()[0].toString(), User::class.java)
//                users.add(user)
//            }
//
//            return@withContext users.toList()
//        }
    }

    suspend fun isUsernameAvailable(username: String): Result<Boolean> =
        withContext(Dispatchers.IO) {
            val data = hashMapOf(
                "username" to username,
            )

            return@withContext FirebaseFunctions.getInstance("europe-west2")
                .getHttpsCallable("isUsernameAvailable")
                .call(data)
                .continueWith { task ->
                    val result = task.result?.data as HashMap<*, *>
                    val available = result["response"] as Boolean
                    Result.Success(available)
                }
                .await()
        }

    suspend fun addRegistrationToken(registrationToken: String) = withContext(Dispatchers.IO) {
        val data = hashMapOf(
            "registrationToken" to registrationToken,
        )

        FirebaseFunctions.getInstance("europe-west2")
            .getHttpsCallable("setRegistrationToken")
            .call(data)
            .await()
    }
}