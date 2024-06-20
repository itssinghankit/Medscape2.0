package com.example.medscape20.domain.usecase.signup_details

import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ValidatePasswordUseCase @Inject constructor() {
    operator fun invoke(password: String): Boolean {
        val pattern="^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$".toRegex()
        return pattern.matches(password)
    }
}