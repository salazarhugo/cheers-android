package com.salazar.cheers.feature.edit_profile

import android.net.Uri
import com.salazar.cheers.core.model.Drink
import com.salazar.cheers.core.model.User

internal data class EditProfileViewModelState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val isFollowing: Boolean = false,
    val done: Boolean = false,
    val profilePictureUri: Uri? = null,
    val bannerUri: Uri? = null,
    val drinks: List<Drink>? = null,
) {
    fun toUiState(): EditProfileUiState =
        EditProfileUiState.HasPosts(
            user = user ?: User(),
            isLoading = isLoading,
            errorMessages = errorMessages,
            isFollowing = isFollowing,
            done = done,
            profilePictureUri = profilePictureUri,
            bannerUri = bannerUri,
            drinks = drinks.orEmpty(),
        )
}