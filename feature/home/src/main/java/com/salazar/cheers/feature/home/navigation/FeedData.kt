package com.salazar.cheers.feature.home.navigation

import com.salazar.cheers.core.Post


sealed class FeedData {
    data class PostItem(val post: Post) : FeedData()
    data class AdItem(val id: String) : FeedData()
}
