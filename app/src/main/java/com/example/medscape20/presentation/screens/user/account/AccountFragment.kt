package com.example.medscape20.presentation.screens.user.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.medscape20.R
import com.example.medscape20.databinding.FragmentAccountBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountFragment : Fragment() {

    private var _binding:FragmentAccountBinding?=null
    private val binding get() = _binding!!

    private val viewModel:AccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding=FragmentAccountBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signoutBtn.setOnClickListener {
            viewModel.event(AccountEvents.OnSignOutClicked)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.state.collect{state->
                    if(state.navigateToAuth){
                        navigateToAuth()
                    }
                }
            }
        }
    }

    private fun navigateToAuth() {
        findNavController().navigate(R.id.action_accountFragment_to_loginFragment,null,NavOptions.Builder().setPopUpTo(R.id.accountFragment,true).build())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

}