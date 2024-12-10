package com.salazar.cheers.core.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.salazar.cheers.core.model.RecentSearch

@Entity(tableName = "recent_searches")
data class RecentSearchEntity(
    @PrimaryKey() val id: String,
    val searchType: String, // Store the type of search (Text, Party, User)
    val searchData: String, // Store the serialized search data
    val date: Long = System.currentTimeMillis(),
)

fun RecentSearchEntity.asExternalModel(): RecentSearch {
    return deserializeSearch(searchType, searchData)!!
}

fun RecentSearch.asEntity(): RecentSearchEntity {
    return RecentSearchEntity(
        id = when (this) {
            is RecentSearch.Party -> party.id
            is RecentSearch.Text -> text
            is RecentSearch.User -> user.id
        },
        searchData = serializeSearch(this),
        searchType = when (this) {
            is RecentSearch.Party -> "party"
            is RecentSearch.Text -> "text"
            is RecentSearch.User -> "user"
        }
    )

}

fun serializeSearch(search: RecentSearch): String {
    val gson = Gson()
    return gson.toJson(search)
}

fun deserializeSearch(type: String, data: String): RecentSearch? {
    val a = when (type) {
        "party" -> RecentSearch.Party::class.java
        "text" -> RecentSearch.Text::class.java
        "user" -> RecentSearch.User::class.java
        else -> return null
    }

    val gson = Gson()
    return try {
        gson.fromJson(data, a)
    } catch (e: JsonSyntaxException) {
        null
    }
}
