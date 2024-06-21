package com.example.medscape20.presentation.screens.auth.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.medscape20.R
import com.example.medscape20.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding:FragmentLoginBinding?=null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = FragmentLoginBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding.sgnUpBtn.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupDetailsFragment)
        }
        return binding.root
    }

}