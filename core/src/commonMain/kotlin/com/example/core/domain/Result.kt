package com.example.core.domain


typealias EmptyResult<E> = Result<Unit, E>

sealed interface Result<out T, out E: Error> {
    data class Success<out T>(val data: T): Result< T, Nothing>
    data class Error<out E: com.example.core.domain.Error>(val error: E): Result<Nothing, E>
}

/**
 * Allows mapping T to R on success
 */
inline fun <T, E : Error, R> Result<T, E>.map(map: (T) -> R): Result<R, E> {
    return when (this) {
        is Result.Error -> Result.Error(error)
        is Result.Success -> Result.Success(map(data))
    }
}

/**
 * When we don't need the data, just the error or status
 */
inline fun <T, E : Error> Result<T, E>.asEmptyDataResult(): EmptyResult<E> = map {}


inline fun <T, E : Error> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E> {
    return when (this) {
        is Result.Error -> this

        is Result.Success -> {
            action(this.data)
            this
        }
    }
}

inline fun <T, E : Error> Result<T, E>.onError(action: (E) -> Unit): Result<T, E> {
    return when (this) {
        is Result.Error -> {
            action(this.error)
            this
        }

        is Result.Success -> this
    }
}