package com.example.medscape20.presentation.screens.user.customer.account.change_password

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.medscape20.R
import com.example.medscape20.databinding.FragmentAccountChangePasswordBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountChangePasswordFragment : Fragment() {

    private var _binding: FragmentAccountChangePasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AccountChangePasswordViewModel by viewModels()
    private lateinit var container: ViewGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountChangePasswordBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        this.container = container!!

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.password.doOnTextChanged { text, _, _, _ ->
            viewModel.event(AccountChangePasswordEvents.ValidatePassword(text.toString()))
        }

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.changePassBtn.setOnClickListener {
            viewModel.event(AccountChangePasswordEvents.OnChangePasswordClicked)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.states.collect { state ->

                    //password validation
                    if (!state.isPassValid) {
                        binding.passCont.error = getString(state.passError!!)
                        binding.changePassBtn.isEnabled = false
                    } else {
                        binding.passCont.error = null
                        binding.passCont.isErrorEnabled = false
                        binding.changePassBtn.isEnabled = true
                    }

                    //to show progress circular
                    if (state.isLoading) {
                        binding.changePassBtn.visibility = View.INVISIBLE
                        binding.progressCircular.visibility = View.VISIBLE
                    } else {
                        binding.changePassBtn.visibility = View.VISIBLE
                        binding.progressCircular.visibility = View.GONE
                    }

                    state.snackbarMessage?.let {
                        showMessage(it)
                    }

                }
            }
        }

    }

    private fun showMessage(errorMessage: Int?) {
        hideKeyboard()
        errorMessage?.let {
            Snackbar.make(container.rootView, getString(errorMessage), Snackbar.LENGTH_SHORT).show()
            viewModel.event(AccountChangePasswordEvents.ResetMessage)
        }
    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Activity.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}