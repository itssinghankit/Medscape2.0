package com.example.medscape20.domain.usecase.signup_details

import com.example.medscape20.util.NameError
import com.example.medscape20.util.ApiResult
import javax.inject.Inject

class NameValidationUseCase @Inject constructor() {
    operator fun invoke(name: String): ApiResult<Unit, NameError> {
        val regex= Regex("^[a-zA-Z ]+\$")
        val isValid= regex.matches(name)
        return when{
            name.isEmpty() -> ApiResult.Error(NameError.EMPTY)
            name.length < 3 -> ApiResult.Error(NameError.TOO_SHORT)
            !isValid -> ApiResult.Error(NameError.ONLY_ALPHABETS)
            else -> ApiResult.Success(Unit)
        }
    }
}