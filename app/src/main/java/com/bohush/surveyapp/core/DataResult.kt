package com.bohush.surveyapp.core

sealed class DataResult<out T> {
    data class Success<out T>(val data: T) : DataResult<T>()
    data class Error(val errorMessage: String) : DataResult<Nothing>()
}
