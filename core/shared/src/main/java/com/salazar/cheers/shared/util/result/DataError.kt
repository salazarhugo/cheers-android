package com.salazar.cheers.shared.util.result

sealed interface DataError : Error {
    enum class Auth : DataError {
        NOT_SIGNED_IN,
    }

    enum class Network : DataError {
        REQUEST_TIMEOUT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        PAYLOAD_TOO_LARGE,
        SERVER_ERROR,
        SERIALIZATION,
        NOT_FOUND,
        ALREADY_EXISTS,
        UNKNOWN
    }

    enum class Local : DataError {
        DISK_FULL
    }
}