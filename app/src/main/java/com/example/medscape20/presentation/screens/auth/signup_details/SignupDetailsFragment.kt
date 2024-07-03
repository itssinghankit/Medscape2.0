package com.example.medscape20.presentation.screens.auth.signup_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.medscape20.R
import com.example.medscape20.databinding.FragmentSignupDetailsBinding
import com.example.medscape20.presentation.screens.auth.maps.MapData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignupDetailsFragment() : Fragment() {

    private var _binding: FragmentSignupDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SignupDetailsViewmodel by viewModels()
    private val args: SignupDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignupDetailsBinding.inflate(layoutInflater)
        _binding!!.viewModel = viewModel
        _binding!!.lifecycleOwner = viewLifecycleOwner

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
                    //remove hint from textField
                    binding.addCont.hint = null
                    viewModel.event(
                        SignupDetailsEvents.OnAddressChanged(
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

        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.nxtBtn.setOnClickListener {
            viewModel.event(SignupDetailsEvents.OnNextClick)
        }

        binding.name.doOnTextChanged { text, start, before, count ->
            viewModel.event(SignupDetailsEvents.OnNameChanged(text.toString()))
        }

        binding.mobile.doOnTextChanged { text, start, before, count ->
            viewModel.event(SignupDetailsEvents.OnMobileChanged(text.toString()))
        }

        //for moving to map screen
        binding.addLocBtn.setOnClickListener {
            viewModel.event(SignupDetailsEvents.OnLocBtnClicked)
        }

        //gender radio button
        binding.gender.check(Gender.MALE.id) //default
        binding.gender.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                Gender.MALE.id -> viewModel.event(SignupDetailsEvents.OnGenderChanged(Gender.MALE.value))
                Gender.FEMALE.id -> viewModel.event(SignupDetailsEvents.OnGenderChanged(Gender.FEMALE.value))
                Gender.OTHER.id -> viewModel.event(SignupDetailsEvents.OnGenderChanged(Gender.OTHER.value))
            }
        }

        //observing events for actions
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect {
                    if (it.navigateToNextScreen) {
                        navigateToAvatarScreen()
                    }

                    //mobile validation
                    if (!it.isMobileValid) {
                        binding.mobileCont.error = getString(it.mobileError!!)

                    } else {
                        binding.mobileCont.error = null
                        binding.mobileCont.isErrorEnabled = false
                    }

                    //name validation
                    if (!it.isNameValid) {
                        binding.nameCont.error = getString(it.nameError!!)

                    } else {
                        binding.nameCont.error = null
                        binding.nameCont.isErrorEnabled = false
                    }

                    //address validation
                    if (!it.isAddressValid) {
                        binding.addCont.error = getString(it.addressError!!)

                    } else {
                        binding.addCont.error = null
                        binding.addCont.isErrorEnabled = false
                    }

                    if (it.navigateToMapFragment) {
                        navigateToMapFragment()
                    }

                }
            }

        }


    }

    private fun navigateToAvatarScreen() {
        viewModel.event(SignupDetailsEvents.OnNavigationDone)
        val state = viewModel.state.value
        val action = SignupDetailsFragmentDirections.actionSignupDetailsFragmentToAvatarFragment(
            email = args.email,
            password = args.password,
            name = viewModel.name.value.trim(),
            mobile = viewModel.mobile.value.trim(),
            gender = viewModel.state.value.gender,
            address = viewModel.address.value,
            lat = state.lat.toString(),
            lng = state.lng.toString(),
            city = state.city,
            state = state.state,
            locality = state.locality
        )
        findNavController().navigate(action)
    }

    private fun navigateToMapFragment() {
        viewModel.event(SignupDetailsEvents.OnNavigationDone)
        findNavController().navigate(R.id.action_signupDetailsFragment_to_mapsFragment)
    }

    override fun onResume() {
        super.onResume()
        if (!binding.address.text.isNullOrEmpty()) {
            binding.addCont.hint = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private enum class Gender(val value: String, @IdRes val id: Int) {
        MALE("male", R.id.male),
        FEMALE("female", R.id.female),
        OTHER("other", R.id.other)
    }
}

