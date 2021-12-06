package com.salazar.cheers.util

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.internal.Environment
import com.salazar.cheers.internal.Post
import org.neo4j.driver.*
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit
import com.google.gson.JsonObject

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.salazar.cheers.internal.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.neo4j.driver.Values.parameters


object Utils {

    fun openPhotoChooser(resultLauncher: ActivityResultLauncher<Intent>)
    {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        resultLauncher.launch(Intent.createChooser(intent, "Select Picture"))
    }
}