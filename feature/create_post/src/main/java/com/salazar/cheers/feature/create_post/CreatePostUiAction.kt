package com.salazar.cheers.feature.create_post

import com.salazar.cheers.core.model.Drink
import com.salazar.cheers.core.model.Privacy
import com.salazar.cheers.core.util.audio.LocalAudio


sealed class CreatePostUIAction {
    data object OnCameraClick : CreatePostUIAction()
    data object OnGalleryClick : CreatePostUIAction()
    data object OnBackPressed : CreatePostUIAction()
    data object OnSwipeRefresh : CreatePostUIAction()
    data object OnAudioClick : CreatePostUIAction()
    data object OnLocationClick : CreatePostUIAction()
    data class OnAddAudio(val localAudio: LocalAudio?) : CreatePostUIAction()
    data class OnSelectPrivacy(val privacy: Privacy) : CreatePostUIAction()
    data class OnSelectDrink(val drink: Drink) : CreatePostUIAction()
    data class OnSelectLocation(val location: String?) : CreatePostUIAction()
    data class OnCaptionChange(val text: String) : CreatePostUIAction()
    data class OnNotificationChange(val enabled: Boolean) : CreatePostUIAction()
}