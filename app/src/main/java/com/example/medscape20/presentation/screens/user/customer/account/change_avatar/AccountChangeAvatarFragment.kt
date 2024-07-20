package com.example.medscape20.presentation.screens.user.customer.account.change_avatar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.medscape20.R
import com.example.medscape20.databinding.FragmentAccountChangeAvatarBinding


class AccountChangeAvatarFragment : Fragment() {

    private var _binding:FragmentAccountChangeAvatarBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding= FragmentAccountChangeAvatarBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}