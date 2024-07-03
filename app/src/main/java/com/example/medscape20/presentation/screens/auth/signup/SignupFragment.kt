package com.example.medscape20.presentation.screens.auth.signup


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.medscape20.databinding.FragmentSignupBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val viewModel: SignupViewModel by viewModels()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignupBinding.inflate(layoutInflater)
        _binding!!.lifecycleOwner = this
        _binding!!.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //sending email and password to next page
        binding.nxtBtn.setOnClickListener {
            viewModel.event(SignupEvents.OnNextClick)
        }

        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.email.doOnTextChanged { text, start, before, count ->
            viewModel.event(SignupEvents.OnEmailChanged(text.toString()))
        }

        binding.password.doOnTextChanged { text, start, before, count ->
            viewModel.event(SignupEvents.OnPasswordChanged(text.toString()))
        }

        //observing events for actions
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect {

                    if (it.navigateToNextScreen) {
                        navigateToSignupDetailsScreen()
                    }

                    //email validation
                    if (!it.isEmailValid) {
                        binding.emailCont.error = getString(it.emailError!!)
                    } else {
                        binding.emailCont.error = null
                        binding.emailCont.isErrorEnabled = false
                    }

                    //password validation
                    if (!it.isPasswordValid) {
                        binding.passCont.error = getString(it.passError!!)
                    } else {
                        binding.passCont.error = null
                        binding.passCont.isErrorEnabled = false
                    }

                }
            }
        }

    }

    fun navigateToSignupDetailsScreen() {
        //change navigateToNextScreen variable false for back stack
        viewModel.event(SignupEvents.OnNavigationDone)

        val action = SignupFragmentDirections.actionSignupFragmentToSignupDetailsFragment(
            email = binding.email.text.toString().lowercase().trim(),
            password = binding.password.text.toString().trim()
        )
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
