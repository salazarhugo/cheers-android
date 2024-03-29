package com.salazar.cheers.data.remote_config.get_remote_config

import com.salazar.cheers.data.remote_config.RemoteConfigRepository
import com.salazar.cheers.domain.get_remote_config.CheckFeatureEnabledUseCase
import com.salazar.cheers.domain.models.RemoteConfigParameter
import javax.inject.Inject

class RemoteConfigCheckFeatureEnabledUseCase @Inject constructor(
    private val repository: RemoteConfigRepository,
) : CheckFeatureEnabledUseCase {
    override suspend operator fun invoke(
        parameter: RemoteConfigParameter,
    ): Result<Boolean> {
        return repository.checkRemoteConfig(parameter = parameter)
    }
}
