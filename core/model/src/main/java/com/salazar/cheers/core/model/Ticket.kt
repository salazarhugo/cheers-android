package com.salazar.cheers.core.model

import androidx.compose.runtime.Immutable
import java.util.UUID

@Immutable
data class Ticket(
    val id: String = "",
    val name: String = "",
    val partyName: String = "",
    val description: String = "",
    val organization: String = "",
    val validated: Boolean = false,
    val price: Int = 0,
)

val duplexTicket = Ticket(
    id = UUID.randomUUID().toString(),
    name = "Duplex VIP",
    partyName = "Erasmus party",
)
