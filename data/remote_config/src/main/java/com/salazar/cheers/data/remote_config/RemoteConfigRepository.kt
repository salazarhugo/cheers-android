package com.salazar.cheers.data.remote_config

import com.salazar.cheers.domain.models.RemoteConfigParameter

interface RemoteConfigRepository {
    suspend fun checkRemoteConfig(
        parameter: RemoteConfigParameter,
    ): Result<Boolean>
}
