package com.example.medscape20.domain.usecase.auth.signup

import com.example.medscape20.util.ApiResult
import com.example.medscape20.util.EmailError
import javax.inject.Inject

class SignupValidateEmailUseCase @Inject constructor() {
    operator fun invoke(email: String): ApiResult<Unit, EmailError> {
        val isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        return when {
            isValid -> ApiResult.Success(Unit)
            email.isEmpty() -> ApiResult.Error(EmailError.EMPTY)
            else -> ApiResult.Error(EmailError.EMAIL_ERROR)
        }
    }

}