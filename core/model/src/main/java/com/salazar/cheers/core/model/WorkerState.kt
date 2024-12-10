package com.salazar.cheers.core.model

sealed interface WorkerState {
    data object ENQUEUED : WorkerState
    data object RUNNING : WorkerState
    data object SUCCEEDED: WorkerState
    data object FAILED: WorkerState
    data object BLOCKED: WorkerState
    data object CANCELLED: WorkerState

    val isFinished: Boolean
        get() = this == SUCCEEDED || this == FAILED || this == CANCELLED
}
