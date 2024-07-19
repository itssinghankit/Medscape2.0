package com.example.medscape20.presentation.screens.user.collector.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.medscape20.databinding.BottomSheetCustomerMapFilterBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CollectorMapFilterBottomSheet:BottomSheetDialogFragment() {

    lateinit var binding:BottomSheetCustomerMapFilterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetCustomerMapFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

}