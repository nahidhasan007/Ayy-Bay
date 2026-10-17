package com.ayybay.app.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/** ACTIVITY_RECOGNITION is a runtime permission from API 29; earlier versions need none. */
object StepPermission {

    fun isRequired(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    fun isGranted(context: Context): Boolean =
        !isRequired() || ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
}
