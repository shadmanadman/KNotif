package org.kmp.shots.knotif

import androidx.compose.ui.graphics.ImageBitmap
internal interface KDrawableController{
    fun toByteArray(): ByteArray?
    fun toImageBitmap(): ImageBitmap?
}
internal expect class KDrawableResolver: KDrawableController {
    override fun toByteArray(): ByteArray?
    override fun toImageBitmap(): ImageBitmap?
}