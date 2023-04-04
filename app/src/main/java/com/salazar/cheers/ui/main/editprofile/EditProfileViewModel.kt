package com.salazar.cheers.ui.main.editprofile

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.core.data.internal.User
import com.salazar.cheers.workers.UploadProfilePicture
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface EditProfileUiState {

    val isLoading: Boolean
    val errorMessages: List<String>
    val isFollowing: Boolean
    val done: Boolean
    val user: User
    val profilePictureUri: Uri?

    data class HasPosts(
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
        override val isFollowing: Boolean,
        override val user: User,
        override val done: Boolean,
        override val profilePictureUri: Uri?,
    ) : EditProfileUiState
}

private data class EditProfileViewModelState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val isFollowing: Boolean = false,
    val done: Boolean = false,
    val profilePictureUri: Uri? = null,
) {
    fun toUiState(): EditProfileUiState =
        EditProfileUiState.HasPosts(
            user = user ?: User(),
            isLoading = isLoading,
            errorMessages = errorMessages,
            isFollowing = isFollowing,
            done = done,
            profilePictureUri = profilePictureUri,
        )
}

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    val userRepository: UserRepository,
    application: Application,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(EditProfileViewModelState(isLoading = true))
    private val workManager = WorkManager.getInstance(application)

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


    fun onSelectPicture(pictureUri: Uri) {
        viewModelState.update {
            it.copy(profilePictureUri = pictureUri)
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

    fun updateUser() {
        viewModelState.update { it.copy(isLoading = true) }

        val user = viewModelState.value.user ?: return

        viewModelScope.launch {
            userRepository.updateUser(user = user)
            viewModelState.update {
                it.copy(done = true, isLoading = false)
            }
        }
        uploadProfilePicture()
    }


    private fun uploadProfilePicture() {
        val profilePicture = viewModelState.value.profilePictureUri ?: return

        val uploadWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<UploadProfilePicture>().apply {
                setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                setInputData(
                    workDataOf(
                        "PHOTO_URI" to profilePicture.toString(),
                    )
                )
            }
                .build()
        workManager.enqueue(uploadWorkRequest)
    }
}