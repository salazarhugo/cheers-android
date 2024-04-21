package com.salazar.cheers.core.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.salazar.cheers.core.model.Ticket

@Entity(tableName = "tickets")
data class TicketEntity(
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    val partyName: String = "",
    val description: String = "",
    val organization: String = "",
    val validated: Boolean = false,
    val price: Int = 0,
)

fun TicketEntity.asExternalModel() = Ticket(
    id = id,
    name = name,
    partyName = partyName,
    description = description,
    organization = organization,
    validated = validated,
    price = price,
)

fun Ticket.asEntity(): TicketEntity {
    return TicketEntity(
        id = id,
        name = name,
        partyName = partyName,
        description = description,
        organization = organization,
        validated = validated,
        price = price,
    )
}

fun List<TicketEntity>.asExternalModel() = this.map { it.asExternalModel() }

fun List<Ticket>.asEntity() = this.map { it.asEntity() }
