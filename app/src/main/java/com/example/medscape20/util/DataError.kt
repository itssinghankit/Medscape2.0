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
    EMPTY,
    EMAIL_ERROR
}
enum class PassError: Error{
    EMPTY,
    PASS_ERROR
}
enum class NameError:Error{
    EMPTY,
    TOO_SHORT,
    ONLY_ALPHABETS
}
enum class MobileError:Error{
    EMPTY,
    TOO_SHORT,
    INVALID
}