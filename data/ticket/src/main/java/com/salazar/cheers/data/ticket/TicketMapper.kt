package com.salazar.cheers.data.ticket

import cheers.ticket.v1.Ticket

fun Ticket.toTicket(): com.salazar.cheers.core.model.Ticket {
    return com.salazar.cheers.core.model.Ticket(
        id = id,
        name = name,
        description = description,
        partyName = partyName,
        price = price.toInt(),
        organization = organizer,
    )
}