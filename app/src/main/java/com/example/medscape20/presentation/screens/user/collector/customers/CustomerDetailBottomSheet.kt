package com.example.medscape20.presentation.screens.user.collector.customers

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.setFragmentResult
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.medscape20.data.remote.dto.user.collector.customers.CustomersResDto
import com.example.medscape20.databinding.CustomerShowDetailsBottomSheetBinding
import com.example.medscape20.domain.usecase.user.collector.customers.CustomerDetailBottomSheetEnum
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CustomerDetailBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: CustomerShowDetailsBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CustomerShowDetailsBottomSheetBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val details = arguments?.getSerializable(
            CustomerDetailBottomSheetEnum.ARGUMENT_KEY_DETAILS.name,
            CustomersResDto::class.java
        )
        val currentItemPos=arguments?.getInt(CustomerDetailBottomSheetEnum.CURRENT_ITEM_POSITION.name)!!

        details?.let {
            setDataToLayout(it)
        }

        binding.disposedBtn.setOnClickListener {
            confirmationDialog(currentItemPos)
        }

        binding.mapViewBtn.setOnClickListener {
            setFragmentResult(CustomerDetailBottomSheetEnum.REQUEST_KEY_DETAILS.name,Bundle().apply {
                putInt(CustomerDetailBottomSheetEnum.CURRENT_ITEM_POSITION.name,currentItemPos)
                putBoolean(CustomerDetailBottomSheetEnum.LOCATE_OPTION.name,true)
            })
            dismiss()
        }

    }

    private fun setDataToLayout(data: CustomersResDto) {
        binding.apply {
            name.text = data.name
            address.text = data.address
            gender.text =
                data.gender.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            mobile.text = data.mobile
            cityState.text = "${data.city}, ${data.state}}"
        }
        Glide.with(requireContext()).load(data.avatar)
            .transition(DrawableTransitionOptions.withCrossFade()).into(binding.avatar)

        if (data.metal == true) binding.metal.isChecked = true
        if (data.medical == true) binding.medical.isChecked = true
        if (data.general == true) binding.general.isChecked = true
        if (data.plastic == true) binding.plastic.isChecked = true

    }

    private fun confirmationDialog(currentItemPos:Int) {

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Confirmation")
            .setMessage(
                "Have you disposed all waste from this customer"
            )
            .setPositiveButton("Yes") { dialog, _ ->
                setFragmentResult(CustomerDetailBottomSheetEnum.REQUEST_KEY_DETAILS.name,Bundle().apply {
                    putBoolean(CustomerDetailBottomSheetEnum.DISPOSED_OPTION.name,true)
                    putInt(CustomerDetailBottomSheetEnum.CURRENT_ITEM_POSITION.name,currentItemPos)
                })
                dialog.dismiss()
                dismiss()

            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create().show()
    }

}