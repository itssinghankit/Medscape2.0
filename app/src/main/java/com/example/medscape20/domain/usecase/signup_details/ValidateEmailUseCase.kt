package com.example.medscape20.domain.usecase.signup_details

import com.example.medscape20.util.EmailError
import com.example.medscape20.util.Result
import javax.inject.Inject

class ValidateEmailUseCase @Inject constructor() {

    operator fun invoke(email: String): Result<Unit, EmailError> {
        val isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        return when (isValid) {
            true -> Result.Success(Unit)
            false -> Result.Error(EmailError.EMAIL_ERROR)
        }
    }

}
