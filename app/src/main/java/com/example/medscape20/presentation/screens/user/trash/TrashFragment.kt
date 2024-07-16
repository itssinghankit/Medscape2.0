package com.example.medscape20.presentation.screens.user.trash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.medscape20.R
import com.example.medscape20.databinding.FragmentTrashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TrashFragment : Fragment() {

    private var _binding: FragmentTrashBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TrashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrashBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //dump trash type bottom sheet filter result
        setFragmentResultListener(TrashType.REQUEST_KEY.value) { _, bundle ->
            val trashTypeList = bundle.getStringArrayList(TrashType.ARGUMENT_KEY.value)

            trashTypeList?.let {
                viewModel.event(TrashEvents.OnTrashTypesSet(trashTypeList))
            }

        }

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        //in both cases we have to do same thing that is update trash types
        binding.dump.setOnClickListener {
            val bottomSheet = TrashBottomSheet()
            bottomSheet.show(parentFragmentManager, "trashBottomSheet")
        }
        binding.edit.setOnClickListener {
            val bottomSheet = TrashBottomSheet()
            bottomSheet.show(parentFragmentManager, "trashBottomSheet")
        }

        binding.cancel.setOnClickListener {
            showCancelDialog()
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.states.collect { state ->

                    if (state.isLoading) {
                        binding.dumpLayout.visibility = View.GONE
                        binding.dumpedLayout.visibility = View.GONE
                        binding.progressCircular.visibility = View.VISIBLE
                    } else {
                        binding.progressCircular.visibility = View.GONE
                        if (state.showDumpPage) {
                            binding.dumpLayout.visibility = View.VISIBLE
                        } else {
                            binding.dumpedLayout.visibility = View.VISIBLE

                            //set toggle buttons for showing user current trash type selected
                            if (state.metal) binding.toggleBtn.check(R.id.metal_btn) else binding.toggleBtn.uncheck(R.id.metal_btn)
                            if (state.general) binding.toggleBtn.check(R.id.general_btn) else binding.toggleBtn.uncheck(R.id.general_btn)
                            if (state.medical) binding.toggleBtn.check(R.id.medical_btn) else binding.toggleBtn.uncheck(R.id.medical_btn)
                            if (state.plastic) binding.toggleBtn.check(R.id.plastic_btn) else binding.toggleBtn.uncheck(R.id.plastic_btn)
                        }
                    }


                }
            }
        }

    }

    private fun showCancelDialog() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Confirmation")
            .setMessage(
                "Are you sure you want to cancel?"
            )
            .setPositiveButton("Yes") { dialog, _ ->
                viewModel.event(TrashEvents.OnCancelClicked)
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