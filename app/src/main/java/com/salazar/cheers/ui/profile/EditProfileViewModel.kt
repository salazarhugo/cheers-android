package com.salazar.cheers.ui.profile

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.User
import com.salazar.cheers.util.FirestoreUtil
import com.salazar.cheers.util.Neo4jUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(): ViewModel() {

    val user = FirestoreUtil.getCurrentUserDocumentLiveData()

    val name = mutableStateOf("")

    fun onNameChanged(name: String) {
        this.name.value = name
    }

    val user2 = mutableStateOf(User())
    val currentUser = mutableStateOf(User())

    fun getCurrentUser() {
        viewModelScope.launch {
            try {
                currentUser.value = Neo4jUtil.getCurrentUser()
            } catch(e: Exception) {
                Log.e("HomeViewModel", e.toString())
            }
        }
    }

    fun getUser(userId: String) {
        viewModelScope.launch {
            try {
                user2.value = Neo4jUtil.getUser(userId)
            } catch(e: Exception) {
                Log.e("HomeViewModel", e.toString())
            }
        }
    }
}