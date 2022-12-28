package com.salazar.cheers.util

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.dynamiclinks.ktx.socialMetaTagParameters
import com.google.firebase.ktx.Firebase
import com.salazar.cheers.util.Constants.URI


object FirebaseDynamicLinksUtil {
    fun createShortLink(url: String): Task<ShortDynamicLink> {
        return Firebase.dynamicLinks.shortLinkAsync(ShortDynamicLink.Suffix.SHORT) {
            link = Uri.parse("$URI/$url")
            domainUriPrefix = "https://cheers2cheers.page.link"
            androidParameters { }
            socialMetaTagParameters {
                title = "Follow your friend on Cheers!"
                description = "This link works whether the app is installed or not!"
            }
        }
    }
}