package com.salazar.cheers.feature.edit_profile

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.user.User
import com.salazar.cheers.data.user.UserRepository
import com.salazar.cheers.domain.update_profile.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface EditProfileUiState {

    val isLoading: Boolean
    val errorMessages: List<String>
    val isFollowing: Boolean
    val done: Boolean
    val user: User
    val profilePictureUri: Uri?
    val bannerUri: Uri?

    data class HasPosts(
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
        override val isFollowing: Boolean,
        override val user: User,
        override val done: Boolean,
        override val profilePictureUri: Uri?,
        override val bannerUri: Uri?,
    ) : EditProfileUiState
}

private data class EditProfileViewModelState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val isFollowing: Boolean = false,
    val done: Boolean = false,
    val profilePictureUri: Uri? = null,
    val bannerUri: Uri? = null,
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
        )
}

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    val userRepository: UserRepository,
    val updateProfileUseCase: UpdateProfileUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(EditProfileViewModelState(isLoading = true))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        viewModelScope.launch {
            userRepository.getCurrentUserFlow().collect { user ->
                viewModelState.update {
                    it.copy(user = user, isLoading = false)
                }
            }
        }
    }

    val currentUser = mutableStateOf(User())


    fun onSelectPicture(pictureUri: Uri?) {
        if (pictureUri == null)
            return
        viewModelState.update {
            it.copy(profilePictureUri = pictureUri)
        }
    }

    fun onSelectBanner(bannerUri: Uri?) {
        if (bannerUri == null)
            return
        viewModelState.update {
            it.copy(bannerUri = bannerUri)
        }
    }

    fun onNameChanged(name: String) {
        viewModelState.update {
            val newUser = it.user?.copy(name = name)
            it.copy(user = newUser)
        }
    }

    fun onWebsiteChanged(website: String) {
        viewModelState.update {
            val newUser = it.user?.copy(website = website)
            it.copy(user = newUser)
        }
    }

    fun onUsernameChange(username: String) {
        viewModelState.update {
            val newUser = it.user?.copy(username = username)
            it.copy(user = newUser)
        }
    }

    fun onBioChanged(bio: String) {
        viewModelState.update {
            val newUser = it.user?.copy(bio = bio)
            it.copy(user = newUser)
        }
    }

    fun onSave() {
        viewModelState.update { it.copy(isLoading = true) }

        val user = viewModelState.value.user ?: return

        viewModelScope.launch {
            updateProfileUseCase(
                picture = user.picture,
                name = user.name,
                banner = user.banner,
                website = user.website,
                bio = user.bio,
            )
            viewModelState.update {
                it.copy(done = true, isLoading = false)
            }
        }
        uploadProfilePicture()
        uploadBanner()
    }

    private fun uploadBanner() {
        val banner = viewModelState.value.bannerUri ?: return

        viewModelScope.launch {
            userRepository.uploadProfileBanner(banner)
        }
    }

    private fun uploadProfilePicture() {
        val profilePicture = viewModelState.value.profilePictureUri ?: return

        viewModelScope.launch {
            userRepository.uploadProfilePicture(profilePicture)
        }
    }
}