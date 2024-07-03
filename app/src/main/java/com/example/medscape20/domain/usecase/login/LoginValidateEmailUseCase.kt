package com.example.medscape20.domain.usecase.login

import com.example.medscape20.util.ApiResult
import com.example.medscape20.util.EmailError
import com.example.medscape20.util.validateEmail
import javax.inject.Inject

class LoginValidateEmailUseCase @Inject constructor() {
    operator fun invoke(email: String): ApiResult<Unit, EmailError> {
        val isValid = validateEmail(email)
        return when {
            isValid -> ApiResult.Success(Unit)
            email.isEmpty() -> ApiResult.Error(EmailError.EMPTY)
            else -> ApiResult.Error(EmailError.EMAIL_ERROR)
        }
    }
}