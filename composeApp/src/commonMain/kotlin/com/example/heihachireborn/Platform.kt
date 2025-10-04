package com.example.heihachireborn

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform