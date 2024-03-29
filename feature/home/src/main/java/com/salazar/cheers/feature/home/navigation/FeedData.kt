package com.salazar.cheers.feature.home.navigation

import com.salazar.cheers.data.post.repository.Post


sealed class FeedData {
    data class PostItem(val post: Post) : FeedData()
    data class AdItem(val id: String) : FeedData()
}
