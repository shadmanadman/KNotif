package org.kmp.shots.knotif

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image

internal actual class KDrawableResolver(private val image: Image?): KDrawableController{
    actual override fun toByteArray(): ByteArray? {
        return image?.encodeToData()?.bytes?.also {
            println("toByteArray size: ${it.size}")
        } ?: run {
            println("toByteArray null")
            null
        }
    }

    actual override fun toImageBitmap(): ImageBitmap? {
        return image?.toComposeImageBitmap() ?: run {
            println("toImageBitmap null")
            null
        }
    }
}