package com.example.medscape20.presentation.screens.auth.avatar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.medscape20.R
import com.example.medscape20.databinding.FragmentAvatarBinding


class AvatarFragment : Fragment() {

    private var _binding: FragmentAvatarBinding? = null
    private val binding get()=_binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding=FragmentAvatarBinding.inflate(layoutInflater)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding.back.setOnClickListener{
            findNavController().popBackStack()
        }
        binding.signupBtn.setOnClickListener{
            findNavController().navigate(R.id.homeActivity)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

}