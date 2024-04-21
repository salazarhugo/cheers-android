package com.salazar.cheers.core.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.salazar.cheers.core.model.UserSuggestion


@Entity(tableName = "user_suggestion")
data class UserSuggestionEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val username: String,
    val verified: Boolean,
    val picture: String?,
    val followBack: Boolean,
    @ColumnInfo(defaultValue = "")
    val accountId: String,
)
fun UserSuggestionEntity.asExternalModel() = UserSuggestion(
    id = id,
    name = name,
    username = username,
    verified = verified,
    picture = picture,
    followBack = followBack,
    accountId = accountId,
)

fun UserSuggestion.asEntity(): UserSuggestionEntity =
    UserSuggestionEntity(
        id = id,
        name = name,
        username = username,
        verified = verified,
        picture = picture,
        followBack = followBack,
        accountId = accountId,
    )

fun List<UserSuggestionEntity>.asExternalModel() = this.map { it.asExternalModel() }

fun List<UserSuggestion>.asEntity() = this.map { it.asEntity() }
