package com.example.medscape20.presentation.screens.auth.login

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
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.medscape20.R
import com.example.medscape20.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater)
        _binding!!.viewModel = viewModel
        _binding!!.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.sgnUpBtn.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

        binding.lgnBtn.setOnClickListener {
            viewModel.event(LoginEvents.OnLoginClicked)
        }

        binding.email.doOnTextChanged { text, _, _, _ ->
            viewModel.event(LoginEvents.OnEmailChanged(text.toString()))
        }

        binding.password.doOnTextChanged { text, _, _, _ ->
            viewModel.event(LoginEvents.OnPassChange(text.toString()))
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.state.collect { state ->

                    //email validation
                    if (!state.isEmailValid) {
                        binding.emailCont.error = getString(state.emailError!!)
                    } else {
                        binding.emailCont.error = null
                        binding.emailCont.isErrorEnabled = false

                    }

                    //password validation
                    if (!state.isPassValid) {
                        binding.passCont.error = getString(state.passError!!)
                    } else {
                        binding.passCont.error = null
                        binding.passCont.isErrorEnabled = false

                    }

                    //navigation
                    if (state.navigateToHome) {
                        navigateToHome()
                    }

                    //for showing error
                    if (state.isError) {
                        showError(state.errMessage!!)
                    }

                    //to show progress circular
                    if (state.isLoading) {
                        binding.lgnBtn.visibility = View.INVISIBLE
                        binding.progressCircular.visibility = View.VISIBLE
                    } else {
                        binding.lgnBtn.visibility = View.VISIBLE
                        binding.progressCircular.visibility = View.GONE
                    }

                }
            }
        }
    }

    private fun showError(errMessage: Int) {
        Snackbar.make(requireView(), getString(errMessage), Snackbar.LENGTH_SHORT).show()
        viewModel.event(LoginEvents.OnErrorShown)
    }

    private fun navigateToHome() {
        viewModel.event(LoginEvents.OnNavigationDone)
        findNavController().navigate(
            R.id.action_loginFragment_to_homeFragment,
            null,
            NavOptions.Builder().setPopUpTo(R.id.loginFragment, true).build()
        )
    }

//    override fun onStart() {
//        super.onStart()
//        // Check if user is signed in (non-null) and update UI accordingly.
//        val currentUser = auth.currentUser
//        if (currentUser != null) {
//
//            findNavController().navigate(
//                R.id.action_loginFragment_to_homeFragment,
//                null,
//                NavOptions.Builder().setPopUpTo(R.id.loginFragment, true).build()
//            )
//
//        }
//    }

}
//TODO : locality is not showing on firebase