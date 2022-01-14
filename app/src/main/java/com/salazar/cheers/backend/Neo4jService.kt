package com.salazar.cheers.backend

import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.salazar.cheers.data.Result
import com.salazar.cheers.internal.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.neo4j.driver.*
import java.lang.reflect.Type
import java.util.*
import java.util.concurrent.TimeUnit


class Neo4jService {

    companion object {
        private val driver: Driver by lazy {
            GraphDatabase.driver(
                Environment.DEFAULT_URL,
                AuthTokens.basic(Environment.DEFAULT_USER, Environment.DEFAULT_PASS),
                Config.builder()
                    .withMaxConnectionLifetime(8, TimeUnit.MINUTES)
                    .withConnectionLivenessCheckTimeout(2, TimeUnit.MINUTES).build()
            )
        }
        const val database: String = Environment.DEFAULT_DATABASE
    }

    // page index zero
    suspend fun events(page: Int, pageSize: Int): Result<List<EventUi>> {
        return withContext(Dispatchers.IO) {
            try {
                val params: MutableMap<String, Any> = mutableMapOf()
                params["userId"] = FirebaseAuth.getInstance().uid!!
                params["pageSize"] = pageSize
                params["skip"] = page * pageSize

                val records = query(
                    "MATCH (u:User {id: \$userId})-[:FOLLOWS*0..1]->(f:User)-[:POSTED]->(e:Event)\n" +
                            "OPTIONAL MATCH (:User)-[r:LIKED]->(e) \n" +
                            "OPTIONAL MATCH (e)-[:WITH]->(e:Event) \n" +
                            "RETURN DISTINCT e { .*, createdTime: apoc.temporal.format(e.createdTime)}, properties(f) " +
                            "ORDER BY datetime(e.createdTime) DESC " +
                            "SKIP \$skip LIMIT \$pageSize",
                    params
                )

                val events = mutableListOf<EventUi>()

                records.forEach { record ->
                    val gson = Gson()

                    val event =
                        gson.fromJson(record.values()[0].toString(), Event::class.java)
                    val user =
                        gson.fromJson(record.values()[1].toString(), User::class.java)

                    val userListType: Type =
                        object : TypeToken<ArrayList<User>>() {}.type

//                    val s = record.values()[2].toString()
//                    val tagUsers =
//                        gson.fromJson<ArrayList<User>>(s, userListType)

                    events.add(
                        EventUi(
                            event = event,
                            host = user,
                            participants = emptyList(),
                        )
                    )
                }
                return@withContext Result.Success(events.toList())
            } catch (e: Exception) {
                return@withContext Result.Error(e)
            }
        }
    }

    // page index zero
    suspend fun posts(page: Int, pageSize: Int): Result<List<Post>> {
        return withContext(Dispatchers.IO) {
            try {
                val params: MutableMap<String, Any> = mutableMapOf()
                params["userId"] = FirebaseAuth.getInstance().uid!!
                params["pageSize"] = pageSize
                params["skip"] = page * pageSize

                val records = query(
                    "MATCH (u:User {id: \$userId})-[:FOLLOWS*0..1]->(f:User)-[:POSTED]->(p:Post)\n" +
                            "OPTIONAL MATCH (:User)-[r:LIKED]->(p) \n" +
                            "OPTIONAL MATCH (p)-[:WITH]->(w:User) \n" +
                            "RETURN p {.*, likes: count(DISTINCT r), liked: exists((u)-[:LIKED]->(p)), createdTime: toString(p.createdTime)}," +
                            " properties(f), collect(DISTINCT properties(w)) " +
                            "ORDER BY datetime(p.createdTime) DESC " +
                            "SKIP \$skip LIMIT \$pageSize",
                    params
                )

                val posts = mutableListOf<Post>()

                records.forEach { record ->
                    val gson = Gson()

                    val post =
                        gson.fromJson(record.values()[0].toString(), Post::class.java)
                    val user =
                        gson.fromJson(record.values()[1].toString(), User::class.java)

                    val userListType: Type =
                        object : TypeToken<ArrayList<User>>() {}.type

                    val s = record.values()[2].toString()
                    val tagUsers =
                        gson.fromJson<ArrayList<User>>(s, userListType)

                    posts.add(
                        post.copy(
                            creator = user,
                            tagUsers = tagUsers,
                        )
                    )
                }
                return@withContext Result.Success(posts.toList())
            } catch (e: Exception) {
                return@withContext Result.Error(e)
            }
        }
    }

    private fun query(query: String, params: MutableMap<String, Any>): List<Record> {
        getSession().use { session ->
            return session.readTransaction { tx ->
                tx.run(query, params).list()
            }
        }
    }

    private fun getSession(): Session {
        return driver.session(SessionConfig.forDatabase(Environment.DEFAULT_DATABASE))
    }
}