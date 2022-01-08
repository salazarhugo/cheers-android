package com.salazar.cheers.backend

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.salazar.cheers.data.Result
import com.salazar.cheers.internal.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.neo4j.driver.*
import java.lang.reflect.Type
import java.util.*
import java.util.UUID.randomUUID
import java.util.concurrent.TimeUnit


object Neo4jUtil {

//    companion object {
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
//    }

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

    fun unfollowUser(username: String) {
        val params: MutableMap<String, Any> = mutableMapOf()
        params["userId"] = FirebaseAuth.getInstance().uid!!
        params["username"] = username

        write(
            "MATCH (u:User)-[r:FOLLOWS]->(u2:User)" +
                    " WHERE u.id = \$userId AND u2.username = \$username" +
                    " DELETE r", params
        )
    }

    fun followUser(username: String) {
        val params: MutableMap<String, Any> = mutableMapOf()
        params["userId"] = FirebaseAuth.getInstance().uid!!
        params["username"] = username

        write(
            "MATCH (u:User), (u2:User)" +
                    " WHERE u.id = \$userId AND u2.username = \$username" +
                    " MERGE (u)-[:FOLLOWS]->(u2)", params
        )
    }

    suspend fun addUser(user: User) {
        return withContext(Dispatchers.IO) {
            val params: MutableMap<String, Any> = mutableMapOf()
            params["user"] = toMap(user)
            write("CREATE (u:User \$user)", params)
        }
    }

    fun updateProfilePicture(profilePictureUrl: String) {
        val params: MutableMap<String, Any> = mutableMapOf()
        params["profilePictureUrl"] = profilePictureUrl
        params["userId"] = FirebaseAuth.getInstance().uid!!

        write(
            "MATCH (u:User { id: \$userId }) SET u.profilePictureUrl = \$profilePictureUrl",
            params
        )
    }

    suspend fun updateUser(user: User) {
        return withContext(Dispatchers.IO) {
            val params: MutableMap<String, Any> = mutableMapOf()
            params["user"] = toMap(user)
            params["userId"] = FirebaseAuth.getInstance().uid!!

            write("MATCH (u:User { id: \$userId }) SET u = \$user", params)
        }
    }

    fun addPost(post: Post, tagUsers: List<String> = emptyList()) {
        val params: MutableMap<String, Any> = mutableMapOf()
        params["userId"] = FirebaseAuth.getInstance().uid!!
        val post2 = PostNeo4j(
            id = randomUUID().toString(),
            type = post.type,
            caption = post.caption,
            createdTime = post.createdTime,
            likes = post.likes,
            liked = post.liked,
            comments = post.comments,
            shares = post.shares,
            showOnMap = post.showOnMap,
            photoUrl = post.photoUrl,
            videoUrl = post.videoUrl,
            videoThumbnailUrl = post.videoThumbnailUrl,
            locationLatitude = post.locationLatitude,
            locationLongitude = post.locationLatitude,
            locationName = post.locationName,
        )
        params["post"] = toMap(post2)
        params["tagUsersId"] = tagUsers
        write(
            "MATCH (u:User) WHERE u.id = \$userId CREATE (p: Post \$post)" +
                    " SET p += { createdTime: datetime() } CREATE (u)-[:POSTED]->(p)" +
                    " WITH p UNWIND \$tagUsersId as tagUserId MATCH (u2:User {id: tagUserId}) CREATE (p)-[:WITH]->(u2)",
            params = params
        )
        sendPostNotification(post2.id)
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

    suspend fun deletePost(postId: String) {
        return withContext(Dispatchers.IO) {
            val params: MutableMap<String, Any> = mutableMapOf()
            params["postId"] = postId
            write(
                "MATCH (p:Post) WHERE p.id = \$postId DETACH DELETE p",
                params = params
            )
        }
    }

    suspend fun getCurrentUser(): Result<User> {
        return getUser(FirebaseAuth.getInstance().currentUser?.uid!!)
    }

    suspend fun getPostLikes(postId: String): Result<List<User>> {
        return withContext(Dispatchers.IO) {
            try {
                val params: MutableMap<String, Any> = mutableMapOf()
                params["postId"] = postId

                val records = query(
                    "MATCH (p:Post { id: \$postId})<-[:LIKED]-(u:User) " +
                            "RETURN properties(u)",
                    params)

                val users = mutableListOf<User>()

                records.forEach { record ->
                    val gson = Gson()
                    val user = gson.fromJson(record.values()[0].toString(), User::class.java)
                    users.add(user)
                }
                return@withContext Result.Success(users.toList())
            } catch (e: Exception) {
                return@withContext Result.Error(e)
            }
        }
    }

    suspend fun getSuggestions(): Result<List<SuggestionUser>> {
        return withContext(Dispatchers.IO) {
            try {
                val params: MutableMap<String, Any> = mutableMapOf()
                params["userId"] = FirebaseAuth.getInstance().uid!!

                val records = query(
                    "MATCH (u:User { id: \$userId})-[:FOLLOWS]->(f:User)-[:FOLLOWS]->(r:User) " +
                            "WHERE r.id <> \$userId " +
                            "AND NOT (u)-[:FOLLOWS]->(r) " +
                            "MATCH (r)-[:POSTED]->(p:Post) " +
                            "RETURN DISTINCT properties(r), collect(properties(p { photoUrl: p.photoUrl }))",
                    params
                )

                val users = mutableListOf<SuggestionUser>()

                records.forEach { record ->
                    val gson = Gson()
                    val parser = JsonParser()
                    val user = gson.fromJson(
                        parser.parse(record.values().get(0).toString()),
                        User::class.java
                    )
                    val postListType: Type =
                        object : TypeToken<ArrayList<Post>>() {}.type
                    val s = record.values()[1].toString()
                    val userPosts =
                        gson.fromJson<ArrayList<Post>>(s, postListType)

                    users.add(
                        SuggestionUser(
                            user = user,
                            posts = userPosts.toList(),
                        )
                    )
                }

                Log.d("FWA", users.toString())
                return@withContext Result.Success(users.toList())
            } catch (e: Exception) {
                Log.e("FWA", e.toString())
                return@withContext Result.Error(e)
            }
        }
    }

    suspend fun getUserRecommendations(): Result<List<User>> {
        return withContext(Dispatchers.IO) {
            try {
                val params: MutableMap<String, Any> = mutableMapOf()
                params["userId"] = FirebaseAuth.getInstance().uid!!

                val records = query(
                    "MATCH (u:User { id: \$userId})-[:FOLLOWS]->(f:User)-[:FOLLOWS]->(r:User) " +
                            "WHERE r.id <> \$userId " +
                            "AND NOT (u)-[:FOLLOWS]->(r) " +
                            "RETURN DISTINCT properties(r)",
                    params
                )

                val users = mutableListOf<User>()

                records.forEach { record ->
                    val gson = Gson()
                    val parser = JsonParser()
                    val user =
                        gson.fromJson(
                            parser.parse(record.values().get(0).toString()),
                            User::class.java
                        )
                    users.add(user)
                }

                Log.d("FWA", users.toString())
                return@withContext Result.Success(users.toList())
            } catch (e: Exception) {
                Log.e("FWA", e.toString())
                return@withContext Result.Error(e)
            }
        }
    }

    suspend fun getUserWithUsername(username: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val params: MutableMap<String, Any> = mutableMapOf()
                params["username"] = username
                params["currentUserId"] = FirebaseAuth.getInstance().currentUser?.uid!!

                val records = query(
                    "MATCH (u:User { username: \$username}) \n" +
                            "MATCH (me:User { id: \$currentUserId }) \n" +
                            "OPTIONAL MATCH (u)-[r:POSTED]->(:Post)\n" +
                            "OPTIONAL MATCH (u)-[f:FOLLOWS]->(:User)\n" +
                            "OPTIONAL MATCH (:User)-[f2:FOLLOWS]->(u)\n" +
                            "RETURN u {.*, posts: count(DISTINCT r), isFollowed: exists((me)-[:FOLLOWS]->(u)), following: count(DISTINCT f), followers: count(DISTINCT f2)}",
                    params
                )

                val gson = Gson()
                val parser = JsonParser()
                val user =
                    gson.fromJson(parser.parse(records[0].values()[0].toString()), User::class.java)
                Log.d("HAHA", user.toString())
                return@withContext Result.Success(user)
            } catch (e: Exception) {
                Log.e("HAHA", e.toString())
                return@withContext Result.Error(e)
            }
        }
    }

    suspend fun getUser(userId: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val params: MutableMap<String, Any> = mutableMapOf()
                params["userId"] = userId

                val records = query(
                    "MATCH (u:User) WHERE u.id = \$userId\n" +
                            "OPTIONAL MATCH (u)-[r:POSTED]->(:Post)\n" +
                            "OPTIONAL MATCH (u)-[f:FOLLOWS]->(:User)\n" +
                            "OPTIONAL MATCH (:User)-[f2:FOLLOWS]->(u)\n" +
                            "RETURN u {.*, posts: count(DISTINCT r), following: count(DISTINCT f), followers: count(DISTINCT f2)}",
                    params
                )

                val gson = Gson()
                val parser = JsonParser()
                val user =
                    gson.fromJson(parser.parse(records[0].values()[0].toString()), User::class.java)
                Log.d("HAHA", user.toString())
                return@withContext Result.Success(user)
            } catch (e: Exception) {
                Log.e("HAHA", e.toString())
                return@withContext Result.Error(e)
            }
        }
    }

    suspend fun queryFriends(query: String): List<User> {
        return withContext(Dispatchers.IO) {
            val params: MutableMap<String, Any> = mutableMapOf()
            params["query"] = query
            params["userId"] = FirebaseAuth.getInstance().uid!!

            val records = query(
                "MATCH (u:User)-[:FOLLOWS]->(u2:User) " +
                        "WHERE u.id = \$userId AND u2.username CONTAINS \$query " +
                        "RETURN properties(u2) LIMIT 20",
                params
            )

            val users = mutableListOf<User>()

            records.forEach { record ->
                val gson = Gson()
                val parser = JsonParser()
                val user =
                    gson.fromJson(parser.parse(record.values().get(0).toString()), User::class.java)
                users.add(user)
            }

            return@withContext users.toList()
        }
    }

    suspend fun queryUsers(query: String): List<User> {
        return withContext(Dispatchers.IO) {
            if (query.isBlank())
                return@withContext emptyList()

            val params: MutableMap<String, Any> = mutableMapOf()
            params["query"] = query
            params["userId"] = FirebaseAuth.getInstance().uid!!

            val records = query(
                "MATCH (u:User) " +
                        "WHERE u.username CONTAINS \$query " +
                        "AND u.id <> \$userId " +
                        "RETURN properties(u) LIMIT 10",
                params
            )

            val users = mutableListOf<User>()

            records.forEach { record ->
                val gson = Gson()
                val parser = JsonParser()
                val user =
                    gson.fromJson(parser.parse(record.values().get(0).toString()), User::class.java)
                users.add(user)
            }

            return@withContext users.toList()
        }
    }

    suspend fun unlikePost(postId: String) {
        return withContext(Dispatchers.IO) {
            val params: MutableMap<String, Any> = mutableMapOf()
            params["postId"] = postId
            params["userId"] = FirebaseAuth.getInstance().uid!!
            write(
                "MATCH (u:User)-[l:LIKED]->(p:Post) WHERE p.id = \$postId AND u.id = \$userId DELETE l",
                params = params
            )
        }
    }

    suspend fun likePost(postId: String) {
        return withContext(Dispatchers.IO) {
            val params: MutableMap<String, Any> = mutableMapOf()
            params["postId"] = postId
            params["userId"] = FirebaseAuth.getInstance().uid!!
            write(
                "MATCH (p:Post), (u:User) WHERE p.id = \$postId AND u.id = \$userId MERGE (u)-[:LIKED]->(p)",
                params = params
            )
        }
    }

    suspend fun isUsernameAvailable(username: String): Result<Boolean> {
        return withContext(Dispatchers.IO)
        {
            try {
                val params: MutableMap<String, Any> = mutableMapOf()
                params["username"] = username

                val records = query(
                    "MATCH (u:User) WHERE u.username = \$username RETURN u",
                    params
                )
                return@withContext Result.Success(records.isEmpty())
            } catch (e: Exception) {
                return@withContext Result.Error(e)
            }
        }
    }

    suspend fun getFollowing(username: String? = null): Result<List<User>> {
        return withContext(Dispatchers.IO) {
            try {
                val params: MutableMap<String, Any> = mutableMapOf()

                if (username != null)
                    params["username"] = username
                else
                    params["userId"] = FirebaseAuth.getInstance().uid!!

                val records = if (username != null)
                    query(
                        "MATCH (u:User)-[:FOLLOWS]->(u2:User) WHERE u.username = \$username RETURN properties(u2)",
                        params
                    )
                else
                    query(
                        "MATCH (u:User)-[:FOLLOWS]->(u2:User) WHERE u.id = \$userId RETURN properties(u2)",
                        params
                    )

                val following = mutableListOf<User>()

                records.forEach { record ->
                    Log.d("HAHA", record.values().size.toString())
                    val gson = Gson()
                    val parser = JsonParser()
                    val user =
                        gson.fromJson(parser.parse(record.values()[0].toString()), User::class.java)
                    following.add(user)
                }

                return@withContext Result.Success(following.toList())
            } catch (e: Exception) {
                return@withContext Result.Error(e)
            }
        }
    }

    suspend fun getFollowers(username: String? = null): Result<List<User>> {
        return withContext(Dispatchers.IO) {
            try {
                val params: MutableMap<String, Any> = mutableMapOf()

                if (username != null)
                    params["username"] = username
                else
                    params["userId"] = FirebaseAuth.getInstance().uid!!

                val records = if (username != null)
                    query(
                        "MATCH (follower:User)-[:FOLLOWS]->(u:User) WHERE u.username = \$username RETURN properties(follower)",
                        params
                    )
                else
                    query(
                        "MATCH (follower:User)-[:FOLLOWS]->(u:User) WHERE u.id = \$userId RETURN properties(follower)",
                        params
                    )

                val followers = mutableListOf<User>()

                records.forEach { record ->
                    Log.d("HAHA", record.values().size.toString())
                    val gson = Gson()
                    val parser = JsonParser()
                    val user =
                        gson.fromJson(parser.parse(record.values()[0].toString()), User::class.java)
                    followers.add(user)
                }

                return@withContext Result.Success(followers.toList())
            } catch (e: Exception) {
                return@withContext Result.Error(e)
            }
        }
    }

    suspend fun getUserPosts(username: String): Result<List<Post>> {
        return withContext(Dispatchers.IO) {
            try {
                val params: MutableMap<String, Any> = mutableMapOf()
                params["username"] = username

                val records = query(
                    "MATCH (u:User {username: \$username})-[:POSTED]->(p) \n" +
                            "OPTIONAL MATCH (:User)-[r:LIKED]->(p) \n" +
                            "RETURN p {.*, likes: count(DISTINCT r), liked: exists( (u)-[:LIKED]->(p) ), createdTime: apoc.temporal.format(p.createdTime, \"HH:mm\")}," +
                            " properties(u) ORDER BY p.createdTime DESC",
                    params
                )

                val posts = mutableListOf<Post>()

                records.forEach { record ->
                    val gson = Gson()
                    val parser = JsonParser()

                    val post =
                        gson.fromJson(parser.parse(record.values()[0].toString()), Post::class.java)
                    val user =
                        gson.fromJson(parser.parse(record.values()[1].toString()), User::class.java)

                    posts.add(
                        post.copy(
                            creator = user,
                        )
                    )
                }
                return@withContext Result.Success(posts.toList())
            } catch (e: Exception) {
                return@withContext Result.Error(e)
            }
        }
    }

    suspend fun getCurrentUserPosts(): Result<List<Post>> {
        return withContext(Dispatchers.IO) {
            try {
                val params: MutableMap<String, Any> = mutableMapOf()
                params["userId"] = FirebaseAuth.getInstance().uid!!

                val records = query(
                    "MATCH (u:User {id: \$userId})-[:POSTED]->(p) \n" +
                            "OPTIONAL MATCH (:User)-[r:LIKED]->(p) \n" +
                            "RETURN p {.*, likes: count(DISTINCT r), liked: exists( (u)-[:LIKED]->(p) ), createdTime: apoc.temporal.format(p.createdTime, \"HH:mm\")}," +
                            " properties(u) ORDER BY p.createdTime DESC",
                    params
                )

                val posts = mutableListOf<Post>()

                records.forEach { record ->
                    val gson = Gson()
                    val parser = JsonParser()

                    val post =
                        gson.fromJson(parser.parse(record.values()[0].toString()), Post::class.java)
                    val user =
                        gson.fromJson(parser.parse(record.values()[1].toString()), User::class.java)
//                    post.username = user.username
//                    post.verified = user.verified
//                    post.userPhotoUrl = user.profilePicturePath
//                    post.userId = user.id

                    posts.add(
                        post.copy(
                            creator = user,
                        )
                    )
                }
                return@withContext Result.Success(posts.toList())
            } catch (e: Exception) {
                return@withContext Result.Error(e)
            }
        }
    }

    suspend fun getMapPosts(): Result<List<Post>> {
        return withContext(Dispatchers.IO) {
            try {
                val params: MutableMap<String, Any> = mutableMapOf()
                params["userId"] = FirebaseAuth.getInstance().uid!!

                val records = query(
                    "MATCH (u:User {id: \$userId})-[:FOLLOWS*0..1]->(f)-[:POSTED]->(p) WHERE p.showOnMap = true \n" +
                            "OPTIONAL MATCH (:User)-[r:LIKED]-(p) \n" +
                            "RETURN p {.*, likes: count(DISTINCT r), liked: exists( (u)-[:LIKED]->(p) ), createdTime: apoc.temporal.format(p.createdTime, \"HH:mm\")}," +
                            " properties(f) ORDER BY p.createdTime DESC",
                    params
                )

                val posts = mutableListOf<Post>()

                records.forEach { record ->
                    val gson = Gson()
                    val parser = JsonParser()

                    val post =
                        gson.fromJson(parser.parse(record.values()[0].toString()), Post::class.java)
                    val user =
                        gson.fromJson(parser.parse(record.values()[1].toString()), User::class.java)
                    posts.add(
                        post.copy(
                            creator = user
                        )
                    )
                }
                return@withContext Result.Success(posts.toList())
            } catch (e: Exception) {
                return@withContext Result.Error(e)
            }
        }
    }

    suspend fun posts(): Result<List<Post>> {
        return withContext(Dispatchers.IO)
        {
            try {
                val params: MutableMap<String, Any> = mutableMapOf()
                params["userId"] = FirebaseAuth.getInstance().uid!!

                val records = query(
                    "MATCH (u:User {id: \$userId})-[:FOLLOWS*0..1]->(f)-[:POSTED]->(p)\n" +
                            "OPTIONAL MATCH (:User)-[r:LIKED]-(p) \n" +
                            "OPTIONAL MATCH (p)-[:WITH]-(w:User) \n" +
                            "RETURN p {.*, likes: count(DISTINCT r), liked: exists((u)-[:LIKED]->(p)), createdTime: toString(p.createdTime)}," +
                            " properties(f), collect(properties(w)) ORDER BY datetime(p.createdTime) DESC",
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

    private fun convert(value: Value): Any? {
        when (value.type().name()) {
            "PATH" -> return value.asList(Neo4jUtil::convert)
            "NODE", "RELATIONSHIP" -> return value.asMap()
        }
        return value.asObject()
    }

    private fun read(query: String, params: Value) {
        getSession().writeTransaction { tx ->
            tx.run(query, params)
        }
    }

    private fun write(query: String, params: MutableMap<String, Any>) {
        getSession().writeTransaction { tx ->
            tx.run(query, params)
        }
    }

}