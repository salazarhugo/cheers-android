package com.salazar.cheers.data.mapper

import cheers.ticket.v1.Ticket

fun Ticket.toTicket(): com.salazar.cheers.core.data.internal.Ticket {
    return com.salazar.cheers.core.data.internal.Ticket(
        id = id,
        name = name,
        description = description,
        partyName = partyName,
        price = price.toInt(),
        organization = organizer,
    )
}