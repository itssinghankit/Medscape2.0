package com.example.medscape20.presentation.screens.user.collector.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.medscape20.R
import com.example.medscape20.databinding.FragmentCollectorHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CollectorHomeFragment : Fragment() {

    private var _binding: FragmentCollectorHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CollectorHomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectorHomeBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.peopleCount.animateToValue(1029)

        //here as dummy data is less so to make animation we are multiplying count variables by 500
        binding.chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            for (checkedId in checkedIds) {
                when (checkedId) {
                    R.id.city_chip -> binding.peopleCount.animateToValue(
                        500 * (viewModel.state.value.cityCount ?: 0)
                    )

                    R.id.state_chip -> binding.peopleCount.animateToValue(
                        500 * (viewModel.state.value.stateCount ?: 0)
                    )

                    R.id.country_chip -> binding.peopleCount.animateToValue(
                        500 * (viewModel.state.value.totalCount ?: 0)
                    )
                }
            }
        }

        binding.seeListBtn.setOnClickListener {
            val action =
                CollectorHomeFragmentDirections.actionCollectorHomeFragmentToCustomersFragment(
                    viewModel.state.value.city ?: "",
                    viewModel.state.value.state ?: ""
                )
            findNavController().navigate(action)
        }

        binding.mapViewBtn.setOnClickListener {
            findNavController().navigate(R.id.action_collectorHomeFragment_to_collectorMapsFragment)
        }

        binding.logoutBtn.setOnClickListener {
            showLogoutAlertDialog()
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.state.collect { state ->
                    if (state.isLoading) {
                        binding.progressCircular.visibility = View.VISIBLE
                        binding.mainView.visibility = View.GONE
                    } else {
                        binding.progressCircular.visibility = View.GONE
                        binding.mainView.visibility = View.VISIBLE
                    }

                    state.avatar?.let {
                        Glide.with(requireView()).load(it).into(binding.avatar)
                    }

                    //here as dummy data is less so to make animation we are multiplying count variables by 500
                    state.totalCount?.let {
                        when {
                            binding.cityChip.isChecked -> binding.peopleCount.animateToValue(500 * state.cityCount!!)
                            binding.stateChip.isChecked -> binding.peopleCount.animateToValue(500 * state.stateCount!!)
                            binding.countryChip.isChecked -> binding.peopleCount.animateToValue(500 * state.totalCount)
                        }
                    }
                }
            }
        }
    }

    private fun showLogoutAlertDialog() {

        AlertDialog.Builder(requireContext())
            .setTitle("Confirmation")
            .setMessage(
                "Are you sure you want to logout?"
            )
            .setPositiveButton("Yes") { dialog, _ ->
                viewModel.event(CollectorHomeEvents.OnLogOutClicked)
                findNavController().navigate(
                    R.id.action_collectorHomeFragment_to_loginFragment,
                    null,
                    NavOptions.Builder().setPopUpTo(R.id.collectorHomeFragment, true).build()
                )
                dialog.dismiss()

            }.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create().show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}