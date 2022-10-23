package com.salazar.cheers.ui.main.story

import com.salazar.cheers.data.db.UserWithStories
import com.salazar.cheers.data.db.entities.Story
import com.salazar.cheers.internal.User

val photo = "https://static01.nyt.com/images/2021/01/30/business/29musk-print/merlin_133348470_4909550a-2f4a-4c38-80b1-969f8306dfba-superJumbo.jpg?quality=75&auto=webp"
val photo2 = "https://m.media-amazon.com/images/I/71dlTkiGsSL._AC_SY741_.jpg"
val photo3 = "https://scontent.fnic2-2.fna.fbcdn.net/v/t1.6435-9/62509773_1369570749850833_7625253820280340480_n.jpg?_nc_cat=105&ccb=1-7&_nc_sid=9267fe&_nc_ohc=6i40BoPgLX0AX_MWsM9&_nc_ht=scontent.fnic2-2.fna&oh=00_AT-K_CSOqZt3BBfK_tNFQzfcqFEPyMbTKxVi41V8Bj7kaw&oe=637AC958"

val fakeUsersWIthStories = listOf(
    UserWithStories(
        user = User(username = "dora"),
        stories = listOf(
            Story(username = "dora", photo = photo, viewed = true),
            Story("pedro", photo = photo3, viewed = true),
        )
    ),
    UserWithStories(
        user = User(username = "hugo"),
        stories = listOf(
            Story(username = "hugo", photo = photo2),
            Story("lisa", photo= photo)),
    ),
    UserWithStories(
        user = User(username = "anastasia"),
        stories = listOf(
            Story(username = "dora", photo = photo3),
            Story("pedro", photo = photo2),
        )
    ),
    UserWithStories(
        user = User(username = "isidora"),
        stories = listOf(
            Story(username = "dora", photo = photo3),
            Story("pedro", photo = photo2),
        )
    ),
    UserWithStories(
        user = User(username = "mcdonalds"),
        stories = listOf(
            Story(username = "dora", photo = photo3),
            Story("pedro", photo = photo2),
        )
    ),
)