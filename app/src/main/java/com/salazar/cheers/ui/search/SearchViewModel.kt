package com.salazar.cheers.ui.search

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.internal.User
import com.salazar.cheers.util.FirestoreUtil
import com.salazar.cheers.util.Neo4jUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(): ViewModel() {

    val name = mutableStateOf("")

    fun onNameChanged(name: String) {
        this.name.value = name
    }

    val conversation = mutableStateOf(listOf(User()))

    fun queryUsers(query: String) {
        viewModelScope.launch {
            try {
                conversation.value = Neo4jUtil.queryUsers(query)
            } catch(e: Exception) {
                Log.e("HomeViewModel", e.toString())
            }
        }
    }
}