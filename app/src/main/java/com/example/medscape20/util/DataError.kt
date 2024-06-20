package com.example.medscape20.util

sealed interface DataError: Error {
    enum class Network:DataError{
        NO_INTERNET,
        INTERNAL_SERVER_ERROR,
        NOT_FOUND,
        UNAUTHORIZED,
        TIMEOUT,
        UNKNOWN,
        ALREADY_CREATED
    }
    enum class Local:DataError{
        DISK_FULL,
        STORAGE_PERMISSION_DENIED
    }
}

enum class EmailError: Error{
    EMAIL_ERROR
}