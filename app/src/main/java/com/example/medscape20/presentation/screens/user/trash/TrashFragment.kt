package com.example.medscape20.presentation.screens.user.trash

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.medscape20.R
import com.example.medscape20.databinding.FragmentTrashBinding
import com.example.medscape20.presentation.screens.auth.maps.MapsFragment.Companion.PERMISSIONS_ARRAY


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

        binding.cancel.setOnClickListener {
            showCancelDialog()
        }

    }

    private fun showCancelDialog() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Confirmation")
            .setMessage(
               "Are you sure you want to cancel?"
            )
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()

            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
           .create().show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}