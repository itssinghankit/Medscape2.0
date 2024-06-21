package com.example.medscape20.domain.usecase.signup

import com.example.medscape20.util.PassError
import com.example.medscape20.util.Result
import javax.inject.Inject

class ValidatePasswordUseCase @Inject constructor() {
    operator fun invoke(password: String): Result<Unit, PassError> {
        val pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$".toRegex()
        val isMatched = pattern.matches(password)
        return when {
            isMatched -> Result.Success(Unit)
            password.isEmpty() -> Result.Error(PassError.EMPTY)
            else -> Result.Error(PassError.PASS_ERROR)
        }
    }
}