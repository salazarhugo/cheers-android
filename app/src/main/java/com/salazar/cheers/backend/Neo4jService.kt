package com.salazar.cheers.backend

import android.util.Log
import com.beust.klaxon.Klaxon
import com.google.firebase.functions.FirebaseFunctions
import com.google.gson.Gson
import com.salazar.cheers.data.Result
import com.salazar.cheers.data.entities.UserStats
import com.salazar.cheers.internal.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class Neo4jService {

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

    suspend fun getFollowersFollowing(userIdOrUsername: String): Pair<List<User>, List<User>> =
        withContext(Dispatchers.IO) {
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
                            Gson().fromJson(
                                result["response"].toString(),
                                com.google.gson.JsonObject::class.java
                            )
                        val followers = Klaxon().parseArray<User>(response["followers"].toString())
                            ?: emptyList()
                        val following = Klaxon().parseArray<User>(response["following"].toString())
                            ?: emptyList()
                        Pair(followers, following)
                    } catch (e: Exception) {
                        Log.e("Cloud", e.toString())
                        Pair(emptyList(), emptyList())
                    }
                }
                .await()
        }

}