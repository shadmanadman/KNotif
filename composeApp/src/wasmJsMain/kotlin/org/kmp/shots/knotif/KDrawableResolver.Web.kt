package org.kmp.shots.knotif

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLImageElement

internal  actual class KDrawableResolver(private val imageElement: HTMLImageElement):
    KDrawableController{
    actual override fun toByteArray(): ByteArray? {
        if (imageElement == null) {
            println("toByteArray null")
            return null
        }

        val canvas = document.createElement("canvas") as HTMLCanvasElement
        canvas.width = imageElement.naturalWidth
        canvas.height = imageElement.naturalHeight

        val ctx = canvas.getContext("2d") as CanvasRenderingContext2D
        ctx.drawImage(imageElement, 0.0, 0.0)

        // toDataURL returns base64 string; we convert it to ByteArray
        val dataUrl = canvas.toDataURL("image/jpeg", 1.0.toJsNumber())
        val base64 = dataUrl.substringAfter("base64,")
        return try {
            decodeBase64ToByteArray(base64)
        } catch (e: Throwable) {
            println("Failed to decode base64: ${e.message}")
            null
        }
    }

    actual override fun toImageBitmap(): ImageBitmap? {
        println("toImageBitmap not yet supported in wasmJs. please use the byte array")
        return null
    }

    private fun decodeBase64ToByteArray(base64: String): ByteArray {
        val binaryString = window.atob(base64)
        val bytes = ByteArray(binaryString.length)
        for (i in binaryString.indices) {
            bytes[i] = binaryString[i].code.toByte()
        }
        return bytes
    }
}