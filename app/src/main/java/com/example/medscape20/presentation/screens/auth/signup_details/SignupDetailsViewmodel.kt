package com.example.medscape20.presentation.screens.auth.signup_details

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medscape20.R
import com.example.medscape20.domain.usecase.signup_details.MobileValidationUseCase
import com.example.medscape20.domain.usecase.signup_details.NameValidationUseCase
import com.example.medscape20.util.MobileError
import com.example.medscape20.util.NameError
import com.example.medscape20.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

data class SignupDetailsState(
    val navigateToNextScreen: Boolean = false,
    val isNameValid: Boolean = true,
    val nameError: Int? = null,
    val isMobileValid: Boolean = true,
    val mobileError: Int? = null,
    val isAddressValid: Boolean = true,
    val addressError: Int? = null,
    val gender: String = "male"
)

@HiltViewModel
class SignupDetailsViewmodel @Inject constructor(
    private val validateNameValidationUseCase: NameValidationUseCase,
    private val validateMobileValidationUseCase: MobileValidationUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SignupDetailsState())
    val state: StateFlow<SignupDetailsState> = _state.asStateFlow()

    val name = MutableStateFlow("")
    val mobile = MutableStateFlow("")
    val address = MutableStateFlow("")

    fun event(action: SignupDetailsEvents) {
        when (action) {
            is SignupDetailsEvents.OnAddressChanged -> TODO()

            is SignupDetailsEvents.OnGenderChanged -> {
                _state.update {
                    it.copy(gender = action.gender)
                }
            }

            SignupDetailsEvents.OnMapBtnClicked -> TODO()
            is SignupDetailsEvents.OnMobileChanged -> {
                when (val result = validateMobileValidationUseCase(action.mobile)) {
                    is Result.Error -> {
                        when (result.error) {
                            MobileError.EMPTY -> {
                                _state.update {
                                    it.copy(
                                        mobileError = R.string.empty_error,
                                        isMobileValid = false
                                    )
                                }
                            }

                            MobileError.TOO_SHORT -> {
                                _state.update {
                                    it.copy(
                                        mobileError = R.string.mobile_short_error,
                                        isMobileValid = false
                                    )
                                }
                            }

                            MobileError.INVALID -> {
                                _state.update {
                                    it.copy(
                                        mobileError = R.string.mobile_error,
                                        isMobileValid = false
                                    )
                                }
                            }
                        }
                    }

                    else -> {
                        _state.update {
                            it.copy(mobileError = null, isMobileValid = true)
                        }
                    }
                }
            }

            is SignupDetailsEvents.OnNameChanged -> {
                when (val result = validateNameValidationUseCase(action.name)) {
                    is Result.Error -> {

                        when (result.error) {
                            NameError.EMPTY -> {
                                _state.update {
                                    it.copy(
                                        nameError = R.string.empty_error,
                                        isNameValid = false
                                    )
                                }
                            }

                            NameError.TOO_SHORT -> {
                                _state.update {
                                    it.copy(
                                        nameError = R.string.name_short_error,
                                        isNameValid = false
                                    )
                                }
                            }

                            NameError.ONLY_ALPHABETS -> {
                                _state.update {
                                    it.copy(
                                        nameError = R.string.name_error,
                                        isNameValid = false
                                    )
                                }
                            }
                        }
                    }

                    else -> {
                        _state.update {
                            it.copy(nameError = null, isNameValid = true)
                        }

                    }
                }
            }

            SignupDetailsEvents.OnNavigationDone -> TODO()
            SignupDetailsEvents.OnNextClick -> TODO()
        }
    }

}