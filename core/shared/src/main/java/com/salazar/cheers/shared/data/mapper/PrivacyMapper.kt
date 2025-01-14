package com.salazar.cheers.shared.data.mapper

import cheers.type.PrivacyOuterClass
import com.salazar.cheers.core.model.Privacy

fun Privacy.toPrivacyPb(): PrivacyOuterClass.Privacy {
    return when (this) {
        Privacy.PRIVATE -> PrivacyOuterClass.Privacy.PRIVATE
        Privacy.PUBLIC -> PrivacyOuterClass.Privacy.PUBLIC
        Privacy.FRIENDS -> PrivacyOuterClass.Privacy.FRIENDS
    }
}

fun PrivacyOuterClass.Privacy.toPrivacy(): Privacy {
    return when (this) {
        PrivacyOuterClass.Privacy.FRIENDS -> Privacy.FRIENDS
        PrivacyOuterClass.Privacy.PRIVATE -> Privacy.PRIVATE
        PrivacyOuterClass.Privacy.PUBLIC -> Privacy.PUBLIC
        PrivacyOuterClass.Privacy.GROUP -> Privacy.PRIVATE
        PrivacyOuterClass.Privacy.UNRECOGNIZED -> Privacy.PUBLIC
    }
}
