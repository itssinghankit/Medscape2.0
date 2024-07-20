package com.example.medscape20.presentation.screens.user.customer.account.change_password

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medscape20.R
import com.example.medscape20.domain.usecase.common.validation.ValidatePasswordUseCase
import com.example.medscape20.domain.usecase.user.customer.account.change_password.AccountUpdatePasswordUseCase
import com.example.medscape20.util.ApiResult
import com.example.medscape20.util.DataError
import com.example.medscape20.util.PassError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


data class AccountChangePasswordStates(
    val isLoading: Boolean = false,
    @StringRes val snackbarMessage: Int? = null,
    val isPassValid: Boolean = true,
    @StringRes val passError: Int? = null,
    val updatedSucces:Boolean=false
)
@HiltViewModel
class AccountChangePasswordViewModel @Inject constructor(
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val accountUpdatePasswordUseCase: AccountUpdatePasswordUseCase
): ViewModel() {

    private val _states= MutableStateFlow(AccountChangePasswordStates())
    val states:StateFlow<AccountChangePasswordStates> = _states.asStateFlow()

    val password = MutableStateFlow("")

    fun event(action:AccountChangePasswordEvents){
        when(action){
            AccountChangePasswordEvents.ResetMessage -> {
                _states.update { it.copy(snackbarMessage = null) }
            }
            is AccountChangePasswordEvents.ValidatePassword -> {

                when (val result = validatePasswordUseCase(action.password)) {
                    is ApiResult.Error -> {
                        when (result.error) {
                            PassError.EMPTY -> {
                                _states.update {
                                    it.copy(
                                        isPassValid = false,
                                        passError = R.string.error_empty
                                    )
                                }
                            }

                            PassError.PASS_ERROR -> {
                                _states.update {
                                    it.copy(
                                        isPassValid = false,
                                        passError = R.string.error_pass
                                    )
                                }
                            }
                        }
                    }

                    else -> {
                        _states.update {
                            it.copy(isPassValid = true, passError = null)
                        }
                    }
                }
            }

            AccountChangePasswordEvents.OnChangePasswordClicked -> {
                updatePassword()
            }
        }
    }

    private fun updatePassword() {
        viewModelScope.launch(Dispatchers.IO) {

            accountUpdatePasswordUseCase(password.value).collect{result->
                when (result) {
                    is ApiResult.Error -> {
                        when (result.error) {

                            DataError.Network.INTERNAL_SERVER_ERROR -> {
                                _states.update {
                                    withContext(Dispatchers.Main) {
                                        it.copy(
                                            snackbarMessage = R.string.error_internal_server,
                                            isLoading = false
                                        )
                                    }

                                }
                            }

                            else -> {
                                _states.update {
                                    withContext(Dispatchers.Main) {
                                        it.copy(
                                            snackbarMessage = R.string.error_unknown,
                                            isLoading = false
                                        )
                                    }

                                }

                            }
                        }
                    }

                    ApiResult.Loading -> {
                        _states.update {
                            withContext(Dispatchers.Main) {
                                it.copy(
                                    isLoading = true
                                )
                            }
                        }
                    }

                    is ApiResult.Success -> {
                        _states.update {
                            withContext(Dispatchers.Main) {
                                it.copy(
                                    isLoading = false,
                                    snackbarMessage = R.string.password_updated_successfully
                                )
                            }

                        }
                    }
                }
            }

        }
    }


}