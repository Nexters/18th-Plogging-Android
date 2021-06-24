package com.plogging.ecorun.util.extension

import android.content.ContentResolver
import android.content.ContentValues
import android.content.res.Resources
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.SystemClock
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import com.plogging.ecorun.R
import io.reactivex.Single
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

//상하좌우 반전 시키기
fun Bitmap.flip(x: Float, y: Float, cx: Float, cy: Float): Bitmap {
    val matrix = Matrix().apply { postScale(x, y, cx, cy) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

fun Bitmap.rotate(orientation: Int): Bitmap {
    val matrix = Matrix()
    when (orientation) {
        in 0..45, in 315..365 -> matrix.setRotate(90f)
        in 46..135 -> matrix.setRotate(180f)
        in 136..225 -> matrix.setRotate(270f)
        in 225..315 -> matrix.setRotate(0f)
        -1 -> matrix.setRotate(180f)
    }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

fun Bitmap.imageCrop(viewHeight: Float, viewWidth: Float): Bitmap {
    val changedWidth = this.height.toFloat() * (viewHeight / viewWidth)
    return Bitmap.createBitmap(
        this,
        (this.width - changedWidth.toInt()) / 2,
        0,
        changedWidth.toInt(),
        this.height
    )
}

fun Canvas.drawMark(bitmap: Bitmap, resource: Int, res: Resources) {
    val attachedImage = BitmapFactory.decodeResource(res, resource)
    // 마크 사이즈
    val resizeMarkImg = Bitmap.createScaledBitmap(
        attachedImage,
        bitmap.height / 4,
        bitmap.width / 4,
        true
    )
    // 마크 위치
    this.drawBitmap(
        resizeMarkImg,
        bitmap.height - (bitmap.height / 4f) - (bitmap.height / 20f),
        bitmap.width - (bitmap.width / 4f) - (bitmap.width / 20f),
        null
    )
}

fun Canvas.drawDate(bitmap: Bitmap, scale: Float) {
    // 글자 색, 글자 크기 지정
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.apply {
        color = Color.WHITE
        textSize = 60 * scale
    }
    //날짜 위치
    val x = bitmap.height / 20f
    val y = bitmap.width / 10f
    val date = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
    val now = System.currentTimeMillis()
    this.drawText(date.format(Date(now)), x, y, paint)
}

fun Canvas.drawDistance(
    bitmap: Bitmap,
    scale: Float,
    distance: Float?,
    resource: Int,
    res: Resources
) {
    val attachedImage = BitmapFactory.decodeResource(res, resource)
    // 마크 사이즈
    val resizeMarkImg = Bitmap.createScaledBitmap(
        attachedImage,
        bitmap.height / 12,
        bitmap.width / 12,
        true
    )
    // 마크 위치
    this.drawBitmap(
        resizeMarkImg,
        bitmap.height / 20f,
        bitmap.width - bitmap.width / 20f - resizeMarkImg.height,
        null
    )
    // 글자 색, 글자 크기 지정
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.apply {
        color = Color.WHITE
        textSize = 60 * scale
    }
    //글자 위치
    val x = bitmap.height / 20f + resizeMarkImg.width + 20f
    val y = bitmap.width - bitmap.width / 20f - resizeMarkImg.height / 4
    this.drawText(distance.toString() + "km", x, y, paint)
}

fun Bitmap.flipBitmap(cameraSelector: CameraSelector): Bitmap {
    if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
        val cx = this.width / 2f
        val cy = this.height / 2f
        this.flip(-1f, 1f, cx, cy)
    }
    return this
}

fun Canvas.drawTrash(
    bitmap: Bitmap,
    scale: Float,
    trashNum: Int?,
    resource: Int,
    res: Resources
) {
    val attachedImage = BitmapFactory.decodeResource(res, resource)
    // 마크 사이즈
    val resizeMarkImg = Bitmap.createScaledBitmap(
        attachedImage,
        bitmap.height / 12,
        bitmap.width / 12,
        true
    )
    // 마크 위치
    this.drawBitmap(
        resizeMarkImg,
        bitmap.height / 2.2f,
        bitmap.width - bitmap.width / 20f - resizeMarkImg.height,
        null
    )
    // 글자 색, 글자 크기 지정
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.apply {
        color = Color.WHITE
        textSize = 60 * scale
    }
    //글자 위치
    val x = bitmap.height / 2.2f + resizeMarkImg.width + 20f
    val y = bitmap.width - bitmap.width / 20f - resizeMarkImg.height / 4
    this.drawText(trashNum.toString(), x, y, paint)
}

fun uriToRequestBody(
    uri: Uri?,
    resolver: ContentResolver,
    parameterName: String
): Single<MultipartBody.Part> = Single.create { emitter ->
    val formatUri = uri
        ?: Uri.parse("android.resource://com.plogging.ecorun/" + R.drawable.ic_default_plogging)
    val options = BitmapFactory.Options()
    val inputStream: InputStream = resolver.openInputStream(formatUri)!!
    val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
    val resizeBitmap =
        Bitmap.createScaledBitmap(bitmap!!, bitmap.width / 4, bitmap.height / 4, false)
    val byteArrayOutputStream = ByteArrayOutputStream()
    resizeBitmap?.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val photoBody = byteArrayOutputStream.toByteArray().toRequestBody("image/*".toMediaType())
    // createFormData name이 서버에서 받는 body parameter 이름과 같아야한다.
    emitter.onSuccess(
        MultipartBody.Part.createFormData(parameterName, File(uri.toString()).name, photoBody)
    )
}

fun saveBitmapToMediaStore(bitmap: Bitmap, resolver: ContentResolver): Single<Uri> {
    return Single.create { emiiter ->
        val fileName = "Ecorun-${SystemClock.currentThreadTimeMillis()}.jpg"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues()
            with(values) {
                put(MediaStore.Images.Media.TITLE, fileName)
                put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/my_folder")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            }
            val uri = resolver
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            val fos = resolver.openOutputStream(uri!!)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos?.run {
                flush()
                close()
            }
            emiiter.onSuccess(uri)
        } else {
            val dir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    .toString() +
                        File.separator +
                        "my_folder"
            val file = File(dir)
            if (!file.exists()) {
                file.mkdirs()
            }

            val imgFile = File(file, "capture.jpg")
            val os = FileOutputStream(imgFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
            os.flush()
            os.close()

            val values = ContentValues()
            with(values) {
                put(MediaStore.Images.Media.TITLE, fileName)
                put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                put(MediaStore.Images.Media.BUCKET_ID, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            }
            resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            emiiter.onSuccess(imgFile.toUri())
        }
    }

}

fun createDrawableFromView(context: FragmentActivity, v: View): Bitmap {
    val displayMetrics = DisplayMetrics()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        context.display?.getRealMetrics(displayMetrics)
    } else context.windowManager.defaultDisplay.getRealMetrics(displayMetrics)
    v.layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    v.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
    v.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
    val bitmap = Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    v.draw(canvas)
    return bitmap
}

fun Uri.toBitmap(contentResolver: ContentResolver): Bitmap {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, this))
    } else {
        MediaStore.Images.Media.getBitmap(contentResolver, this)
    }
}

fun ByteArray.byteArrayToBitmap(): Bitmap {
    return BitmapFactory.decodeByteArray(this, 0, this.size)
}

fun Bitmap.saveImageIn(resolver: ContentResolver): Single<Uri?> {
    val filename = "IMG_${System.currentTimeMillis()}.jpg"
    var fos: OutputStream?
    return Single.create { emitter ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            var imageUri: Uri?
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                put(MediaStore.Video.Media.IS_PENDING, 1)
            }
            //use application context to get contentResolver
            resolver.also { resolver ->
                imageUri =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
            fos?.use { this.compress(Bitmap.CompressFormat.JPEG, 70, it) }
            emitter.onSuccess(imageUri!!)
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
            fos?.use { this.compress(Bitmap.CompressFormat.JPEG, 70, it) }
            emitter.onSuccess(image.toUri())
        }
    }
}

fun URL.toBitmap(): Single<Bitmap?> {
    return Single.create { emitter ->
        val image = BitmapFactory.decodeStream(this.openConnection().getInputStream())
        emitter.onSuccess(image)
    }
}
