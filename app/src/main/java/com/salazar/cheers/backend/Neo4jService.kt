package com.salazar.cheers.backend

import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.salazar.cheers.data.Result
import com.salazar.cheers.data.entities.StoryResponse
import com.salazar.cheers.internal.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.neo4j.driver.*
import java.lang.reflect.Type
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

    suspend fun getUser(userIdOrUsername: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val params: MutableMap<String, Any> = mutableMapOf()
                params["userIdOrUsername"] = userIdOrUsername
                params["currentUserId"] = FirebaseAuth.getInstance().currentUser?.uid!!

                val records = query(
                    "MATCH (u:User) WHERE u.id = \$userIdOrUsername OR u.username = \$userIdOrUsername\n" +
                            "OPTIONAL MATCH (u)-[r:POSTED]->(:Post)\n" +
                            "OPTIONAL MATCH (u)-[f:FOLLOWS]->(:User)\n" +
                            "OPTIONAL MATCH (:User)-[f2:FOLLOWS]->(u)\n" +
                            "RETURN u {.*, postCount: count(DISTINCT r), isFollowed: exists( (:User{id:\$currentUserId})-[:FOLLOWS]->(u) ), following: count(DISTINCT f), followers: count(DISTINCT f2)}",
                    params
                )

                val user = Gson().fromJson(records[0].values()[0].toString(), User::class.java)

                return@withContext Result.Success(user)
            } catch (e: Exception) {
                return@withContext Result.Error(e)
            }
        }
    }

    // page index zero
    suspend fun events(
        page: Int,
        pageSize: Int
    ): Result<List<EventUi>> {
        return withContext(Dispatchers.IO) {
            try {
                val params: MutableMap<String, Any> = mutableMapOf()
                params["userId"] = FirebaseAuth.getInstance().uid!!
                params["pageSize"] = pageSize
                params["skip"] = page * pageSize

                val records = query(
                    "MATCH (u:User {id: \$userId})-[:FOLLOWS*0..1]->(author:User)-[:POSTED]->(e:Event)\n" +
                            "OPTIONAL MATCH (author)-[authorPosts:POSTED]->(:Post)\n" +
                            "OPTIONAL MATCH (author)-[authorFollowing:FOLLOWS]->(:User)\n" +
                            "OPTIONAL MATCH (:User)-[authorFollowers:FOLLOWS]->(author)\n" +
                            "OPTIONAL MATCH (:User)-[r:LIKED]->(e) \n" +
                            "OPTIONAL MATCH (e)-[:WITH]->(e:Event) \n" +
                            "RETURN DISTINCT e { .*, createdTime: toString(e.createdTime), startDate: toString(e.startDate), endDate: toString(e.endDate)},\n" +
                            "       author {.*, postCount: count(DISTINCT authorPosts), isFollowed: exists( (u)-[:FOLLOWS]->(author) ), following: count(DISTINCT authorFollowing), followers: count(DISTINCT authorFollowers)},\n" +
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
    suspend fun stories(
        page: Int,
        pageSize: Int
    ): Result<List<Pair<StoryResponse, List<User>>>> {
        return withContext(Dispatchers.IO) {
            try {
                val params: MutableMap<String, Any> = mutableMapOf()
                params["userId"] = FirebaseAuth.getInstance().uid!!
                params["pageSize"] = pageSize
                params["skip"] = page * pageSize

                val records = query(
                    "MATCH (u:User {id: \$userId})-[:FOLLOWS*0..1]->(author:User)-[:POSTED]->(s:Story)\n" +
                            "OPTIONAL MATCH (author)-[authorPosts:POSTED]->(:Post)\n" +
                            "OPTIONAL MATCH (author)-[authorFollowing:FOLLOWS]->(:User)\n" +
                            "OPTIONAL MATCH (:User)-[authorFollowers:FOLLOWS]->(author)\n" +
                            "OPTIONAL MATCH (viewers:User)-[:SEEN]->(s) \n" +
                            "OPTIONAL MATCH (s)-[:WITH]->(w:User) \n" +
                            "RETURN s {.*, created: s.created.epochMillis}," +
                            "       author {.*, postCount: count(DISTINCT authorPosts), isFollowed: exists( (u)-[:FOLLOWS]->(author) ), following: count(DISTINCT authorFollowing), followers: count(DISTINCT authorFollowers)}, " +
                            "       collect(DISTINCT properties(w)), collect(DISTINCT viewers.id) " +
                            "ORDER BY s.created DESC " +
                            "SKIP \$skip LIMIT \$pageSize",
                    params
                )

                val stories = mutableListOf<Pair<StoryResponse, List<User>>>()

                records.forEach { record ->
                    val userListType: Type = object : TypeToken<ArrayList<User>>() {}.type
                    val stringArrayType: Type = object : TypeToken<ArrayList<String>>() {}.type

                    val gson = Gson()

                    val story =
                        gson.fromJson(record.values()[0].toString(), StoryResponse::class.java)

                    val author =
                        gson.fromJson(record.values()[1].toString(), User::class.java)

                    val withUsers = record.values()[2].toString()

                    val seenUsers = record.values()[3].toString()

                    val users =
                        gson.fromJson<ArrayList<User>>(withUsers, userListType)

                    val seenUsersIds =
                        gson.fromJson<ArrayList<String>>(seenUsers, stringArrayType)

                    users.add(author)

                    stories.add(Pair(story.copy(seenBy = seenUsersIds), users))
                }
                return@withContext Result.Success(stories.toList())
            } catch (e: Exception) {
                return@withContext Result.Error(e)
            }
        }
    }

    // page index zero
    suspend fun posts(
        page: Int,
        pageSize: Int
    ): Result<List<Pair<Post, List<User>>>> {
        return withContext(Dispatchers.IO) {
            try {
                val params: MutableMap<String, Any> = mutableMapOf()
                params["userId"] = FirebaseAuth.getInstance().uid!!
                params["pageSize"] = pageSize
                params["skip"] = page * pageSize

                val records = query(
                    "MATCH (u:User {id: \$userId})-[:FOLLOWS*0..1]->(author:User)-[authorPosts:POSTED]->(p:Post)\n" +
                            "OPTIONAL MATCH (author)-[authorFollowing:FOLLOWS]->(:User)\n" +
                            "OPTIONAL MATCH (:User)-[authorFollowers:FOLLOWS]->(author)\n" +
                            "OPTIONAL MATCH (:User)-[r:LIKED]->(p) \n" +
                            "OPTIONAL MATCH (p)-[:WITH]->(w:User) \n" +
                            "RETURN p {.*, likes: count(DISTINCT r), liked: exists((u)-[:LIKED]->(p)), createdTime: toString(p.createdTime)}," +
                            "       author {.*, postCount: count(DISTINCT authorPosts), isFollowed: exists( (u)-[:FOLLOWS]->(author) ), following: count(DISTINCT authorFollowing), followers: count(DISTINCT authorFollowers)}, " +
                            "       collect(DISTINCT properties(w)) " +
                            "ORDER BY datetime(p.createdTime) DESC " +
                            "SKIP \$skip LIMIT \$pageSize",
                    params
                )

                val posts = mutableListOf<Pair<Post, List<User>>>()

                records.forEach { record ->
                    val gson = Gson()

                    val post =
                        gson.fromJson(record.values()[0].toString(), Post::class.java)

                    val author =
                        gson.fromJson(record.values()[1].toString(), User::class.java)

                    val userListType: Type = object : TypeToken<ArrayList<User>>() {}.type

                    val s = record.values()[2].toString()

                    val users =
                        gson.fromJson<ArrayList<User>>(s, userListType)
                    users.add(author)

                    posts.add(Pair(post, users))
                }
                return@withContext Result.Success(posts.toList())
            } catch (e: Exception) {
                return@withContext Result.Error(e)
            }
        }
    }

    suspend fun seenStory(storyId: String) {
        return withContext(Dispatchers.IO) {
            val params: MutableMap<String, Any> = mutableMapOf()
            params["storyId"] = storyId
            params["userId"] = FirebaseAuth.getInstance().uid!!
            write(
                "MATCH (s:Story), (u:User) WHERE s.id = \$storyId AND u.id = \$userId MERGE (u)-[:SEEN]->(s) " +
                        "SET s.seenBy = s.seenBy + \$userId",
                params = params
            )
        }
    }

    private fun query(
        query: String,
        params: MutableMap<String, Any>
    ): List<Record> {
        getSession().use { session ->
            return session.readTransaction { tx ->
                tx.run(query, params).list()
            }
        }
    }

    private fun write(
        query: String,
        params: MutableMap<String, Any>
    ) {
        getSession().writeTransaction { tx ->
            tx.run(query, params)
        }
    }

    private fun getSession(): Session {
        return driver.session(SessionConfig.forDatabase(Environment.DEFAULT_DATABASE))
    }
}