package com.example.medscape20.util

typealias RootError = Error
sealed interface Result<out D,out E:RootError> {
    //    data class Success<D>(val data: D) : ApiResult2<D, Nothing>
//    data class Failure<E:RootError>(val error: E) : ApiResult2<Nothing, E>
    data class Success<out D,out E:RootError>(val data:D):Result<D,E>
    data class Error<out D,out E:RootError>(val error: E):Result<D,E>
    object Loading: Result<Nothing, Nothing>

}