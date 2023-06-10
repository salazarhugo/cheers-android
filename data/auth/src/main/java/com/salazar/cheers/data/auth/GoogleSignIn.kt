package com.salazar.cheers.data.auth

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task

fun getIdToken(task: Task<GoogleSignInAccount>?): String? {
    return task?.result?.idToken
}