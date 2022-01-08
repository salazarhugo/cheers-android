package com.salazar.cheers.ui.profile

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.salazar.cheers.data.Result
import com.salazar.cheers.internal.User
import com.salazar.cheers.backend.Neo4jUtil
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

    data class HasPosts(
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
        override val isFollowing: Boolean,
        override val user: User,
        override val done: Boolean,
    ) : EditProfileUiState
}

private data class EditProfileViewModelState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val isFollowing: Boolean = false,
    val done: Boolean = false,
) {
    fun toUiState(): EditProfileUiState =
        EditProfileUiState.HasPosts(
            user = user ?: User(),
            isLoading = isLoading,
            errorMessages = errorMessages,
            isFollowing = isFollowing,
            done = done,
        )
}

@HiltViewModel
class EditProfileViewModel @Inject constructor(application: Application) : ViewModel() {

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
        refreshUser()
    }

    val currentUser = mutableStateOf(User())
    val photoUri = mutableStateOf<Uri?>(null)

    private fun refreshUser() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            viewModelState.update {
                when (val result = Neo4jUtil.getCurrentUser()) {
                    is Result.Success -> it.copy(user = result.data, isLoading = false)
                    is Result.Error -> it.copy(
                        errorMessages = listOf(result.exception.toString()),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onNameChanged(name: String) {
        viewModelState.update {
            val newUser = it.user?.copy(fullName = name)
            it.copy(user = newUser)
        }
    }

    fun onWebsiteChanged(website: String) {
        viewModelState.update {
            val newUser = it.user?.copy(website = website)
            it.copy(user = newUser)
        }
    }

    fun onBioChanged(bio: String) {
        viewModelState.update {
            val newUser = it.user?.copy(bio = bio)
            it.copy(user = newUser)
        }
    }

    fun updateUser(user: User) {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            Neo4jUtil.updateUser(user)
            viewModelState.update {
                it.copy(done = true)
            }
        }
        uploadProfilePicture()
    }


    private fun uploadProfilePicture() {
        if (photoUri.value == null)
            return

        val uploadWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<UploadProfilePicture>().apply {
                setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                setInputData(
                    workDataOf(
                        "PHOTO_URI" to photoUri.value.toString(),
                    )
                )
            }
                .build()
        workManager.enqueue(uploadWorkRequest)
    }

    fun onNameChanged() {
        viewModelState.update {
            it.copy()
        }
    }

}