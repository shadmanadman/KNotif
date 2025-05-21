package org.kmp.shots.knotif

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.ByteArrayOutputStream

internal actual class KDrawableResolver (private val bitmap: android.graphics.Bitmap?): KDrawableController {
    actual override fun toByteArray(): ByteArray? {
        return if (bitmap != null) {
            val byteArrayOutputStream = ByteArrayOutputStream()
            @Suppress("MagicNumber") bitmap.compress(
                android.graphics.Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream
            )
            byteArrayOutputStream.toByteArray()
        } else {
            println("toByteArray null")
            null
        }
    }

    actual override fun toImageBitmap(): ImageBitmap? {
        val byteArray = toByteArray()
        return if (byteArray != null) {
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size).asImageBitmap()
        } else {
            println("toImageBitmap null")
            null
        }
    }

}