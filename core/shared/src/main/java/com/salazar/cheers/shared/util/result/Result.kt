package com.salazar.cheers.shared.util.result

typealias RootError = Error

sealed interface Result<out D, out E : RootError> {
    data class Success<out D, out E : RootError>(val data: D) : Result<D, E>
    data class Error<out D, out E : RootError>(val error: E) : Result<D, E>
}

fun <D, E : RootError> Result<D, E>.getOrThrow(): D {
    return when(this) {
        is Result.Success -> this.data
        is Result.Error -> throw Exception(this.error.toString())
    }
}

fun <D, E : RootError> Result<D, E>.getOrNull(): D? {
    return when(this) {
        is Result.Success -> this.data
        is Result.Error -> null
    }
}
