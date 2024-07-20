package com.example.medscape20.presentation.screens.auth.signup_details

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.example.medscape20.R
import com.example.medscape20.domain.usecase.common.validation.MobileValidationUseCase
import com.example.medscape20.domain.usecase.common.validation.NameValidationUseCase
import com.example.medscape20.util.ApiResult
import com.example.medscape20.util.MobileError
import com.example.medscape20.util.NameError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class SignupDetailsState(
    val navigateToNextScreen: Boolean = false,
    val isNameValid: Boolean = true,
    @StringRes val nameError: Int? = null,
    val isMobileValid: Boolean = true,
    @StringRes val mobileError: Int? = null,
    val isAddressValid: Boolean = true,
    @StringRes val addressError: Int? = null,
    val gender: String = "male",
    val navigateToMapFragment: Boolean = false,
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val state: String? = null,
    val city: String? = null,
    val locality: String? = null
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
            SignupDetailsEvents.OnLocBtnClicked -> {
                _state.update {
                    it.copy(navigateToMapFragment = true)
                }
            }

            is SignupDetailsEvents.OnAddressChanged -> {
                address.value = action.address
                _state.update {
                    it.copy(
                        lat = action.lat,
                        lng = action.lng,
                        state = action.state,
                        city = action.city,
                        locality = action.locality
                    )
                }
            }

            is SignupDetailsEvents.OnGenderChanged -> {
                _state.update {
                    it.copy(gender = action.gender)
                }
            }

            is SignupDetailsEvents.OnMobileChanged -> {
                when (val result = validateMobileValidationUseCase(action.mobile)) {
                    is ApiResult.Error -> {
                        when (result.error) {
                            MobileError.EMPTY -> {
                                _state.update {
                                    it.copy(
                                        mobileError = R.string.error_empty,
                                        isMobileValid = false
                                    )
                                }
                            }

                            MobileError.TOO_SHORT -> {
                                _state.update {
                                    it.copy(
                                        mobileError = R.string.error_mobile_short,
                                        isMobileValid = false
                                    )
                                }
                            }

                            MobileError.INVALID -> {
                                _state.update {
                                    it.copy(
                                        mobileError = R.string.error_mobile,
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
                    is ApiResult.Error -> {

                        when (result.error) {
                            NameError.EMPTY -> {
                                _state.update {
                                    it.copy(
                                        nameError = R.string.error_empty,
                                        isNameValid = false
                                    )
                                }
                            }

                            NameError.TOO_SHORT -> {
                                _state.update {
                                    it.copy(
                                        nameError = R.string.error_name_short,
                                        isNameValid = false
                                    )
                                }
                            }

                            NameError.ONLY_ALPHABETS -> {
                                _state.update {
                                    it.copy(
                                        nameError = R.string.error_name,
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

            SignupDetailsEvents.OnNavigationDone -> {
                _state.update {
                    it.copy(navigateToNextScreen = false, navigateToMapFragment = false)
                }
            }

            SignupDetailsEvents.OnNextClick -> {
                if (address.value.isEmpty()) {
                    _state.update {
                        it.copy(isAddressValid = false, addressError = R.string.error_empty)
                    }
                }
                if (_state.value.isNameValid && _state.value.isMobileValid && _state.value.isAddressValid && mobile.value.isNotEmpty() && name.value.isNotEmpty() && address.value.isNotEmpty()) {
                    _state.update {
                        it.copy(navigateToNextScreen = true)
                    }
                }
            }
        }
    }

}