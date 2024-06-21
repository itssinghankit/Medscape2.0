package com.example.medscape20.presentation.screens.auth.signup_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.medscape20.R
import com.example.medscape20.databinding.FragmentSignupDetailsBinding
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
//
//        Toast.makeText(context,args.email, Toast.LENGTH_SHORT).show()
//        Toast.makeText(context,args.password, Toast.LENGTH_SHORT).show()

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

        //gender radio button
        binding.gender.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                Gender.MALE.id -> viewModel.event(SignupDetailsEvents.OnGenderChanged(Gender.MALE.value))
                Gender.FEMALE.id -> viewModel.event(SignupDetailsEvents.OnGenderChanged(Gender.FEMALE.value))
                Gender.OTHER.id -> viewModel.event(SignupDetailsEvents.OnGenderChanged(Gender.OTHER.value))
            }
        }

//        binding.gender.check(R.id.male)

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

                }
            }

        }


    }

    fun navigateToAvatarScreen() {
        viewModel.event(SignupDetailsEvents.OnNavigationDone)

        val action = SignupDetailsFragmentDirections.actionSignupDetailsFragmentToAvatarFragment(
            email = args.email,
            password = args.password,
            name = viewModel.name.value,
            mobile = viewModel.mobile.value,
            gender = viewModel.state.value.gender,
            address = viewModel.address.value
        )
        findNavController().navigate(action)
    }

    private enum class Gender(val value: String, @IdRes val id: Int) {
        MALE("male", R.id.male),
        FEMALE("female", R.id.female),
        OTHER("other", R.id.other)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

