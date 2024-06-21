package com.example.medscape20.domain.usecase.signup_details

import com.example.medscape20.util.NameError
import com.example.medscape20.util.Result
import javax.inject.Inject

class NameValidationUseCase @Inject constructor() {
    operator fun invoke(name: String): Result<Unit, NameError> {
        val regex= Regex("^[a-zA-Z ]+\$")
        val isValid= regex.matches(name)
        return when{
            name.isEmpty() -> Result.Error(NameError.EMPTY)
            name.length < 3 -> Result.Error(NameError.TOO_SHORT)
            !isValid -> Result.Error(NameError.ONLY_ALPHABETS)
            else -> Result.Success(Unit)
        }
    }
}