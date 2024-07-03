package com.example.medscape20.util

fun validateEmail(email:String):Boolean{
    val isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    return isValid
}

fun validatePassword(password:String) :Boolean{
    val pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$".toRegex()
     return pattern.matches(password)
}