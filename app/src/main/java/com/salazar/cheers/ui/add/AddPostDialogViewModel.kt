package com.salazar.cheers.ui.add

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.salazar.cheers.internal.Post
import com.salazar.cheers.util.Neo4jUtil
import kotlinx.coroutines.launch

class AddPostDialogViewModel : ViewModel() {

    val caption = mutableStateOf("")

    fun onCaptionChanged(caption: String) {
        this.caption.value = caption
    }

    fun addPost(post: Post) {
        viewModelScope.launch {
            try {
                Neo4jUtil.addPost(post)
            } catch(e: Exception) {
                Log.e("HomeViewModel", e.toString())
            }
        }
    }
}
