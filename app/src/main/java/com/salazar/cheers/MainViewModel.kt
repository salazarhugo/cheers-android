package com.salazar.cheers

import android.util.Log
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.Result
import com.salazar.cheers.internal.User
import com.salazar.cheers.util.FirestoreUtil
import com.salazar.cheers.backend.Neo4jUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    val user2: MutableState<User?> = mutableStateOf(null)
    val user = FirestoreUtil.getCurrentUserDocumentLiveData()
    val unreadMessages = mutableStateOf(0)
    val sheetState: ModalBottomSheetState = ModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    var selectedPostId = mutableStateOf("")

    init {
        refreshUser()
    }

    fun selectPost(postId: String) {
        selectedPostId.value = postId
    }

    private fun refreshUser() {
        viewModelScope.launch {
            when (val result = Neo4jUtil.getCurrentUser()) {
                is Result.Success -> user2.value = result.data
                is Result.Error -> user2.value = null
            }
        }
    }

    fun onNewMessage() {
        unreadMessages.value += 1
    }

    fun deletePost() {
        viewModelScope.launch {
            try {
                Neo4jUtil.deletePost(postId = selectedPostId.value)
            } catch (e: Exception) {
                Log.e("HomeViewModel", e.toString())
            }
        }
    }
}