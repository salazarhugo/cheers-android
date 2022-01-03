package com.salazar.cheers.ui.add

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.mapbox.search.result.SearchResult
import com.salazar.cheers.internal.User
import com.salazar.cheers.workers.UploadPostWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class AddPostDialogViewModel @Inject constructor(application: Application) : ViewModel() {

    private val workManager = WorkManager.getInstance(application)

    val caption = mutableStateOf("")

    val photoUri = mutableStateOf<Uri?>(null)
    val videoUri = mutableStateOf<Uri?>(null)

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

    var uploadWorkerState: Flow<WorkInfo>? = null

    fun uploadPost() {
        val uploadWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<UploadPostWorker>().apply {
                setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                setInputData(
                    workDataOf(
                        "PHOTO_URI" to photoUri.value.toString(),
                        "VIDEO_URI" to videoUri.value.toString(),
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
