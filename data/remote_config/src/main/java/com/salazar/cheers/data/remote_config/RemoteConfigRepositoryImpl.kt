package com.salazar.cheers.data.remote_config

import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.salazar.cheers.domain.models.RemoteConfigParameter
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject

class RemoteConfigRepositoryImpl @Inject constructor(
    private val firebaseRemoteConfig: FirebaseRemoteConfig,
) : RemoteConfigRepository {

    private suspend fun fetchRemoteConfig(): Result<Boolean> {
        return try {
            firebaseRemoteConfig.fetchAndActivate().await()
            Log.i(
                "FirebaseRemoteConfig",
                firebaseRemoteConfig.all.toMap().toString(),
            )
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(IOException("Error fetching remote config ${e.message}", e))
        }
    }

    override suspend fun checkRemoteConfig(
        parameter: RemoteConfigParameter,
    ): Result<Boolean> {
        return fetchRemoteConfig().mapCatching {
            firebaseRemoteConfig.getBoolean(parameter.key)
        }
    }
}
