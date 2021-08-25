package com.example.cargo.utils

sealed class MySealed<T>(
    val data: T? = null,
    val exception: Exception? = null
) {
    class Loading<T>(data: T?) : MySealed<T>(data)
    class Success<T>(data: T) : MySealed<T>(data)
    class Error<T>(data: T? = null, exception: Exception?) : MySealed<T>(data, exception)
}