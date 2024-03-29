package com.salazar.cheers.domain.get_remote_config

import com.salazar.cheers.domain.models.RemoteConfigParameter

interface CheckFeatureEnabledUseCase {
    suspend operator fun invoke(parameter: RemoteConfigParameter): Result<Boolean>
}
