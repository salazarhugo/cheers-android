package com.salazar.cheers.backend

import android.util.Log
import com.beust.klaxon.Klaxon
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.salazar.cheers.data.Result
import com.salazar.cheers.data.entities.StoryResponse
import com.salazar.cheers.data.entities.UserStats
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class Neo4jService {

    suspend fun getStoryFeed(
        page: Int,
        pageSize: Int
    ): Result<List<Pair<StoryResponse, List<User>>>> = withContext(Dispatchers.IO) {

        val data = hashMapOf(
            "page" to page,
            "pageSize" to pageSize,
        )

        return@withContext FirebaseFunctions.getInstance("europe-west2")
            .getHttpsCallable("getStoryFeed")
            .call(data)
            .continueWith { task ->
                val posts = mutableListOf<Pair<StoryResponse, List<User>>>()
                try {
                    val result = task.result?.data as HashMap<*, *>
                    val response =
                        Gson().fromJson(result["response"].toString(), JsonArray::class.java)

                    response.asJsonArray.forEach { postFeed ->
                        val post =
                            Klaxon().parse<StoryResponse>(postFeed.asJsonObject["story"].toString())!!
                        val author =
                            Klaxon().parse<User>(postFeed.asJsonObject["author"].toString())!!
                        val tagUsers =
                            Klaxon().parseArray<User>(postFeed.asJsonObject["tags"].toString())
                                ?: emptyList()

                        posts.add(Pair(post, tagUsers + author))
                    }
                    return@continueWith Result.Success(posts.toList())
                } catch (e: Exception) {
                    Log.e("Cloud", "Failed to parse posts: $e")
                    return@continueWith Result.Error(e)
                }
            }
            .await()
    }

    suspend fun getPostFeed(
        page: Int,
        pageSize: Int
    ): Result<List<Pair<Post, List<User>>>> = withContext(Dispatchers.IO) {

        val data = hashMapOf(
            "page" to page,
            "pageSize" to pageSize,
        )

        return@withContext FirebaseFunctions.getInstance("europe-west2")
            .getHttpsCallable("getPostFeed")
            .call(data)
            .continueWith { task ->
                val posts = mutableListOf<Pair<Post, List<User>>>()
                try {
                    val result = task.result?.data as HashMap<*, *>
                    val response =
                        Gson().fromJson(result["response"].toString(), JsonArray::class.java)

                    response.asJsonArray.forEach { postFeed ->
                        val post = Klaxon().parse<Post>(postFeed.asJsonObject["post"].toString())!!
                        val author =
                            Klaxon().parse<User>(postFeed.asJsonObject["author"].toString())!!
                        val tagUsers =
                            Klaxon().parseArray<User>(postFeed.asJsonObject["users"].toString())
                                ?: emptyList()

                        posts.add(Pair(post.copy(accountId = FirebaseAuth.getInstance().currentUser?.uid!!), tagUsers + author))
                    }
                    Log.d("Cloud Posts:", posts.toString())
                    return@continueWith Result.Success(posts.toList())
                } catch (e: Exception) {
                    Log.e("Cloud", "Failed to parse posts: $e")
                    return@continueWith Result.Error(e)
                }
            }
            .await()
    }

    suspend fun getUser(userIdOrUsername: String): Result<User?> = withContext(Dispatchers.IO) {
        val data = hashMapOf(
            "userIdOrUsername" to userIdOrUsername,
        )

        return@withContext FirebaseFunctions.getInstance("europe-west2")
            .getHttpsCallable("getUser")
            .call(data)
            .continueWith { task ->
                if (task.result == null || task.result.data == null)
                    return@continueWith  Result.Error(java.lang.Exception("Network error"))

                if (task.result.data.toString() == "[]")
                    return@continueWith Result.Error(java.lang.Exception("User doesn't exist."))

                val result = task.result.data as HashMap<*, *>
                Log.d("Cloud", result["response"].toString())

                val user = Klaxon().parse<User>(result["response"].toString())

                return@continueWith Result.Success(user)
            }
            .await()
    }

    suspend fun getSuggestions(): Result<List<User>> = withContext(Dispatchers.IO) {
        return@withContext Result.Success(emptyList())
//        return withContext(Dispatchers.IO) {
//            try {
//                val params: MutableMap<String, Any> = mutableMapOf()
//                params["userId"] = FirebaseAuth.getInstance().uid!!
//                params["pageSize"] = 10
//                params["skip"] = 0 * 10
//
//                val records = query(
//                    "MATCH (u:User { id: \$userId})-[:FOLLOWS]->(f:User)-[:FOLLOWS]->(suggestion:User) " +
//                            "WHERE suggestion.id <> \$userId " +
//                            "AND NOT (u)-[:FOLLOWS]->(suggestion) " +
//                            "WITH suggestion SKIP \$skip LIMIT \$pageSize\n" +
//                            "OPTIONAL MATCH (suggestion)-[posts:POSTED]->(:Post)\n" +
//                            "OPTIONAL MATCH (suggestion)-[following:FOLLOWS]->(:User)\n" +
//                            "OPTIONAL MATCH (:User)-[followers:FOLLOWS]->(suggestion)\n" +
//                            "WITH suggestion, count(DISTINCT posts) as posts, count(DISTINCT followers) as followers, count(DISTINCT following) as following, exists((u)-[:FOLLOWS]->(suggestion)) as isFollowed\n" +
//                            "RETURN suggestion {.*,  postCount: posts, followers: followers, following: following, isFollowed: isFollowed }",
//                    params
//                )
//
//                val users = mutableListOf<User>()
//
//                records.forEach { record ->
//                    val gson = Gson()
//                    val user =
//                        gson.fromJson(record.values()[0].toString(), User::class.java)
//                    users.add(user)
//                }
//
//                return@withContext Result.Success(users.toList())
//            } catch (e: Exception) {
//                return@withContext Result.Error(e)
//            }
//        }
    }

    suspend fun seenStory(storyId: String) = withContext(Dispatchers.IO) {
        val data = hashMapOf(
            "storyId" to storyId,
        )

        return@withContext FirebaseFunctions.getInstance("europe-west2")
            .getHttpsCallable("seenStory")
            .call(data)
            .continueWith { task ->
                val result = task.result?.data as String
                result
            }
    }

    suspend fun getUserStats(username: String): Result<UserStats> = withContext(Dispatchers.IO) {
        return@withContext Result.Success(UserStats())
//        return withContext(Dispatchers.IO) {
//            try {
//                val params: MutableMap<String, Any> = mutableMapOf()
//                params["username"] = username
//                params["userId"] = FirebaseAuth.getInstance().currentUser?.uid!!
//
//                val records = query(
//                    "MATCH (u:User { username: \$username})-[:POSTED]-(p:Post)\n" +
//                            "WITH u, p.beverage as favDrink, count(p.beverage) as occ ORDER BY occ DESC LIMIT 1\n" +
//                            "MATCH (u)-[:POSTED]->(p:Post)\n" +
//                            "WITH u, favDrink, count(p.beverage) as drinks, avg(p.drunkenness) as avgDrunkenness, max(p.drunkenness) as maxDrunkenness\n" +
//                            "RETURN { id: u.id, username: u.username, favoriteDrink: favDrink, drinks: drinks," +
//                            " avgDrunkenness: avgDrunkenness, maxDrunkenness: maxDrunkenness }",
//                    params
//                )
//
//                val userStats =
//                    Gson().fromJson(records[0].values()[0].toString(), UserStats::class.java)
//
//                return@withContext Result.Success(userStats)
//            } catch (e: Exception) {
//                return@withContext Result.Error(e)
//            }
//        }
    }

    suspend fun getFollowersFollowing(userIdOrUsername: String): Pair<List<User>, List<User>> = withContext(Dispatchers.IO) {
            val data = hashMapOf(
                "userIdOrUsername" to userIdOrUsername,
            )

        return@withContext FirebaseFunctions.getInstance("europe-west2")
            .getHttpsCallable("getFollowersFollowing")
            .call(data)
            .continueWith { task ->
                try {
                    val result = task.result?.data as HashMap<*, *>
                    val response =
                        Gson().fromJson(result["response"].toString(), com.google.gson.JsonObject::class.java)
                    val followers = Klaxon().parseArray<User>(response["followers"].toString()) ?: emptyList()
                    val following = Klaxon().parseArray<User>(response["following"].toString()) ?: emptyList()
                    Pair(followers, following)
                }catch (e:Exception) {
                    Log.e("Cloud", e.toString())
                    Pair(emptyList(), emptyList())
                }
            }
            .await()
    }

    suspend fun queryUsers(query: String): List<User> = withContext(Dispatchers.IO) {
        // Create the arguments to the callable function.
        val data = hashMapOf(
            "query" to query,
        )

        return@withContext FirebaseFunctions.getInstance("europe-west2")
            .getHttpsCallable("queryUser")
            .call(data)
            .continueWith { task ->
                val result = task.result?.data as HashMap<*, *>
                val user = Klaxon().parseArray<User>(result["response"].toString()) ?: emptyList()
                user
            }
            .await()
    }

    suspend fun deleteStory(storyId: String) = withContext(Dispatchers.IO) {
        val data = hashMapOf(
            "storyId" to storyId,
        )

        return@withContext FirebaseFunctions.getInstance("europe-west2")
            .getHttpsCallable("deleteStory")
            .call(data)
            .continueWith { task ->
                val result = task.result?.data as String
                result
            }
    }

    suspend fun deletePost(postId: String) = withContext(Dispatchers.IO) {
        val data = hashMapOf(
            "postId" to postId,
        )

        return@withContext FirebaseFunctions.getInstance("europe-west2")
            .getHttpsCallable("deletePost")
            .call(data)
            .continueWith { task ->
                val result = task.result?.data as String
                result
            }
    }

    suspend fun unlikePost(postId: String) = withContext(Dispatchers.IO) {
        val data = hashMapOf(
            "postId" to postId,
        )

        return@withContext FirebaseFunctions.getInstance("europe-west2")
            .getHttpsCallable("unlikePost")
            .call(data)
            .continueWith { task ->
                val result = task.result?.data as String
                result
            }
    }

    suspend fun likePost(postId: String) = withContext(Dispatchers.IO) {
        val data = hashMapOf(
            "postId" to postId,
        )

        return@withContext FirebaseFunctions.getInstance("europe-west2")
            .getHttpsCallable("likePost")
            .call(data)
            .continueWith { task ->
                val result = task.result?.data as String
                result
            }
    }

    suspend fun blockUser(otherUserId: String) = withContext(Dispatchers.IO) {
        val data = hashMapOf(
            "otherUserId" to otherUserId,
        )

        return@withContext FirebaseFunctions.getInstance("europe-west2")
            .getHttpsCallable("blockUser")
            .call(data)
            .continueWith { task ->
                val result = task.result?.data as String
                result
            }
    }


    suspend fun unfollowUser(username: String) = withContext(Dispatchers.IO) {
        // Create the arguments to the callable function.
        val data = hashMapOf(
            "username" to username,
        )

        return@withContext FirebaseFunctions.getInstance("europe-west2")
            .getHttpsCallable("unfollowUser")
            .call(data)
            .continueWith { task ->
                val result = task.result?.data as String
                result
            }
    }

    suspend fun followUser(username: String) = withContext(Dispatchers.IO) {
        // Create the arguments to the callable function.
        val data = hashMapOf(
            "username" to username,
        )

        return@withContext FirebaseFunctions.getInstance("europe-west2")
            .getHttpsCallable("followUser")
            .call(data)
            .continueWith { task ->
                val result = task.result?.data as String
                Log.i("Cloud Functions", result)
                result
            }
    }
}