package org.kmp.shots.knotif

fun String.colorFromHex(): androidx.compose.ui.graphics.Color {
    val cleanHex = this.removePrefix("#")
    val colorLong = cleanHex.toLong(16)
    return when (cleanHex.length) {
        6 -> androidx.compose.ui.graphics.Color((0xFF shl 24) or colorLong.toInt())
        8 -> androidx.compose.ui.graphics.Color(colorLong.toInt())
        else -> throw IllegalArgumentException("Invalid hex color: $this")
    }
}