package com.salazar.cheers

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.firebase.FirebaseApp
import com.salazar.cheers.internal.Environment
import dagger.hilt.android.HiltAndroidApp
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Config
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class CheersApplication : Application(), Configuration.Provider {

    val isDark = mutableStateOf(false)

    fun toggleLightTheme() {
        isDark.value = !isDark.value
    }

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
        initDatabase()
    }
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    private fun initDatabase()
    {
        val driver: Driver = GraphDatabase.driver(
            Environment.DEFAULT_URL,
            AuthTokens.basic(Environment.DEFAULT_USER, Environment.DEFAULT_PASS),
            Config.builder()
                .withMaxConnectionLifetime(8, TimeUnit.MINUTES)
                .withConnectionLivenessCheckTimeout(2, TimeUnit.MINUTES).build()
        )
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}