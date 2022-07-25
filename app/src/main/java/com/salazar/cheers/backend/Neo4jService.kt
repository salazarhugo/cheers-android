package com.salazar.cheers.backend

import com.salazar.cheers.data.Result
import com.salazar.cheers.data.entities.UserStats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class Neo4jService {

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
}