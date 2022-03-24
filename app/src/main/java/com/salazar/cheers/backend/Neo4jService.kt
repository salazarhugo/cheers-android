package com.salazar.cheers.backend

import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.salazar.cheers.data.Result
import com.salazar.cheers.data.entities.StoryResponse
import com.salazar.cheers.data.entities.UserStats
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
                            "OPTIONAL MATCH (u)-[authorPosts:POSTED]->(:Post)\n" +
                            "OPTIONAL MATCH (u)-[following:FOLLOWS]->(:User)\n" +
                            "OPTIONAL MATCH (:User)-[followers:FOLLOWS]->(u)\n" +
                            "WITH u, count(DISTINCT authorPosts) as postCount, count(DISTINCT following) as following,\n" +
                            "count(DISTINCT followers) as followers, exists((:User{id:\$currentUserId})-[:FOLLOWS]->(u)) as isFollowed\n" +
                            "RETURN u {.*, postCount: postCount, isFollowed: isFollowed, following: following, followers: followers}",
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
                            "RETURN DISTINCT e { .*, startDate: toString(e.startDate), endDate: toString(e.endDate)},\n" +
                            "       author {.*, postCount: count(DISTINCT authorPosts), isFollowed: exists( (u)-[:FOLLOWS]->(author) ), following: count(DISTINCT authorFollowing), followers: count(DISTINCT authorFollowers)},\n" +
                            "ORDER BY e.created DESC " +
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
                            "WITH u, author, s SKIP \$skip LIMIT \$pageSize\n" +
                            "OPTIONAL MATCH (author)-[authorPosts:POSTED]->(:Post)\n" +
                            "OPTIONAL MATCH (author)-[authorFollowing:FOLLOWS]->(:User)\n" +
                            "OPTIONAL MATCH (:User)-[authorFollowers:FOLLOWS]->(author)\n" +
                            "OPTIONAL MATCH (viewers:User)-[:SEEN]->(s) \n" +
                            "OPTIONAL MATCH (s)-[:WITH]->(w:User) \n" +
                            "WITH s, author, count(DISTINCT authorPosts) as postCount, count(DISTINCT authorFollowing) as following,\n" +
                            "count(DISTINCT authorFollowers) as followers, collect(DISTINCT properties(w)) as tags, exists((u)-[:FOLLOWS]->(author)) as isFollowed,\n" +
                            "collect(DISTINCT viewers.id) as viewers\n" +
                            "RETURN properties(s),\n" +
                            "       author {.*, postCount: postCount, isFollowed: isFollowed, following: following, followers: followers}, " +
                            "       tags, viewers\n" +
                            "ORDER BY s.created DESC",
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
                    "MATCH (u:User {id: \$userId})-[:FOLLOWS*0..1]->(author:User)-[:POSTED]->(p:Post)\n" +
                            "WITH u, author, p SKIP \$skip LIMIT \$pageSize\n" +
                            "OPTIONAL MATCH (author)-[authorPosts:POSTED]->(:Post)\n" +
                            "OPTIONAL MATCH (author)-[authorFollowing:FOLLOWS]->(:User)\n" +
                            "OPTIONAL MATCH (:User)-[authorFollowers:FOLLOWS]->(author)\n" +
                            "OPTIONAL MATCH (:User)-[r:LIKED]->(p) \n" +
                            "OPTIONAL MATCH (p)-[:WITH]->(w:User) \n" +
                            "WITH p, author, count(DISTINCT authorPosts) as postCount, exists((u)-[:LIKED]->(p)) as liked, count(DISTINCT authorFollowing) as following,\n" +
                            "count(DISTINCT authorFollowers) as followers, count(DISTINCT r) as likes, collect(DISTINCT properties(w)) as tags, exists((u)-[:FOLLOWS]->(author)) as isFollowed\n" +
                            "RETURN p {.*, likes: likes, liked: liked}," +
                            "       author {.*, postCount: postCount, isFollowed: isFollowed, following: following, followers: followers}, " +
                            "       tags\n" +
                            "ORDER BY p.created DESC",
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

    suspend fun getSuggestions(): Result<List<User>> {
        return withContext(Dispatchers.IO) {
            try {
                val params: MutableMap<String, Any> = mutableMapOf()
                params["userId"] = FirebaseAuth.getInstance().uid!!
                params["pageSize"] = 10
                params["skip"] = 0 * 10

                val records = query(
                    "MATCH (u:User { id: \$userId})-[:FOLLOWS]->(f:User)-[:FOLLOWS]->(suggestion:User) " +
                            "WHERE suggestion.id <> \$userId " +
                            "AND NOT (u)-[:FOLLOWS]->(suggestion) " +
                            "WITH suggestion SKIP \$skip LIMIT \$pageSize\n" +
                            "OPTIONAL MATCH (suggestion)-[posts:POSTED]->(:Post)\n" +
                            "OPTIONAL MATCH (suggestion)-[following:FOLLOWS]->(:User)\n" +
                            "OPTIONAL MATCH (:User)-[followers:FOLLOWS]->(suggestion)\n" +
                            "WITH suggestion, count(DISTINCT posts) as posts, count(DISTINCT followers) as followers, count(DISTINCT following) as following\n" +
                            "RETURN suggestion {.*,  postCount: posts, followers: followers, following: following, isFollowed: false }",
                    params
                )

                val users = mutableListOf<User>()

                records.forEach { record ->
                    val gson = Gson()
                    val user =
                        gson.fromJson(record.values()[0].toString(), User::class.java)
                    users.add(user)
                }

                return@withContext Result.Success(users.toList())
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

    suspend fun getUserStats(username: String): UserStats {
        return withContext(Dispatchers.IO) {

            val params: MutableMap<String, Any> = mutableMapOf()
            params["username"] = username
            params["userId"] = FirebaseAuth.getInstance().currentUser?.uid!!

            val records = query(
                "MATCH (u:User { username: \$username})-[:POSTED]-(p:Post)\n" +
                        "WITH u, p.beverage as favDrink, count(p.beverage) as occ ORDER BY occ DESC LIMIT 1\n" +
                        "MATCH (u)-[:POSTED]->(p:Post)\n" +
                        "WITH u, favDrink, count(p.beverage) as drinks, avg(p.drunkenness) as avgDrunkenness, max(p.drunkenness) as maxDrunkenness\n" +
                        "RETURN { id: u.id, username: u.username, favoriteDrink: favDrink, drinks: drinks," +
                        " avgDrunkenness: avgDrunkenness, maxDrunkenness: maxDrunkenness }",
                params
            )

            val userStats =
                Gson().fromJson(records[0].values()[0].toString(), UserStats::class.java)

            return@withContext userStats
        }
    }

    suspend fun queryUsers(query: String): List<User> {
        return withContext(Dispatchers.IO) {
            if (query.isBlank())
                return@withContext emptyList()

            val params: MutableMap<String, Any> = mutableMapOf()
            params["query"] = query
            params["userId"] = FirebaseAuth.getInstance().currentUser?.uid!!

            val records = query(
                "MATCH (u:User)\n" +
                        "WHERE u.username CONTAINS \$query\n" +
                        "AND u.id <> \$userId\n" +
                        "WITH u LIMIT 10\n" +
                        "OPTIONAL MATCH (u)-[posts:POSTED]->(:Post)\n" +
                        "OPTIONAL MATCH (u)-[following:FOLLOWS]->(:User)\n" +
                        "OPTIONAL MATCH (:User)-[followers:FOLLOWS]->(u)\n" +
                        "WITH u, count(DISTINCT posts) as posts, count(DISTINCT following) as following, count(DISTINCT followers) as followers, exists((:User {id: \$userId})-[:FOLLOWS]->(u)) as isFollowed\n" +
                        "RETURN u {.*, postCount: posts, isFollowed: isFollowed, following: following, followers: followers}",
                params
            )

            val users = mutableListOf<User>()

            records.forEach { record ->
                val gson = Gson()
                val user =
                    gson.fromJson(record.values()[0].toString(), User::class.java)
                users.add(user)
            }

            return@withContext users.toList()
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