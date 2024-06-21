package com.example.medscape20.domain.usecase.signup_details

import com.example.medscape20.util.MobileError
import com.example.medscape20.util.Result
import javax.inject.Inject

class MobileValidationUseCase @Inject constructor() {
    operator fun invoke(mobile: String): Result<Unit, MobileError> {
        val regex = Regex("^[6-9]\\d{9}\$")
        val isValid= regex.matches(mobile)
        return when{
            mobile.isEmpty() -> Result.Error(MobileError.EMPTY)
            mobile.length < 10 -> Result.Error(MobileError.TOO_SHORT)
            !isValid -> Result.Error(MobileError.INVALID)
            else -> Result.Success(Unit)
        }
    }
}