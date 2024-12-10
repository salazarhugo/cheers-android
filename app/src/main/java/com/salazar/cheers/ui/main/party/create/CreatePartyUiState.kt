package com.salazar.cheers.ui.main.party.create

import android.net.Uri
import com.salazar.cheers.core.model.Privacy
import com.salazar.cheers.core.model.SearchSuggestion
import java.util.Date


data class CreatePartyUiState(
    val isLoading: Boolean,
    val errorMessage: String? = null,
    val name: String = "",
    val participants: List<String> = emptyList(),
    val startDateTimeMillis: Long = Date().time,
    val endDateTimeMillis: Long = Date().time,
    val endDate: String = "End date",
    val endTime: String = "End time",
    val address: String = "",
    val photo: Uri? = null,
    val description: String = "",
    val hasEndDate: Boolean = false,
    val showGuestList: Boolean = false,
    val locationName: String = "",
    val city: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val locationQuery: String = "",
    val locationResults: List<SearchSuggestion> = emptyList(),
    val privacy: Privacy = Privacy.FRIENDS,
)