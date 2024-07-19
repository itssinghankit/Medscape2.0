package com.example.medscape20.presentation.screens.user.collector.customers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import com.example.medscape20.databinding.BottomSheetCustomersFilterBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CustomersFilterBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetCustomersFilterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = BottomSheetCustomersFilterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val previousFilterList =
            arguments?.getStringArrayList(CustomersTrashFilters.FILTERS_LIST.name) as ArrayList<String>
        setPreviousFilters(previousFilterList)

        binding.apply.setOnClickListener {
            val newFiltersList = setNewFilters()
            if (newFiltersList == previousFilterList) {
                dismiss()
            } else {
                setFragmentResult(CustomersTrashFilters.REQUEST_KEY.name, Bundle().apply {
                    putStringArrayList(CustomersTrashFilters.ARGUMENT_KEY.name, newFiltersList)
                })
                dismiss()
            }
        }
        binding.cancel.setOnClickListener {
            dismiss()
        }


    }

    private fun setNewFilters(): ArrayList<String> {
        val filtersList = arrayListOf<String>()
        if (binding.toggleBtn.checkedButtonId==binding.cityBtn.id) filtersList.add(
            CustomersTrashFilters.CITY.name)
        if (binding.toggleBtn.checkedButtonId==binding.stateBtn.id) filtersList.add(
            CustomersTrashFilters.STATE.name)
        if (binding.metal.isChecked) filtersList.add(CustomersTrashFilters.METAL.name)
        if (binding.medical.isChecked) filtersList.add(CustomersTrashFilters.MEDICAL.name)
        if (binding.general.isChecked) filtersList.add(CustomersTrashFilters.GENERAL.name)
        if (binding.plastic.isChecked) filtersList.add(CustomersTrashFilters.PLASTIC.name)
        return filtersList

    }

    private fun setPreviousFilters(previousFilterList: ArrayList<String>) {
        if (previousFilterList.contains(CustomersTrashFilters.CITY.name)) binding.toggleBtn.check(
            binding.cityBtn.id
        )
        if (previousFilterList.contains(CustomersTrashFilters.STATE.name)) binding.toggleBtn.check(
            binding.stateBtn.id
        )
        if (previousFilterList.contains(CustomersTrashFilters.METAL.name)) binding.metal.isChecked =
            true
        if (previousFilterList.contains(CustomersTrashFilters.MEDICAL.name)) binding.medical.isChecked =
            true
        if (previousFilterList.contains(CustomersTrashFilters.GENERAL.name)) binding.general.isChecked =
            true
        if (previousFilterList.contains(CustomersTrashFilters.PLASTIC.name)) binding.plastic.isChecked =
            true
    }

}