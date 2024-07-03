package com.example.medscape20.presentation.screens.auth.login

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medscape20.R
import com.example.medscape20.data.remote.dto.login.LoginReqDto
import com.example.medscape20.domain.usecase.login.LoginUseCase
import com.example.medscape20.domain.usecase.login.LoginValidateEmailUseCase
import com.example.medscape20.domain.usecase.login.LoginValidatePasswordUseCase
import com.example.medscape20.util.ApiResult
import com.example.medscape20.util.DataError
import com.example.medscape20.util.EmailError
import com.example.medscape20.util.PassError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class LoginStates(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    @StringRes val errMessage: Int? = null,
    val navigateToHome: Boolean = false,
    val isEmailValid: Boolean = true,
    @StringRes val emailError: Int? = null,
    val isPassValid: Boolean = true,
    @StringRes val passError: Int? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginValidateEmailUseCase: LoginValidateEmailUseCase,
    private val loginValidatePasswordUseCase: LoginValidatePasswordUseCase,
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginStates())
    val state: StateFlow<LoginStates> = _state.asStateFlow()

    val email = MutableStateFlow("")
    val password = MutableStateFlow("")

    fun event(action: LoginEvents) {

        when (action) {
            is LoginEvents.OnEmailChanged -> {
                when (val result = loginValidateEmailUseCase(action.email)) {
                    is ApiResult.Error -> {
                        when (result.error) {
                            EmailError.EMPTY -> {
                                _state.update {
                                    it.copy(
                                        isEmailValid = false,
                                        emailError = R.string.empty_error
                                    )
                                }
                            }

                            EmailError.EMAIL_ERROR -> {
                                _state.update {
                                    it.copy(
                                        isEmailValid = false,
                                        emailError = R.string.email_error
                                    )
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

            is LoginEvents.OnPassChange -> {
                when (val result = loginValidatePasswordUseCase(action.pass)) {
                    is ApiResult.Error -> {
                        when (result.error) {
                            PassError.EMPTY -> {
                                _state.update {
                                    it.copy(
                                        isPassValid = false,
                                        passError = R.string.empty_error
                                    )
                                }
                            }

                            PassError.PASS_ERROR -> {
                                _state.update {
                                    it.copy(
                                        isPassValid = false,
                                        passError = R.string.pass_error
                                    )
                                }
                            }
                        }
                    }

                    else -> {
                        _state.update {
                            it.copy(isPassValid = true, passError = null)
                        }
                    }
                }
            }

            LoginEvents.OnErrorShown -> {
                _state.update {
                    it.copy(isError = false, errMessage = null)
                }
            }
            LoginEvents.OnLoginClicked -> {
                if(_state.value.isEmailValid && _state.value.isPassValid && email.value.isNotEmpty() && password.value.isNotEmpty()){
                    login()
                }
            }
            LoginEvents.OnNavigationDone -> {

                _state.update {
                    it.copy(navigateToHome = false)
                }
                Timber.d("navigation done ${_state.value.navigateToHome}")
            }
        }

    }

    private fun login() {

        viewModelScope.launch {
            val loginReqDto=LoginReqDto(email.value,password.value)
            loginUseCase(loginReqDto).collect{ result->
                when(result){
                    is ApiResult.Error ->{
                        when(result.error){

                            DataError.Network.NOT_FOUND -> {
                                _state.update {
                                    it.copy(
                                        isError = true,
                                        errMessage = R.string.login_invalid_creadential_error,
                                        isLoading = false
                                    )
                                }
                            }

                            else->{
                                _state.update {
                                    it.copy(
                                        isError = true,
                                        errMessage = R.string.internal_server_error,
                                        isLoading = false
                                    )
                                }
                            }

                        }
                    }
                    ApiResult.Loading -> {
                        _state.update {
                            it.copy(isLoading = true)
                        }
                    }
                    is ApiResult.Success -> {
                        _state.update {
                            it.copy(
                                navigateToHome = true,
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }

}