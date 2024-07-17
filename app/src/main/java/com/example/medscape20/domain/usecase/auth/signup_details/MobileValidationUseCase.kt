package com.example.medscape20.domain.usecase.auth.signup_details

import com.example.medscape20.util.MobileError
import com.example.medscape20.util.ApiResult
import javax.inject.Inject

class MobileValidationUseCase @Inject constructor() {
    operator fun invoke(mobile: String): ApiResult<Unit, MobileError> {
        val regex = Regex("^[6-9]\\d{9}\$")
        val isValid= regex.matches(mobile)
        return when{
            mobile.isEmpty() -> ApiResult.Error(MobileError.EMPTY)
            mobile.length < 10 -> ApiResult.Error(MobileError.TOO_SHORT)
            !isValid -> ApiResult.Error(MobileError.INVALID)
            else -> ApiResult.Success(Unit)
        }
    }
}