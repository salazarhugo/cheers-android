package com.salazar.cheers.data.mapper

import com.salazar.cheers.internal.Activity


fun cheers.activity.v1.Activity.toActivity(accountId: String): Activity {
 return Activity().copy(
     id = id,
     accountId = accountId,
     username = text,
     avatar = picture,
     photoUrl = mediaPicture,
     userId = userId,
     createTime = timestamp,
    )
}