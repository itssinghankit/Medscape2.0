package com.example.medscape20.presentation.screens.user.customer.trash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import com.example.medscape20.R
import com.example.medscape20.databinding.BottomSheetTrashBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TrashBottomSheet :
    BottomSheetDialogFragment() {

    lateinit var binding: BottomSheetTrashBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetTrashBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val trashTypeList = arrayListOf<String>()

        binding.chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->

            trashTypeList.clear()

            if (checkedIds.isNotEmpty()) {
                binding.error.visibility = View.INVISIBLE

                for (checkedId in checkedIds) {
                    when (checkedId) {
                        R.id.metal -> trashTypeList.add(TrashType.METAL.value)
                        R.id.plastic -> trashTypeList.add(TrashType.PLASTIC.value)
                        R.id.general -> trashTypeList.add(TrashType.GENERAL.value)
                        R.id.medical -> trashTypeList.add(TrashType.MEDICAL.value)
                    }
                }
            }
        }
        //setting the dump logic
        binding.dump.setOnClickListener {
            if (trashTypeList.isEmpty()) {
                binding.error.visibility = View.VISIBLE
            } else {
                setFragmentResult(TrashType.REQUEST_KEY.value, Bundle().apply {
                    putStringArrayList(TrashType.ARGUMENT_KEY.value, trashTypeList)
                })
                dismiss()
            }
        }

        binding.cancel.setOnClickListener {
            dismiss()
        }

    }


}

enum class TrashType(val value: String) {
    REQUEST_KEY("request_key"),
    ARGUMENT_KEY("argument_key"),
    METAL("metal"),
    PLASTIC("plastic"),
    GENERAL("general"),
    MEDICAL("medical")

}