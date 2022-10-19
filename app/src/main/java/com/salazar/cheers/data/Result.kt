package com.salazar.cheers.data

/**
 * A generic class that holds a value or an exception
 */
sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}

fun <T> Result<T>.successOr(fallback: T): T {
    return (this as? Result.Success<T>)?.data ?: fallback
}

/**
 * A generic class that holds a value or an exception
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T?) : Resource<T>(data)
    class Error<T>(
        message: String,
        data: T? = null
    ) : Resource<T>(data, message)

    class Loading<T>(val isLoading: Boolean = true) : Resource<T>(null)
}