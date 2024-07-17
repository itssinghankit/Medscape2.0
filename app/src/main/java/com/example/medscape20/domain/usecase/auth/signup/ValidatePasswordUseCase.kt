package com.example.medscape20.domain.usecase.auth.signup

import com.example.medscape20.util.PassError
import com.example.medscape20.util.ApiResult
import javax.inject.Inject

class ValidatePasswordUseCase @Inject constructor() {
    operator fun invoke(password: String): ApiResult<Unit, PassError> {
        val pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$".toRegex()
        val isMatched = pattern.matches(password)
        return when {
            isMatched -> ApiResult.Success(Unit)
            password.isEmpty() -> ApiResult.Error(PassError.EMPTY)
            else -> ApiResult.Error(PassError.PASS_ERROR)
        }
    }
}