package com.salazar.cheers.data.note

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey
    val userId: String,
    val text: String = "",
    val picture: String = "",
    val username: String = "",
    val name: String = "",
    val createTime: Long = 0,
)