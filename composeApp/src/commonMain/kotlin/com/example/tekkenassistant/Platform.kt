package com.example.tekkenassistant

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform