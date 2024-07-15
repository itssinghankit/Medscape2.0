package com.example.medscape20.presentation.screens.user.trash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.setFragmentResult
import com.example.medscape20.R
import com.example.medscape20.databinding.ArticlesBottomSheetFilterBinding
import com.example.medscape20.databinding.TrashBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import timber.log.Timber

class TrashBottomSheet :
    BottomSheetDialogFragment() {

    lateinit var binding: TrashBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TrashBottomSheetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //setting the filter listener
        binding.dump.setOnClickListener {
//            setFragmentResult(FilterArgs.REQUEST_KEY.value, Bundle().apply {
////                putString(FilterArgs.COUNTRY.value, countryAbbreviation)
////                putString(FilterArgs.CATEGORY.value, category.value)
//            })
            dismiss()
        }
        binding.cancel.setOnClickListener {
            dismiss()
        }
    }

}

