package com.salazar.cheers.core.util

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.compose.ui.Modifier
import com.salazar.cheers.shared.util.result.getOrThrow
import com.snap.creativekit.SnapCreative
import com.snap.creativekit.exceptions.SnapMediaSizeException
import com.snap.creativekit.models.SnapPhotoContent
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.Math.min
import java.text.SimpleDateFormat
import java.util.Locale


object Utils {

    fun Context.setLocale(language: String) {
        resources.apply {
            val locale = Locale(language)
            val config = Configuration(configuration)

            createConfigurationContext(configuration)
            Locale.setDefault(locale)
            config.setLocale(locale)
            resources.updateConfiguration(config, displayMetrics)
        }
    }

    suspend fun Context.shareToSnapchat(username: String) {
        val snapCreativeKitApi = SnapCreative.getApi(this)
        val snapMediaFactory = SnapCreative.getMediaFactory(this)

        try {
            val file = File("${filesDir}/snapchat-add-friend.jpg")
            val photoFile = snapMediaFactory.getSnapPhotoFromFile(file)
            val snapPhotoContent = SnapPhotoContent(photoFile)
            val result = FirebaseDynamicLinksUtil.createShortLink("u/$username").getOrThrow()
            snapPhotoContent.attachmentUrl = result
            snapCreativeKitApi.send(snapPhotoContent)
            Log.d("SNAP", result)
        } catch (e: SnapMediaSizeException) {
            Log.e("SNAP", e.toString())
            return
        }
    }

    fun isLowerCase(username: String): Boolean {
        return username == username.lowercase()
    }

    fun hasValidChars(username: String): Boolean {
        val regex = Regex("^[._a-z0-9]+\$")
        return username.matches(regex)
    }

    fun Modifier.conditional(
        condition: Boolean,
        modifier: Modifier.() -> Modifier
    ): Modifier {
        return if (condition) {
            modifier.invoke(this)
        } else {
            this
        }
    }

    fun String.validateUsername(): Boolean {
        val regex = Regex("^(?!.*\\.\\.)(?!.*\\.\$)[^\\W][\\w.]{0,29}\$")
        return isLowerCase(this) && hasValidChars(this) && matches(regex)
    }

    fun Context.getActivity(): AppCompatActivity? = when (this) {
        is AppCompatActivity -> this
        is ContextWrapper -> baseContext.getActivity()
        else -> null
    }

    fun Bitmap.getCircularBitmapWithWhiteBorder(): Bitmap {
        val squareBitmapWidth = min(this.width, this.height)
        val dstBitmap = Bitmap.createBitmap(
            squareBitmapWidth,  // Width
            squareBitmapWidth,  // Height
            Bitmap.Config.ARGB_8888 // Config
        )
        val canvas = Canvas(dstBitmap)
        val paint = Paint()
        val rect = Rect(0, 0, squareBitmapWidth, squareBitmapWidth)
        paint.isAntiAlias = true
        val rectF = RectF(rect)
        canvas.drawOval(rectF, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        val left = ((squareBitmapWidth - this.width) / 2).toFloat()
        val top = ((squareBitmapWidth - this.height) / 2).toFloat()
        canvas.drawBitmap(this, left, top, paint)
        this.recycle()
        return dstBitmap
    }

    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this)
            .matches()
    }

    fun Context.isDarkModeOn(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }

    fun getOutputFileOptions(
        lensFacing: Int,
        photoFile: File
    ): ImageCapture.OutputFileOptions {

        // Setup image capture metadata
        val metadata = ImageCapture.Metadata().apply {
            // Mirror image when using the front camera
            isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
        }
        // Create output options object which contains file + metadata

        return ImageCapture.OutputFileOptions.Builder(photoFile)
            .setMetadata(metadata)
            .build()
    }

    fun createFile(
        baseFolder: File,
        format: String,
        extension: String
    ) =
        File(
            baseFolder, SimpleDateFormat(format, Locale.US)
                .format(System.currentTimeMillis()) + extension
        )

    fun Context.getOutputDirectory(): File {
        val mediaDir = this.externalMediaDirs.firstOrNull()?.let {
            File(it, "cheers").apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else this.filesDir
    }

    fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null)
            return null
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

    fun Bitmap.getCircledBitmap(): Bitmap {
        val output = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint()
        val rect = Rect(0, 0, this.width, this.height)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(this.width / 2f, this.height / 2f, this.width / 2f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(this, rect, rect, paint)
        return output
    }

    public fun extractImage(
        path: Uri,
        applicationContext: Context,
    ): ByteArray {
        val source: ImageDecoder.Source =
            ImageDecoder.createSource(applicationContext.contentResolver, path)
        val selectedImageBmp: Bitmap = ImageDecoder.decodeBitmap(source)

        val outputStream = ByteArrayOutputStream()
        selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)

        return outputStream.toByteArray()
    }

}