package com.salazar.cheers.feature.edit_profile

import android.net.Uri
import com.salazar.cheers.core.model.Drink
import com.salazar.cheers.core.model.User

sealed interface EditProfileUiState {
    val isLoading: Boolean
    val errorMessages: List<String>
    val isFollowing: Boolean
    val done: Boolean
    val user: User
    val profilePictureUri: Uri?
    val bannerUri: List<Uri>
    val drinks: List<Drink>

    data class HasPosts(
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
        override val isFollowing: Boolean,
        override val user: User,
        override val done: Boolean,
        override val profilePictureUri: Uri?,
        override val bannerUri: List<Uri>,
        override val drinks: List<Drink>,
    ) : EditProfileUiState
}