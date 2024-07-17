package com.example.medscape20.presentation.screens.auth.signup

import androidx.lifecycle.ViewModel
import com.example.medscape20.R
import com.example.medscape20.domain.usecase.auth.signup.SignupValidateEmailUseCase
import com.example.medscape20.domain.usecase.auth.signup.ValidatePasswordUseCase
import com.example.medscape20.util.ApiResult
import com.example.medscape20.util.EmailError
import com.example.medscape20.util.PassError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class SignupState(
    val navigateToNextScreen: Boolean = false,
    val isEmailValid: Boolean = true,
    val emailError: Int? = null,
    val isPasswordValid: Boolean = true,
    val passError: Int? = null
)

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val validateEmailUseCase: SignupValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase
) : ViewModel() {

    private var _state = MutableStateFlow(SignupState())
    val state: StateFlow<SignupState> = _state.asStateFlow()

    //two way data binding flows
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")

    fun event(action: SignupEvents) {

        when (action) {

            is SignupEvents.OnNextClick -> {
                if (state.value.isEmailValid && state.value.isPasswordValid && email.value.isNotEmpty() && password.value.isNotEmpty()) {
                    _state.update {
                        it.copy(navigateToNextScreen = true)
                    }
                }
            }

            is SignupEvents.OnEmailChanged -> {

                when (val result = validateEmailUseCase(action.email)) {
                    is ApiResult.Error -> {
                        when (result.error) {
                            EmailError.EMAIL_ERROR -> {
                                _state.update {
                                    it.copy(isEmailValid = false, emailError = R.string.email_error)
                                }
                            }

                            EmailError.EMPTY -> {
                                _state.update {
                                    it.copy(isEmailValid = false, emailError = R.string.empty_error)
                                }
                            }
                        }
                    }

                    else -> {
                        _state.update {
                            it.copy(isEmailValid = true, emailError = null)
                        }
                    }
                }

            }

            is SignupEvents.OnPasswordChanged -> {

                when (val isValid = validatePasswordUseCase(action.password)) {
                    is ApiResult.Error -> {
                        when (isValid.error) {
                            PassError.EMPTY -> {
                                _state.update {
                                    it.copy(
                                        isPasswordValid = false,
                                        passError = R.string.empty_error
                                    )
                                }
                            }

                            PassError.PASS_ERROR -> {
                                _state.update {
                                    it.copy(
                                        isPasswordValid = false,
                                        passError = R.string.pass_error
                                    )
                                }
                            }
                        }

                    }

                    else -> {
                        _state.update {
                            it.copy(isPasswordValid = true, passError = null)
                        }
                    }
                }

            }

            SignupEvents.OnNavigationDone -> {
                _state.update {
                    it.copy(navigateToNextScreen = false)
                }
            }
        }

    }
}
