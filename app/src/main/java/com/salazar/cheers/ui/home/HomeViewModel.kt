package com.salazar.cheers.ui.home

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.salazar.cheers.internal.Post
import com.salazar.cheers.util.Neo4jUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    val posts = mutableStateOf<List<Post>>(emptyList())

    fun updatePosts() {
        viewModelScope.launch {
            try {
                posts.value = Neo4jUtil.posts()
            } catch(e: Exception) {
                Log.e("HomeViewModel", e.toString())
            }
        }
    }

    fun likePost(postId: String) {
        viewModelScope.launch {
            try {
                Neo4jUtil.likePost(postId = postId)
            } catch(e: Exception) {
                Log.e("HomeViewModel", e.toString())
            }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            try {
                Neo4jUtil.deletePost(postId = postId)
            } catch(e: Exception) {
                Log.e("HomeViewModel", e.toString())
            }
        }
    }
}
