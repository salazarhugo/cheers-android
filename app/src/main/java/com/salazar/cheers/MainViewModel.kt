package com.salazar.cheers

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.data.UserRepository
import com.salazar.cheers.internal.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    val user: MutableState<User?> = mutableStateOf(null)

    init {
        refreshUser()
    }

    private fun refreshUser() {
        viewModelScope.launch {
            user.value = userRepository.getUser(FirebaseAuth.getInstance().currentUser?.uid!!)
        }
    }

    fun onNewMessage() {
    }
}