package com.salazar.cheers.ui.add

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import androidx.work.*
import com.salazar.cheers.internal.Post
import com.salazar.cheers.util.Neo4jUtil
import com.salazar.cheers.workers.UploadPostWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPostDialogViewModel @Inject constructor(application: Application) : ViewModel() {

    private val workManager = WorkManager.getInstance(application)

    val caption = mutableStateOf("")
    val photoUri = mutableStateOf<Uri?>(null)

    fun onCaptionChanged(caption: String) {
        this.caption.value = caption
    }

    fun uploadPost()
    {
        val uploadWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<UploadPostWorker>().apply {
                setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                setInputData(workDataOf(
                    "PHOTO_URI" to photoUri.value.toString(),
                    "PHOTO_CAPTION" to caption.value
                ))
            }
                .build()

        // Actually start the work
        workManager.enqueue(uploadWorkRequest)
    }


//    fun addPost(post: Post) {
//        viewModelScope.launch {
//            try {
//                Neo4jUtil.addPost(post)
//            } catch(e: Exception) {
//                Log.e("HomeViewModel", e.toString())
//            }
//        }
//    }
}
