package com.salazar.cheers.shared.data

import com.salazar.common.util.result.DataError
import io.grpc.Status
import io.grpc.StatusException

fun StatusException.toDataError(): DataError.Network {
    return when(status.code) {
        Status.Code.OK -> DataError.Network.UNKNOWN
        Status.Code.CANCELLED -> DataError.Network.UNKNOWN
        Status.Code.UNKNOWN -> DataError.Network.UNKNOWN
        Status.Code.INVALID_ARGUMENT -> DataError.Network.UNKNOWN
        Status.Code.DEADLINE_EXCEEDED -> DataError.Network.REQUEST_TIMEOUT
        Status.Code.NOT_FOUND -> DataError.Network.NOT_FOUND
        Status.Code.ALREADY_EXISTS -> DataError.Network.ALREADY_EXISTS
        Status.Code.PERMISSION_DENIED -> DataError.Network.UNKNOWN
        Status.Code.RESOURCE_EXHAUSTED -> DataError.Network.UNKNOWN
        Status.Code.FAILED_PRECONDITION -> DataError.Network.UNKNOWN
        Status.Code.ABORTED -> DataError.Network.UNKNOWN
        Status.Code.OUT_OF_RANGE -> DataError.Network.UNKNOWN
        Status.Code.UNIMPLEMENTED -> DataError.Network.UNKNOWN
        Status.Code.INTERNAL -> DataError.Network.UNKNOWN
        Status.Code.UNAVAILABLE -> DataError.Network.UNKNOWN
        Status.Code.DATA_LOSS -> DataError.Network.UNKNOWN
        Status.Code.UNAUTHENTICATED -> DataError.Network.UNKNOWN
        else -> DataError.Network.UNKNOWN
    }
}