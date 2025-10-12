package domain

import com.example.core.domain.Error

enum class BotError: Error {
    INVALID_QUERY,
    UNKNOWN_CHARACTER,
    UNKNOWN_MOVE,
}