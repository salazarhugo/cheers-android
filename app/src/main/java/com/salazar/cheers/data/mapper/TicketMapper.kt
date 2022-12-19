package com.salazar.cheers.data.mapper

import cheers.ticket.v1.Ticket

fun Ticket.toTicket(): com.salazar.cheers.internal.Ticket {
    return com.salazar.cheers.internal.Ticket(
        id = id,
        name = name,
        description = description,
        partyName = partyName,
    )
}