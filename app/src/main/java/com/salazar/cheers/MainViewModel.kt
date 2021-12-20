package com.salazar.cheers

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.Result
import com.salazar.cheers.internal.User
import com.salazar.cheers.util.FirestoreUtil
import com.salazar.cheers.util.Neo4jUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    val user2: MutableState<User?> = mutableStateOf(null)
    val user = FirestoreUtil.getCurrentUserDocumentLiveData()

    init {
        refreshUser()
    }

    private fun refreshUser() {
        viewModelScope.launch {
            when (val result = Neo4jUtil.getCurrentUser()) {
                is Result.Success -> user2.value = result.data
                is Result.Error -> user2.value = null
            }
        }
    }
}