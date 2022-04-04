package com.salazar.cheers.backend

import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
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
//        val params: MutableMap<String, Any> = mutableMapOf()
//        params["userId"] = FirebaseAuth.getInstance().currentUser?.uid!!
//
//        val finalStory = story.copy(
//            id = randomUUID().toString(),
//            authorId = FirebaseAuth.getInstance().currentUser?.uid!!,
//        )
//
//        params["story"] = toMap(finalStory)
//        params["tagUsersId"] = finalStory.tagUsersId
//
//        write(
//            "MATCH (u:User) WHERE u.id = \$userId CREATE (s: Story \$story)" +
//                    " SET s += { created: datetime().epochMillis } CREATE (u)-[:POSTED]->(s)" +
//                    " WITH s UNWIND \$tagUsersId as tagUserId MATCH (u2:User {id: tagUserId}) CREATE (s)-[:WITH]->(u2)",
//            params = params
//        )
    }

    suspend fun addPost(post: Post) = withContext(Dispatchers.IO) {
//        val params: MutableMap<String, Any> = mutableMapOf()
//        params["userId"] = FirebaseAuth.getInstance().currentUser?.uid!!
//
//        val finalPost = post.copy(
//            id = randomUUID().toString(),
//            authorId = FirebaseAuth.getInstance().currentUser?.uid!!,
//        )
//
//        params["post"] = toMap(finalPost)
//        params["tagUsersId"] = finalPost.tagUsersId
//
//        write(
//            "MATCH (u:User) WHERE u.id = \$userId CREATE (p: Post \$post)" +
//                    " SET p += { created: datetime().epochMillis, duration: duration({ hours: 2 }) } CREATE (u)-[:POSTED]->(p)" +
//                    " WITH p UNWIND \$tagUsersId as tagUserId MATCH (u2:User {id: tagUserId}) CREATE (p)-[:WITH]->(u2)",
//            params = params
//        )
//        sendPostNotification(finalPost.id)
    }

    private fun sendPostNotification(postId: String): Task<String> {
        val data = hashMapOf(
            "postId" to postId,
        )

        return FirebaseFunctions.getInstance("europe-west2")
            .getHttpsCallable("postNotification")
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

    suspend fun getFollowing(username: String? = null): Result<List<User>> {
        return Result.Success(emptyList())
//        return withContext(Dispatchers.IO) {
//            try {
//                val params: MutableMap<String, Any> = mutableMapOf()
//                params["userId"] = FirebaseAuth.getInstance().currentUser?.uid!!
//                params["userIdOrUsername"] =
//                    username ?: FirebaseAuth.getInstance().currentUser?.uid!!
//
//                val records = query(
//                    "MATCH (u:User)-[:FOLLOWS]->(f:User)\n" +
//                            "MATCH (me:User {id: \$userId})\n" +
//                            "WHERE u.username = \$userIdOrUsername OR u.id = \$userIdOrUsername\n" +
//                            "WITH f, exists((me)-[:FOLLOWS]->(f)) as isFollowed\n" +
//                            "RETURN f { .*, isFollowed: isFollowed }",
//                    params
//                )
//
//                val following = mutableListOf<User>()
//
//                records.forEach { record ->
//                    val user =
//                        Gson().fromJson(record.values()[0].toString(), User::class.java)
//                    following.add(user)
//                }
//
//                return@withContext Result.Success(following.toList())
//            } catch (e: Exception) {
//                Log.e("Neo4j", e.toString())
//                return@withContext Result.Error(e)
//            }
//        }
    }

    suspend fun getFollowers(username: String? = null): Result<List<User>> {
        return Result.Success(emptyList())
//        return withContext(Dispatchers.IO) {
//            try {
//                val params: MutableMap<String, Any> = mutableMapOf()
//
//                if (username != null)
//                    params["username"] = username
//                else
//                    params["userId"] = FirebaseAuth.getInstance().uid!!
//
//                val records = if (username != null)
//                    query(
//                        "MATCH (follower:User)-[:FOLLOWS]->(u:User) WHERE u.username = \$username RETURN properties(follower)",
//                        params
//                    )
//                else
//                    query(
//                        "MATCH (follower:User)-[:FOLLOWS]->(u:User) WHERE u.id = \$userId RETURN properties(follower)",
//                        params
//                    )
//
//                val followers = mutableListOf<User>()
//
//                records.forEach { record ->
//                    Log.d("HAHA", record.values().size.toString())
//                    val gson = Gson()
//                    val user =
//                        gson.fromJson(record.values()[0].toString(), User::class.java)
//                    followers.add(user)
//                }
//
//                return@withContext Result.Success(followers.toList())
//            } catch (e: Exception) {
//                Log.e("Neo4j", e.toString())
//                return@withContext Result.Error(e)
//            }
//        }
    }

    suspend fun addRegistrationToken(registrationToken: String)  = withContext(Dispatchers.IO) {
        val data = hashMapOf(
            "registrationToken" to registrationToken,
        )

        FirebaseFunctions.getInstance("europe-west2")
            .getHttpsCallable("setRegistrationToken")
            .call(data)
            .await()
    }
}