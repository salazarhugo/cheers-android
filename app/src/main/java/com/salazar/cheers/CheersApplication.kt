package com.salazar.cheers

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CheersApplication : Application() {

    val isDark = mutableStateOf(false)

    fun toggleLightTheme() {
        isDark.value = !isDark.value
    }

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
    }
}