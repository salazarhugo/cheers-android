package com.salazar.cheers.feature.edit_profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.model.Drink
import com.salazar.cheers.core.model.Gender
import com.salazar.cheers.data.user.UserRepositoryImpl
import com.salazar.cheers.domain.list_drink.ListDrinkUseCase
import com.salazar.cheers.domain.update_profile.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepositoryImpl: UserRepositoryImpl,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val listDrinkUseCase: ListDrinkUseCase,
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
            userRepositoryImpl.getCurrentUserFlow().collect { user ->
                viewModelState.update {
                    it.copy(user = user, isLoading = false)
                }
            }
        }

        viewModelScope.launch {
            listDrinkUseCase().collect { result ->
                result.onSuccess(::updateDrinks)
                    .onFailure {
                    }
            }
        }
    }


    fun onSelectPicture(pictureUri: Uri?) {
        if (pictureUri == null)
            return
        viewModelState.update {
            it.copy(profilePictureUri = pictureUri)
        }
    }

    private fun updateDrinks(drinks: List<Drink>) {
        val emptyDrink = listOf(
            Drink(
                id = String(),
                name = "",
                icon = "",
                category = "",
            )
        )
        viewModelState.update {
            it.copy(drinks = emptyDrink + drinks)
        }
    }

    fun selectDrink(drink: Drink) {
        viewModelState.update {
            val newUser = it.user?.copy(favouriteDrink = drink)
            it.copy(user = newUser)
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
                favouriteDrinkId = user.favouriteDrink?.id,
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
            userRepositoryImpl.uploadProfileBanner(banner)
        }
    }

    private fun uploadProfilePicture() {
        val profilePicture = viewModelState.value.profilePictureUri ?: return

        viewModelScope.launch {
            userRepositoryImpl.uploadProfilePicture(profilePicture)
        }
    }

    fun updateGender(gender: Gender) {
        viewModelState.update {
            it.copy(user = it.user?.copy(gender = gender))
        }
    }
}