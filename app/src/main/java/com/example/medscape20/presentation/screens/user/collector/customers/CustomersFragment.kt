package com.example.medscape20.presentation.screens.user.collector.customers

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medscape20.databinding.FragmentCustomersBinding
import com.example.medscape20.domain.usecase.user.collector.customers.CustomerDetailBottomSheetEnum
import com.example.medscape20.domain.usecase.user.collector.customers.CustomersTrashFilters
import com.example.medscape20.domain.usecase.user.collector.customers.CustomersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CustomersFragment : Fragment() {

    private var _binding: FragmentCustomersBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CustomersViewModel by viewModels()
    private val args: CustomersFragmentArgs by navArgs()

    private lateinit var mapLauncher: ActivityResultLauncher<Intent>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomersBinding.inflate(layoutInflater, container, false)

        mapLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
                //we don't need to do anything here as our map is shown in the app
//                if (result.resultCode == Activity.RESULT_CANCELED) {
//                    // Handle the case where the user cancelled the Maps action
//                    Toast.makeText(context, "Map view was cancelled", Toast.LENGTH_SHORT).show()
//                }
            }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //set collector city and state
        viewModel.event(
            CustomersEvents.SetCollectorCityState(
                args.collectorCity,
                args.collectorState
            )
        )

        //getting new filters
        setFragmentResultListener(CustomersTrashFilters.REQUEST_KEY.name) { _, bundle ->
            val newFilters = bundle.getStringArrayList(CustomersTrashFilters.ARGUMENT_KEY.name)
            newFilters?.let {
                viewModel.event(CustomersEvents.OnNewFiltersSet(it))
            }
        }

        //selected option form details bottom sheet
        setFragmentResultListener(CustomerDetailBottomSheetEnum.REQUEST_KEY_DETAILS.name) { _, bundle ->
            val selectedDisposeOption =
                bundle.getBoolean(CustomerDetailBottomSheetEnum.DISPOSED_OPTION.name)
            val currentItemPosition =
                bundle.getInt(CustomerDetailBottomSheetEnum.CURRENT_ITEM_POSITION.name)
            val selectedLocateOption =
                bundle.getBoolean(CustomerDetailBottomSheetEnum.LOCATE_OPTION.name)

            if (selectedLocateOption) {
                openLocationInGoogleMaps(
                    viewModel.state.value.newFilteredList[currentItemPosition].lat,
                    viewModel.state.value.newFilteredList[currentItemPosition].lng
                )
            }
            if (selectedDisposeOption) viewModel.event(
                CustomersEvents.OnDisposedClicked(
                    currentItemPosition
                )
            )

        }


        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.filterBtn.setOnClickListener {
            openFilterBottomSheet()
        }

        val adapter = CustomersRecyclerAdapter { position ->
            showDetailBottomSheet(position)
        }
        binding.customersRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.customersRecyclerView.adapter = adapter

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->

                    if (state.isLoading) {
                        binding.progressCircular.visibility = View.VISIBLE
                        binding.mainContent.visibility = View.GONE
                    } else {
                        binding.progressCircular.visibility = View.GONE
                        binding.mainContent.visibility = View.VISIBLE
                    }

                    if (state.newFilteredList.isNotEmpty()) {
                        adapter.submitList(state.newFilteredList)
                    }

                }
            }
        }

    }

    private fun showDetailBottomSheet(position: Int) {

        val customer = viewModel.state.value.newFilteredList[position]
        val bottomSheet = CustomerDetailBottomSheet().apply {
            arguments = Bundle().apply {
                putSerializable(CustomerDetailBottomSheetEnum.ARGUMENT_KEY_DETAILS.name, customer)
                putInt(CustomerDetailBottomSheetEnum.CURRENT_ITEM_POSITION.name, position)
            }
        }
        bottomSheet.show(parentFragmentManager, "detail")

    }

    private fun openFilterBottomSheet() {

        //sending previous filters to the bottom sheet
        val filtersList = arrayListOf<String>()
        if (viewModel.filter.value.cityFilter) filtersList.add(CustomersTrashFilters.CITY.name)
        if (viewModel.filter.value.stateFilter) filtersList.add(CustomersTrashFilters.STATE.name)
        if (viewModel.filter.value.metalFilter) filtersList.add(CustomersTrashFilters.METAL.name)
        if (viewModel.filter.value.medicalFilter) filtersList.add(CustomersTrashFilters.MEDICAL.name)
        if (viewModel.filter.value.generalFilter) filtersList.add(CustomersTrashFilters.GENERAL.name)
        if (viewModel.filter.value.plasticFilter) filtersList.add(CustomersTrashFilters.PLASTIC.name)

        val bottomSheet = CustomersFilterBottomSheet().apply {
            arguments = Bundle().apply {
                putStringArrayList(CustomersTrashFilters.FILTERS_LIST.name, filtersList)
            }
        }
        bottomSheet.show(parentFragmentManager, "filter")

    }

    private fun openLocationInGoogleMaps(latitude: Double, longitude: Double) {

        val uri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")
        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        mapIntent.setPackage("com.google.android.apps.maps")

        try {
            mapLauncher.launch(mapIntent)
        } catch (e: ActivityNotFoundException) {
            // Google Maps app is not installed, open in browser instead
            val browserUri = Uri.parse("https://www.google.com/maps?q=$latitude,$longitude")
            val browserIntent = Intent(Intent.ACTION_VIEW, browserUri)
            mapLauncher.launch(browserIntent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}