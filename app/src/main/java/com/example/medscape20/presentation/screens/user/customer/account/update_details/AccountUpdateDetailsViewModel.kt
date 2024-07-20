package com.example.medscape20.presentation.screens.user.customer.account.update_details

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medscape20.R
import com.example.medscape20.domain.usecase.common.validation.MobileValidationUseCase
import com.example.medscape20.domain.usecase.common.validation.NameValidationUseCase
import com.example.medscape20.domain.usecase.common.validation.ValidateEmailUseCase
import com.example.medscape20.domain.usecase.user.customer.account.update_details.AccountUpdateDetailsUseCase
import com.example.medscape20.util.ApiResult
import com.example.medscape20.util.DataError
import com.example.medscape20.util.EmailError
import com.example.medscape20.util.MobileError
import com.example.medscape20.util.NameError
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

data class AccountUpdateDetailsStates(
    val isLoading: Boolean = false,
    @StringRes val snackBarMessage: Int? = null,
    @StringRes val mobileError: Int? = null,
    val isMobileValid: Boolean = true,
    @StringRes val nameError: Int? = null,
    val isNameValid: Boolean = true,
    @StringRes val emailError: Int? = null,
    val isEmailValid: Boolean = true,
    val city: String? = null,
    val state: String? = null,
    val address: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val detailsUpdatedSuccessfully: Boolean = false
)

@HiltViewModel
class AccountUpdateDetailsViewModel @Inject constructor(
    private val validateNameValidationUseCase: NameValidationUseCase,
    private val validateEmailValidationUseCase: ValidateEmailUseCase,
    private val validateMobileValidationUseCase: MobileValidationUseCase,
    private val accountUpdateDetailsUseCase: AccountUpdateDetailsUseCase,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(AccountUpdateDetailsStates())
    val state: StateFlow<AccountUpdateDetailsStates> = _state.asStateFlow()

    val name = MutableStateFlow("")
    val email = MutableStateFlow("")
    val mobile = MutableStateFlow("")

    var flag = true
    fun event(action: AccountUpdateDetailsEvents) {

        when (action) {
            AccountUpdateDetailsEvents.ResetSnackbarMessage -> {
                _state.value = _state.value.copy(snackBarMessage = null)
            }

            is AccountUpdateDetailsEvents.ValidateEmail -> {
                when (val result = validateEmailValidationUseCase(action.email)) {
                    is ApiResult.Error -> {
                        when (result.error) {
                            EmailError.EMPTY -> {
                                _state.update {
                                    it.copy(
                                        isEmailValid = false,
                                        emailError = R.string.error_empty
                                    )
                                }
                            }

                            EmailError.EMAIL_ERROR -> {
                                _state.update {
                                    it.copy(
                                        isEmailValid = false,
                                        emailError = R.string.error_email
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

            is AccountUpdateDetailsEvents.ValidateMobile -> {
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

            is AccountUpdateDetailsEvents.ValidateName -> {
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

            is AccountUpdateDetailsEvents.SaveDetails -> {

                //to save it only once
                if (flag) {
                    _state.update {
                        it.copy(
                            state = action.data.state ?: "",
                            city = action.data.city ?: "",
                            address = action.data.address ?: "",
                            lat = action.data.lat,
                            lng = action.data.lng
                        )
                    }
                    name.value = action.data.name ?: ""
                    email.value = action.data.email ?: ""
                    mobile.value = action.data.mobile ?: ""

                    flag = false
                }
            }

            is AccountUpdateDetailsEvents.OnAddressChanged -> {
                _state.update {
                    it.copy(
                        state = action.state ?: "",
                        city = action.city ?: "",
                        address = action.address,
                        lat = action.lat,
                        lng = action.lng,
                    )
                }
            }

            AccountUpdateDetailsEvents.OnUpdateDetailsClicked -> {

                if (email.value.isNotEmpty() && name.value.isNotEmpty() && mobile.value.isNotEmpty() && state.value.isEmailValid && state.value.isNameValid && state.value.isMobileValid) {

                    val updates = hashMapOf<String,Any>(
                        "state" to state.value.state!!,
                        "city" to state.value.city!!,
                        "address" to state.value.address!!,
                        "lat" to state.value.lat!!,
                        "lng" to state.value.lng!!,
                        "name" to name.value,
                        "email" to email.value,
                        "mobile" to mobile.value
                    )
Timber.d(updates.toString())
                    updateDetail(updates, firebaseAuth.currentUser!!.uid)

                }


            }
        }
    }

    private fun updateDetail(updates: HashMap<String, Any>, uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            accountUpdateDetailsUseCase(uid, updates).collect { result ->
                when (result) {
                    is ApiResult.Error -> {
                        when (result.error) {
                            DataError.Network.INTERNAL_SERVER_ERROR -> {
                                _state.update {
                                    withContext(Dispatchers.Main) {
                                        it.copy(
                                            isLoading = false,
                                            snackBarMessage = R.string.error_internal_server
                                        )
                                    }
                                }
                            }

                            else -> {
                                _state.update {
                                    withContext(Dispatchers.Main) {
                                        it.copy(
                                            isLoading = false,
                                            snackBarMessage = R.string.error_unknown
                                        )
                                    }
                                }
                            }
                        }
                    }

                    ApiResult.Loading -> {
                        _state.update {
                            withContext(Dispatchers.Main) {
                                it.copy(isLoading = true)
                            }
                        }
                    }

                    is ApiResult.Success -> {
                        _state.update {
                            withContext(Dispatchers.Main) {
                                it.copy(
                                    isLoading = false,
                                    snackBarMessage = R.string.details_updated_successfully,
                                    detailsUpdatedSuccessfully = true
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}