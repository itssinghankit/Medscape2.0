package com.example.medscape20.presentation.screens.user.customer.account.update_details

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.medscape20.R
import com.example.medscape20.databinding.FragmentAccountUpdateDetailsBinding
import com.example.medscape20.presentation.screens.auth.maps.MapData
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.Serializable

@AndroidEntryPoint
class AccountUpdateDetailsFragment : Fragment() {

    private var _binding: FragmentAccountUpdateDetailsBinding? = null
    private val binding get() = _binding!!
    private var container: ViewGroup? = null
    private val viewModel: AccountUpdateDetailsViewModel by viewModels()
    private val args: AccountUpdateDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAccountUpdateDetailsBinding.inflate(layoutInflater, container, false)
        this.container = container

        container!!.rootView.findViewById<View>(R.id.bottotmAppBar).visibility = View.GONE
        container.rootView.findViewById<View>(R.id.trash_fab).visibility = View.GONE

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener(MapData.RESULT_KEY.value) { key, bundle ->
            val address = bundle.getString(MapData.ADDRESS.value)
            val lat = bundle.getDouble(MapData.LATITUDE.value)
            val lng = bundle.getDouble(MapData.LONGITUDE.value)
            val state = bundle.getString(MapData.STATE.value)
            val city = bundle.getString(MapData.CITY.value)
            val locality = bundle.getString(MapData.LATITUDE.value)
            address?.let {
                if (address.isNotEmpty()) {
                    viewModel.event(
                        AccountUpdateDetailsEvents.OnAddressChanged(
                            address,
                            lat,
                            lng,
                            state,
                            city,
                            locality
                        )
                    )
                }
            }
        }

        //saving data to view model from account screen
        viewModel.event(AccountUpdateDetailsEvents.SaveDetails(args.details))

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        //validation
        binding.name.doOnTextChanged { text, _, _, _ ->
            viewModel.event(AccountUpdateDetailsEvents.ValidateName(text.toString()))
        }
        binding.mobile.doOnTextChanged { text, _, _, _ ->
            viewModel.event(AccountUpdateDetailsEvents.ValidateMobile(text.toString()))
        }

        binding.addLocBtn.setOnClickListener {
            navigateToMapFragment()
        }

        binding.updateBtn.setOnClickListener {
            viewModel.event(AccountUpdateDetailsEvents.OnUpdateDetailsClicked)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.state.collect { state ->

                    //name validation
                    if (!state.isNameValid) {
                        binding.nameCont.error = getString(state.nameError!!)
                    } else {
                        binding.nameCont.error = null
                        binding.nameCont.isErrorEnabled = false
                    }

                    //mobile validation
                    if (!state.isMobileValid) {
                        binding.mobileCont.error = getString(state.mobileError!!)
                    } else {
                        binding.mobileCont.error = null
                        binding.mobileCont.isErrorEnabled = false
                    }

                    //for showing error
                    state.snackBarMessage?.let {
                        showMessage(it)
                    }

                    //to show progress circular
                    if (state.isLoading) {
                        binding.updateBtn.visibility = View.INVISIBLE
                        binding.progressCircular.visibility = View.VISIBLE
                    } else {
                        binding.updateBtn.visibility = View.VISIBLE
                        binding.progressCircular.visibility = View.GONE
                    }

                    if (state.detailsUpdatedSuccessfully) {
                        navigateBackToAccountScreen()
                    }

                }

            }
        }


    }

    private fun navigateToMapFragment() {
        findNavController().navigate(R.id.action_accountUpdateDetailsFragment_to_mapsFragment)
    }

    private fun navigateBackToAccountScreen() {
        setFragmentResult(
            UpdateAccountDetailsEnum.ACCOUNT_DETAILS_UPDATED_REQUEST_KEY.name,
            Bundle().apply {
                putSerializable(
                    UpdateAccountDetailsEnum.ACCOUNT_DETAILS_UPDATED_ARGUMENT_KEY.name,
                    AccountUpdatedDetails(
                        name = viewModel.name.value,
                        mobile = viewModel.mobile.value,
                        address = viewModel.state.value.state!!,
                        lat = viewModel.state.value.lat!!,
                        lng = viewModel.state.value.lng!!,
                        state = viewModel.state.value.state!!,
                        city = viewModel.state.value.city!!
                    )
                )
            })
        findNavController().navigateUp()
    }

    private fun showMessage(errorMessage: Int?) {
        hideKeyboard()
        errorMessage?.let {
            Snackbar.make(container!!.rootView, getString(errorMessage), Snackbar.LENGTH_SHORT)
                .show()
            viewModel.event(AccountUpdateDetailsEvents.ResetSnackbarMessage)
        }
    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Activity.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}

data class AccountUpdatedDetails(
    val name: String,
    val mobile: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val state: String,
    val city: String
) : Serializable

enum class UpdateAccountDetailsEnum {
    ACCOUNT_DETAILS_UPDATED_REQUEST_KEY,
    ACCOUNT_DETAILS_UPDATED_ARGUMENT_KEY
}
//TODO: landing to home instead of account page