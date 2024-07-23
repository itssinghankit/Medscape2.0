package com.example.medscape20.presentation.screens.auth.login

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.medscape20.R
import com.example.medscape20.databinding.FragmentLoginBinding
import com.example.medscape20.util.UserPreferences
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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
            hideKeyboard()
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

        binding.lgnBtn.setOnClickListener {
            hideKeyboard()
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
                    if (state.navigateToUserScreen) {
                        navigateToUserScreen()
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

    private fun navigateToUserScreen() {
        viewModel.event(LoginEvents.OnNavigationDone)
        lifecycleScope.launch {

            if (binding.collectorCheckBox.isChecked) {
                setUserData(true)
                findNavController().navigate(R.id.action_loginFragment_to_collectorHomeFragment)
            } else {
                setUserData(false)
                findNavController().navigate(
                    R.id.action_loginFragment_to_userFragment,
                    null,
                    NavOptions.Builder().setPopUpTo(R.id.loginFragment, true).build()
                )
            }
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

    private suspend fun setUserData(isCollector: Boolean) {
        UserPreferences.getDataStore(requireContext()).edit { datastore ->
            datastore[IS_COLLECTOR] = isCollector
        }
    }

    override fun onStart() {
        super.onStart()

    }

    companion object {
        val IS_COLLECTOR = booleanPreferencesKey("is_collector")
    }

}
//TODO : locality is not showing on firebase
//TODO: change dispatchers for viewmodelscope
//TODO: save data in avatar screen
//TODO: datastore
//TODO: material alert dialog
//TODO: app:behavior_peekHeight="1000dp"