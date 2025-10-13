package com.example.core.domain

interface Service {
    fun source(): Source
}

data class Source(
    val name: String,
    val iconUrl: String? = null,
)