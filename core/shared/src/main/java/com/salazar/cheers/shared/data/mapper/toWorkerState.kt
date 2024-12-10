package com.salazar.cheers.shared.data.mapper

import androidx.work.WorkInfo
import com.salazar.cheers.core.model.WorkerState

fun WorkInfo.State.toWorkerState(): WorkerState {
    return when (this) {
        WorkInfo.State.ENQUEUED -> WorkerState.ENQUEUED
        WorkInfo.State.RUNNING -> WorkerState.RUNNING
        WorkInfo.State.SUCCEEDED -> WorkerState.SUCCEEDED
        WorkInfo.State.FAILED -> WorkerState.FAILED
        WorkInfo.State.BLOCKED -> WorkerState.BLOCKED
        WorkInfo.State.CANCELLED -> WorkerState.CANCELLED
    }
}
