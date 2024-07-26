package com.salazar.cheers.shared.util

sealed interface Resource<out T> {
    data class Success<T>(val data: T?) : Resource<T>
    data class Error<T>(val message: String?, val data: Throwable? = null) : Resource<T>
    data class Loading<T>(val isLoading: Boolean) : Resource<T>
}