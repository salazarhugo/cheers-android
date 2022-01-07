package com.salazar.cheers.ui.add

import android.app.Application
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.work.*
import com.mapbox.search.result.SearchResult
import com.salazar.cheers.internal.PostType
import com.salazar.cheers.internal.User
import com.salazar.cheers.workers.UploadPostWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddPostDialogViewModel @Inject constructor(application: Application) : ViewModel() {

    private val workManager = WorkManager.getInstance(application)

    val caption = mutableStateOf("")

    val postType = mutableStateOf(PostType.TEXT)
    val mediaUri = mutableStateOf<Uri?>(null)

    val videoThumbnail = mutableStateOf<Uri?>(null)

    val location = mutableStateOf("Current Location")
    val locationResults = mutableStateOf<List<SearchResult>>(emptyList())
    val selectedLocation = mutableStateOf<SearchResult?>(null)
    val selectedTagUsers = mutableStateListOf<User>()
    val showOnMap = mutableStateOf(true)

    fun onShowOnMapChanged(showOnMap: Boolean) {
        this.showOnMap.value = showOnMap
    }

    fun selectTagUser(user: User) {
        if (selectedTagUsers.contains(user))
            selectedTagUsers.remove(user)
        else
            selectedTagUsers.add(user)
    }

    fun unselectLocation() {
        selectedLocation.value = null
    }

    fun selectLocation(location: SearchResult) {
        selectedLocation.value = location
    }

    fun updateLocationResults(results: List<SearchResult>) {
        this.locationResults.value = results
    }

    fun onCaptionChanged(caption: String) {
        this.caption.value = caption
    }

    fun setPostImage(image: Uri) {
        mediaUri.value = image
        postType.value = PostType.IMAGE
    }

    fun setPostVideo(video: Uri) {
        mediaUri.value = video
        postType.value = PostType.VIDEO
//        videoThumbnail.value = ;
        ThumbnailUtils.createVideoThumbnail(video.path!!, MediaStore.Images.Thumbnails.MINI_KIND)
    }

    var uploadWorkerState: Flow<WorkInfo>? = null
    val id = mutableStateOf<UUID?>(null)

    fun uploadPost() {
        val uploadWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<UploadPostWorker>().apply {
                setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                setInputData(
                    workDataOf(
                        "MEDIA_URI" to mediaUri.value.toString(),
                        "POST_TYPE" to postType.value,
                        "PHOTO_CAPTION" to caption.value,
                        "LOCATION_NAME" to selectedLocation.value?.name,
                        "LOCATION_LATITUDE" to selectedLocation.value?.coordinate?.latitude(),
                        "LOCATION_LONGITUDE" to selectedLocation.value?.coordinate?.longitude(),
                        "TAG_USER_IDS" to selectedTagUsers.map { it.id }.toTypedArray(),
                        "SHOW_ON_MAP" to showOnMap.value,
                    )
                )
            }
                .build()

        id.value = uploadWorkRequest.id

        // Actually start the work
        workManager.enqueue(uploadWorkRequest)

        uploadWorkerState = workManager.getWorkInfoByIdLiveData(uploadWorkRequest.id).asFlow()
    }


    fun updateLocation(location: String) {
        this.location.value = location
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
