package com.example.medscape20.presentation.screens.user.collector.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import com.example.medscape20.databinding.BottomSheetCustomerMapFilterBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CollectorMapFilterBottomSheet:BottomSheetDialogFragment() {

    lateinit var binding:BottomSheetCustomerMapFilterBinding
    var showAll:Boolean=false
    var radius:Double=0.5

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetCustomerMapFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //set previous filter values
        showAll=arguments?.getBoolean(CollectorMapsEnum.SHOW_ALL.name)?:false
        radius=arguments?.getDouble(CollectorMapsEnum.RADIUS.name)?:0.5
        binding.radiusSlider.isEnabled = !showAll

        setPreviousFilter()

        binding.radiusSlider.addOnChangeListener { slider, value, fromUser ->
           radius=value.toDouble()
        }

        binding.showAllSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            showAll=isChecked
            binding.radiusSlider.isEnabled = !showAll
        }


        binding.cancel.setOnClickListener {
            dismiss()
        }
        binding.apply.setOnClickListener {
            setFragmentResult(CollectorMapsEnum.REQUEST_KEY.name, Bundle().apply {
                putBoolean(CollectorMapsEnum.SHOW_ALL.name, showAll)
                putDouble(CollectorMapsEnum.RADIUS.name, radius)
            })
            dismiss()
        }
    }

    private fun setPreviousFilter() {
        binding.radiusSlider.values = listOf(radius.toFloat())
        binding.showAllSwitch.isChecked = showAll
    }

}