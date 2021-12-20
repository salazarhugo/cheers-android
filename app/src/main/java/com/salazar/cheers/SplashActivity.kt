package com.salazar.cheers

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.startActivity


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (FirebaseAuth.getInstance().currentUser == null)
            startActivity<SignInActivity>()
        else
            startActivity<MainActivity>()

        finish()
    }
}