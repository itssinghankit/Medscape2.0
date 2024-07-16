package com.example.medscape20.presentation.screens.user.trash

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medscape20.R
import com.example.medscape20.domain.usecase.user.trash.TrashIsDumpedUseCase
import com.example.medscape20.domain.usecase.user.trash.TrashSetDumpUseCase
import com.example.medscape20.util.ApiResult
import com.example.medscape20.util.DataError
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

data class TrashStates(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    @StringRes val errMessage: Int? = null,
    val showDumpPage: Boolean = true,
    val metal: Boolean = false,
    val general: Boolean = false,
    val medical: Boolean = false,
    val plastic: Boolean = false
)

@HiltViewModel
class TrashViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val trashSetDumpUseCase: TrashSetDumpUseCase,
    private val trashIsDumpedUseCase: TrashIsDumpedUseCase
) : ViewModel() {

    init {
        firebaseAuth.addAuthStateListener { auth->
            if(auth.currentUser != null){
                idDumped()
            }
        }
    }

    private val _state = MutableStateFlow(TrashStates())
    val states: StateFlow<TrashStates> = _state.asStateFlow()

    fun event(action: TrashEvents) {
        when (action) {
            TrashEvents.ResetErrorMessage -> {
                _state.update {
                    it.copy(isError = false, errMessage = null)
                }
            }

            is TrashEvents.OnTrashTypesSet -> {
                val updates = hashMapOf<String, Any>(
                    "dump" to true,
                    "metal" to action.trashTypeList.contains(TrashType.METAL.value),
                    "general" to action.trashTypeList.contains(TrashType.GENERAL.value),
                    "medical" to action.trashTypeList.contains(TrashType.MEDICAL.value),
                    "plastic" to action.trashTypeList.contains(TrashType.PLASTIC.value)
                )
                setTrashDump(updates)
            }
        }
    }

    private fun setTrashDump(updates: HashMap<String, Any>) {

        viewModelScope.launch(Dispatchers.IO) {
            val uid = firebaseAuth.currentUser?.uid!!
            trashSetDumpUseCase(uid, updates).collect { result ->
                when (result) {
                    is ApiResult.Error -> {
                        when (result.error) {

                            DataError.Network.INTERNAL_SERVER_ERROR -> {
                                _state.update {
                                    withContext(Dispatchers.Main) {
                                        it.copy(
                                            isError = true,
                                            isLoading = false,
                                            errMessage = R.string.error_internal_server
                                        )
                                    }
                                }
                            }

                            else -> {
                                _state.update {
                                    withContext(Dispatchers.Main) {
                                        it.copy(
                                            isError = true,
                                            isLoading = false,
                                            errMessage = R.string.error_unknown
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
                        idDumped()
                    }
                }
            }
        }

    }

    private fun idDumped() {
        viewModelScope.launch(Dispatchers.IO) {

            val uid = firebaseAuth.currentUser?.uid!!
            trashIsDumpedUseCase(uid).collect { result ->

                when (result) {
                    is ApiResult.Error -> {
                        when (result.error) {

                            DataError.Network.INTERNAL_SERVER_ERROR -> {
                                _state.update {
                                    withContext(Dispatchers.Main) {
                                        it.copy(
                                            isError = true,
                                            isLoading = false,
                                            errMessage = R.string.error_internal_server
                                        )
                                    }
                                }
                            }

                            else -> {
                                _state.update {
                                    withContext(Dispatchers.Main) {
                                        it.copy(
                                            isError = true,
                                            isLoading = false,
                                            errMessage = R.string.error_unknown
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
                                    showDumpPage = !result.data.dump,
                                    isLoading = false,
                                    metal = result.data.metal,
                                    general = result.data.general,
                                    medical = result.data.medical,
                                    plastic = result.data.plastic
                                )
                            }
                        }

                    }
                }
            }
        }
    }

}