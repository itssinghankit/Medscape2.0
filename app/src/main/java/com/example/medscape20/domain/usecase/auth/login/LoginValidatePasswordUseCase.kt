package com.example.medscape20.domain.usecase.auth.login

import com.example.medscape20.util.ApiResult
import com.example.medscape20.util.PassError
import com.example.medscape20.util.validatePassword
import javax.inject.Inject

class LoginValidatePasswordUseCase @Inject constructor() {
    operator fun invoke(password: String): ApiResult<Unit, PassError> {
        val isValid = validatePassword(password)
        return when {
            isValid -> ApiResult.Success(Unit)
            password.isEmpty() -> ApiResult.Error(PassError.EMPTY)
            else -> ApiResult.Error(PassError.PASS_ERROR)
        }
    }
}