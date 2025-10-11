package util

internal fun String.removeTag(): String {
    return this
        .substringAfter("@")
        .substringAfter(" ")
}