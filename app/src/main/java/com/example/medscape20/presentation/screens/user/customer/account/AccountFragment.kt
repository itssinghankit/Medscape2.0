package com.example.medscape20.presentation.screens.user.customer.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.medscape20.R
import com.example.medscape20.databinding.FragmentAccountBinding
import com.example.medscape20.presentation.screens.auth.avatar.AvatarEvents
import com.example.medscape20.presentation.screens.user.customer.account.change_avatar.UpdateAvatarEnum
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAccountBinding.inflate(layoutInflater, container, false)
        binding.viewModel=viewModel
        binding.lifecycleOwner=viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener(UpdateAvatarEnum.UPDATE_AVATAR_REQUEST_KEY.name){_,bundle->
            val avatar = bundle.getString(UpdateAvatarEnum.UPDATE_AVATAR_AVATAR_URL.name)
            avatar?.let{
                viewModel.event(AccountEvents.OnAvatarUpdation(it))
            }
        }

        binding.signoutBtn.setOnClickListener {
            viewModel.event(AccountEvents.OnSignOutClicked)
        }

        binding.accountDetails.setOnClickListener{
            navigateToShowDetailsFragment()
        }

        binding.changeAvatar.setOnClickListener{
            navigateToChangeAvatarFragment()
        }

        binding.changePass.setOnClickListener{
            navigateToChangePasswordFragment()
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    if (state.navigateToAuth) {
                        navigateToAuth()
                    }
                    when (state.isLoading) {
                        true -> {
                            binding.mainView.visibility = View.GONE
                            binding.progressCircular.visibility = View.VISIBLE
                        }

                        false -> {
                            binding.mainView.visibility = View.VISIBLE
                            binding.progressCircular.visibility = View.GONE
                        }
                    }

                    state.avatar?.let {
                        Glide.with(requireContext()).load(it).into(binding.avatar)
                    }

                    state.errMessage?.let {
                        showError(it)
                    }
                }
            }
        }
    }

    private fun navigateToChangePasswordFragment() {
        findNavController().navigate(R.id.action_accountFragment_to_accountChangePasswordFragment)
    }

    private fun navigateToChangeAvatarFragment() {
        viewModel.state.value.userDetails?.let {
            val action=AccountFragmentDirections.actionAccountFragmentToAccountChangeAvatarFragment(it.avatar)
            findNavController().navigate(action)
        }
    }

    private fun navigateToShowDetailsFragment() {
        viewModel.state.value.userDetails?.let {
            val action=AccountFragmentDirections.actionAccountFragmentToAccountDetailsFragment(it)
            findNavController().navigate(action)
        }
    }

    private fun navigateToAuth() {
        findNavController().navigate(
            R.id.action_accountFragment_to_loginFragment,
            null,
            NavOptions.Builder().setPopUpTo(R.id.accountFragment, true).build()
        )
    }

    private fun showError(errorMessage: Int?) {

        errorMessage?.let {
            Snackbar.make(requireView(), getString(errorMessage), Snackbar.LENGTH_SHORT).show()
            viewModel.event(AccountEvents.ResetErrorMessage)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}