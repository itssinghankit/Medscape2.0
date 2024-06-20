//package com.example.sharesphere.domain.newstructure
//
//class TextValidator {
//
//    fun validatePassword(password:String):ApiResult2<Unit,PasswordError>{
//        return when{
//            password.length<6 -> ApiResult2.Error(PasswordError.TOO_SHORT)
//            password.length>12 -> ApiResult2.Error(PasswordError.TOO_LONG)
//            else -> ApiResult2.Success(Unit)
//        }
//    }
//
//    enum class PasswordError:Error{
//        TOO_SHORT,
//        TOO_LONG
//    }
//}/
