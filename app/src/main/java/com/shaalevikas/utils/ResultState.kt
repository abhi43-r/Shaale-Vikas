package com.shaalevikas.utils

sealed interface ResultState<out T> {
    data class Success<T>(val data: T) : ResultState<T>
    data class Error(val message: String, val throwable: Throwable? = null) : ResultState<Nothing>
}
