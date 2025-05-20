package org.kmp.shots.knotif

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform