package com.salazar.cheers.core.util

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.dynamiclinks.ktx.socialMetaTagParameters
import com.google.firebase.ktx.Firebase
import com.salazar.cheers.core.util.Constants.URI
import com.salazar.common.util.result.DataError
import com.salazar.common.util.result.Result
import kotlinx.coroutines.tasks.await


object FirebaseDynamicLinksUtil {
    suspend fun createShortLink(url: String): Result<String, DataError> {
        return try {
            val shortLink = Firebase.dynamicLinks.shortLinkAsync(ShortDynamicLink.Suffix.SHORT) {
                link = Uri.parse("$URI/$url")
                domainUriPrefix = "https://cheers2cheers.page.link"
                androidParameters { }
                socialMetaTagParameters {
                    title = "Follow your friend on Cheers!"
                    description = "This link works whether the app is installed or not!"
                }
            }.await()

            Result.Success(shortLink.shortLink.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Network.UNKNOWN)
        }
    }
}