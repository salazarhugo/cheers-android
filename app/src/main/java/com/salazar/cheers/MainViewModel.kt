package com.salazar.cheers

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.data.UserRepository
import com.salazar.cheers.data.db.UserPreferenceDao
import com.salazar.cheers.data.entities.Theme
import com.salazar.cheers.data.entities.UserPreference
import com.salazar.cheers.internal.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userPreferenceDao: UserPreferenceDao,
) : ViewModel() {

    val user: MutableState<User?> = mutableStateOf(null)

    val uid = FirebaseAuth.getInstance().currentUser?.uid

    val userPreference = UserPreference(id = "fwf", theme = Theme.SYSTEM) //userPreferenceDao.getUserPreference(FirebaseAuth.getInstance().currentUser?.uid!!)

    init {
        refreshUser()
    }

    fun refreshUser() {
        viewModelScope.launch {
            if (FirebaseAuth.getInstance().currentUser?.uid != null)
                user.value = userRepository.getUser(FirebaseAuth.getInstance().currentUser?.uid!!)
        }
    }

    fun onNewMessage() {
    }
}