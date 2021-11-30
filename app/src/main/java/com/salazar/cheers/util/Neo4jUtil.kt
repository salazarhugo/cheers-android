package com.salazar.cheers.util

import android.graphics.PorterDuff
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.internal.Environment
import com.salazar.cheers.internal.Post
import org.neo4j.driver.*
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit
import com.google.gson.JsonObject

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.salazar.cheers.internal.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.neo4j.driver.Values.parameters


object Neo4jUtil {

    private val driver: Driver by lazy {
         GraphDatabase.driver(
            Environment.DEFAULT_URL,
            AuthTokens.basic(Environment.DEFAULT_USER, Environment.DEFAULT_PASS),
            Config.builder()
                .withMaxConnectionLifetime(8, TimeUnit.MINUTES)
                .withConnectionLivenessCheckTimeout(2, TimeUnit.MINUTES).build()
        )
    }
    private const val database: String = Environment.DEFAULT_DATABASE

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

    fun followUser(otherUserId: String) {
        val params: MutableMap<String, Any> = mutableMapOf()
        params["userId"] = FirebaseAuth.getInstance().uid!!
        params["otherUserId"] = otherUserId

        write("MATCH (u:User), (u2:User)" +
                    " WHERE u.id = \$userId AND u2.id = \$otherUserId" +
                    " MERGE (u)-[:FOLLOW]->(u2)", params)
    }

    suspend fun addUser(user: User) {
        return withContext(Dispatchers.IO) {
            val params: MutableMap<String, Any> = mutableMapOf()
            params["user"] = toMap(user)
            write("CREATE (u:User \$user)", params)
        }
    }

    suspend fun addPost(post: Post) {
        return withContext(Dispatchers.IO) {
            val params: MutableMap<String, Any> = mutableMapOf()
            params["userId"] = FirebaseAuth.getInstance().uid!!
            params["post"] = toMap(post)
            write(
                "MATCH (u:User) WHERE u.id = \$userId CREATE (p: Post \$post) SET p += {id: randomUUID(), createdTime: datetime() }CREATE (u)-[:POSTED]->(p)",
                params = params
            )
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

    fun getCurrentUser(): User
    {
        return getUser(FirebaseAuth.getInstance().currentUser?.uid!!)
    }

    fun getUser(userId: String): User {

        val params: MutableMap<String, Any> = mutableMapOf()
        params["userId"] = userId

        val records = query(
            "MATCH (u:User) WHERE u.id = \$userId\n" +
                    "OPTIONAL MATCH (u)-[r:POSTED]-(:Post)\n" +
                    "OPTIONAL MATCH (u)-[f:FOLLOW]-(:User)\n" +
                    "OPTIONAL MATCH (:User)-[f2:FOLLOW]-(u)\n" +
                    "RETURN u {.*, posts: count(r), following: count(f), followers: count(f2)}",
            params
        )

        val gson = Gson()
        val parser = JsonParser()
        val user = gson.fromJson(parser.parse(records[0].values()[0].toString()), User::class.java)

        return user
    }


    suspend fun queryUsers(query: String): List<User> {
        return withContext(Dispatchers.IO) {
            val params: MutableMap<String, Any> = mutableMapOf()
            params["query"] = query

            val records = query(
                "MATCH (u:User) WHERE u.username CONTAINS \$query RETURN properties(u)",
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

    suspend fun posts(): List<Post> {
        return withContext(Dispatchers.IO)
        {
            val params: MutableMap<String, Any> = mutableMapOf()
            params["userId"] = FirebaseAuth.getInstance().uid!!

            val records = query(
                "MATCH (u:User)-[:POSTED]-(p) WHERE u.id = \$userId " +
                        "OPTIONAL MATCH (:User)-[r:LIKED]-(p) RETURN p {.*, likes: count(r)," +
                        " createdTime: apoc.temporal.format(p.createdTime, \"HH:mm\")}, properties(u)",
                params
            )

            val posts = mutableListOf<Post>()

            records.forEach { record ->
                val gson = Gson()
                val parser = JsonParser()

                val post = gson.fromJson(parser.parse(record.values()[0].toString()), Post::class.java)
                val user = gson.fromJson(parser.parse(record.values()[1].toString()), User::class.java)
                post.username = user.username
                post.photoUrl = user.photoUrl
                post.userId = user.id

                posts.add(post)
            }
            return@withContext posts.toList()
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

    private fun read(query: String, params: Value)
    {
        getSession().writeTransaction { tx->
            tx.run(query, params)
        }
    }

    private fun write(query: String, params: MutableMap<String, Any>)
    {
        getSession().writeTransaction { tx->
            tx.run(query, params)
        }
    }

}