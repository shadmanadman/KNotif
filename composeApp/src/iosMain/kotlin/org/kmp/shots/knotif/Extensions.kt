package org.kmp.shots.knotif

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.free
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.set
import kotlinx.cinterop.staticCFunction
import platform.CoreGraphics.CGColorRenderingIntent.kCGRenderingIntentDefault
import platform.CoreGraphics.CGColorSpaceCreateDeviceRGB
import platform.CoreGraphics.CGDataProviderCreateWithData
import platform.CoreGraphics.CGImageAlphaInfo
import platform.CoreGraphics.CGImageCreate
import platform.CoreGraphics.kCGBitmapByteOrder32Big
import platform.Foundation.NSString
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUUID
import platform.Foundation.stringByAppendingPathComponent
import platform.Foundation.writeToFile
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation

@OptIn(ExperimentalForeignApi::class)
internal fun ImageBitmap.toUIImage(): UIImage? {
    val skiaBitmap = this.asSkiaBitmap()
    val width = skiaBitmap.width
    val height = skiaBitmap.height

    val bytesPerPixel = 4
    val bitsPerComponent = 8
    val bytesPerRow = bytesPerPixel * width


    val pixelBytes: ByteArray? = skiaBitmap.readPixels(
        dstInfo = org.jetbrains.skia.ImageInfo.makeN32Premul(width, height),
        dstRowBytes = bytesPerRow
    )

    val imageSize = pixelBytes?.size ?: 0
    val buffer = nativeHeap.allocArray<ByteVar>(imageSize)
    for (i in 0 until imageSize) {
        buffer[i] = pixelBytes!![i]
    }

    val provider = CGDataProviderCreateWithData(
        info = null,
        data = buffer,
        size = imageSize.toULong(),
        releaseData = staticCFunction { _, data, _ -> nativeHeap.free(data!!) })

    val colorSpace = CGColorSpaceCreateDeviceRGB()
    val bitmapInfo =
        kCGBitmapByteOrder32Big or CGImageAlphaInfo.kCGImageAlphaPremultipliedLast.value

    val cgImage = CGImageCreate(
        width.toULong(),
        height.toULong(),
        bitsPerComponent.toULong(),
        (bytesPerPixel * bitsPerComponent).toULong(),
        bytesPerRow.toULong(),
        colorSpace,
        bitmapInfo.toUInt(),
        provider,
        null,
        false,
        kCGRenderingIntentDefault,
    ) ?: return null

    return UIImage.imageWithCGImage(cgImage)
}


internal fun ImageBitmap.saveImageBitmapToFileUrl(): NSURL? {
    // Convert ImageBitmap to UIImage (You will need a helper function to convert)
    val uiImage = this.toUIImage() ?: return null

    // Convert UIImage to PNG NSData
    val pngData = UIImagePNGRepresentation(uiImage) ?: return null

    // Get temporary directory path
    val tempDir = NSTemporaryDirectory()
    val filename = "notif_image_${NSUUID().UUIDString}.png"
    val filePath = (tempDir as NSString).stringByAppendingPathComponent(filename)

    // Write NSData to file
    val success = pngData.writeToFile(filePath, true)
    return if (success) {
        NSURL.fileURLWithPath(filePath)
    } else {
        println("Failed to write image to file.")
        null
    }
}