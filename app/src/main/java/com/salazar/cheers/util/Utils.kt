package com.salazar.cheers.util

import android.content.*
import android.content.res.Configuration
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import com.google.protobuf.Timestamp
import com.salazar.cheers.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


object Utils {

    fun isLowerCase(username: String): Boolean {
        return username == username.lowercase()
    }

    fun hasValidChars(username: String): Boolean {
        val regex = Regex("^[._a-z0-9]+\$")
        return username.matches(regex)
    }

    fun String.validateUsername(): Boolean {
        val regex = Regex("^(?!.*\\.\\.)(?!.*\\.\$)[^\\W][\\w.]{0,29}\$")
        return isLowerCase(this) && hasValidChars(this) && matches(regex)
    }

    fun getCurrentUserToken(): String {
        return ""
    }

    fun Context.copyToClipboard(text: CharSequence) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Link copied", Toast.LENGTH_SHORT).show()
    }

    fun Context.getActivity(): AppCompatActivity? = when (this) {
        is AppCompatActivity -> this
        is ContextWrapper -> baseContext.getActivity()
        else -> null
    }

    fun Bitmap.getCircularBitmapWithWhiteBorder(borderWidth: Int): Bitmap? {
        if (this.isRecycled) return null

        val size = 0
        val width = size + borderWidth
        val height = size + borderWidth
        val canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val shader = BitmapShader(this, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = shader
        val canvas = Canvas(canvasBitmap)
        val radius = if (width > height) height.toFloat() / 2f else width.toFloat() / 2f
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius, paint)
        paint.shader = null
        paint.style = Paint.Style.STROKE
        paint.color = Color.WHITE
        paint.strokeWidth = borderWidth.toFloat()
        canvas.drawCircle(
            (width / 2).toFloat(),
            (height / 2).toFloat(),
            radius - borderWidth / 2,
            paint
        )
        return canvasBitmap
    }

    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this)
            .matches()
    }

    fun Context.isDarkModeOn(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }

    fun getImageUri(
        inContext: Context,
        inImage: Bitmap
    ): Uri? {
//        val bytes = ByteArrayOutputStream()
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "", null)
        return Uri.parse(path)
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
            File(it, this.resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else this.filesDir
    }

    fun openPhotoVideoChooser(
        resultLauncher: ActivityResultLauncher<Intent>,
        allowMultiple: Boolean = false
    ) {
        val intent = Intent()
        intent.type = "image/* video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultiple)
        resultLauncher.launch(Intent.createChooser(intent, "Select Picture"))
    }

    fun openPhotoChooser(
        resultLauncher: ActivityResultLauncher<Intent>,
        allowMultiple: Boolean = false
    ) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultiple)
        resultLauncher.launch(Intent.createChooser(intent, "Select Picture"))
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

    fun Timestamp.isToday(): Boolean {
        val c = Calendar.getInstance()

        c[Calendar.HOUR_OF_DAY] = 0
        c[Calendar.MINUTE] = 0
        c[Calendar.SECOND] = 0
        c[Calendar.MILLISECOND] = 0

        val today = c.time

        return this.seconds > today.time
    }

}