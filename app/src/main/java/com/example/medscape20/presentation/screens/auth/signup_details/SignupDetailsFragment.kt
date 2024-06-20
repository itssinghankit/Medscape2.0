package com.example.medscape20.presentation.screens.auth.signup_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.medscape20.databinding.FragmentSignupDetailsBinding
import com.example.medscape20.presentation.screens.auth.signup.SignupFragmentDirections

class SignupDetailsFragment() : Fragment() {

    private var _binding: FragmentSignupDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignupDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.nxtBtn.setOnClickListener {

            findNavController().popBackStack()
        }

        val action= SignupFragmentDirections.actionSignupFragmentToSignupDetailsFragment()
    }
}

