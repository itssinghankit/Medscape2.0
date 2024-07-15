package com.example.medscape20.presentation.screens.user.trash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.medscape20.databinding.FragmentTrashBinding


class TrashFragment : Fragment() {

    private var _binding: FragmentTrashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrashBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.dump.setOnClickListener {
            val bottomSheet = TrashBottomSheet()
            bottomSheet.show(parentFragmentManager, "trashBottomSheet")
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}