package com.salazar.cheers.core.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tickets")
data class Ticket(
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    val partyName: String = "",
    val description: String = "",
    val organization: String = "",
    val validated: Boolean = false,
    val price: Int = 0,
)