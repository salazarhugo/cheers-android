package com.salazar.cheers.feature.home.select_city

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.model.City
import com.salazar.cheers.core.model.londonCity
import com.salazar.cheers.core.model.parisCity
import com.salazar.cheers.data.user.datastore.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class SelectCityUiState(
    val isNearbyEnabled: Boolean = true,
    val currentCity: String = "",
    val cities: List<City> = emptyList(),
)

@HiltViewModel
class SelectCityViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    stateHandle: SavedStateHandle,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(SelectCityUiState())
    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        viewModelScope.launch {
            dataStoreRepository.getLocationEnabled()
                .collect(::updateNearbyEnabled)
        }

        viewModelScope.launch {
            dataStoreRepository.getCityFlow()
                .collect(::updateCity)
        }
        val cities = listOf(parisCity, londonCity)
        updateCities(cities)
    }

    fun onToggleNearby(nearbyEnabled: Boolean) {
        viewModelScope.launch {
            dataStoreRepository.updateLocationEnabled(nearbyEnabled)
        }
    }

    fun onCityClick(city: City) {
        viewModelScope.launch {
            dataStoreRepository.updateCity(city.name)
        }
    }

    private fun updateCities(cities: List<City>) {
        viewModelState.update {
            it.copy(cities = cities)
        }
    }

    fun updateCity(city: String) {
        viewModelState.update {
            it.copy(currentCity = city)
        }
    }

    private fun updateNearbyEnabled(nearbyEnabled: Boolean) {
        viewModelState.update {
            it.copy(isNearbyEnabled = nearbyEnabled)
        }
    }
}

