package domain

import com.example.core.domain.Error

enum class WavuError: Error {
    UNKNOWN_CHARACTER,
    UNKNOWN_MOVE,
}