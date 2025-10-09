package com.example.core.util

fun String.removeWhiteSpace(): String {
    return this.replace("\\s".toRegex(), "")
}