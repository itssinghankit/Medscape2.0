package com.example.medscape20.presentation.screens.user.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.anychart.core.cartesian.series.Column
import com.anychart.enums.Anchor
import com.anychart.enums.HoverMode
import com.anychart.enums.Position
import com.anychart.enums.TooltipPositionMode
import com.example.medscape20.R
import com.example.medscape20.data.remote.dto.user.statistics.income_waste.StatisticsIncomeWasteDto
import com.example.medscape20.data.remote.dto.user.statistics.india_waste_treatment.StatisticsIndiaWasteTreatmentDto
import com.example.medscape20.data.remote.dto.user.statistics.region_waste.StatisticsRegionWasteDto
import com.example.medscape20.data.remote.dto.user.statistics.waste_composition.StatisticsWasteCompositionDto
import com.example.medscape20.databinding.FragmentStatisticsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StatisticsViewModel by viewModels()
    private lateinit var globalPieRecyclerAdapter: StatisticsGlobalPieAdapter

    private val bgColor by lazy {  hexColor(R.color.grey_surface) }
    private val strokeColor by lazy { hexColor(R.color.bg_colors) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentStatisticsBinding.inflate(layoutInflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val globalPieList = mutableListOf<StatisticsGlobalPieModel>()

        //setting charts recyclerview
        binding.globalPieRecycler.layoutManager = LinearLayoutManager(context)
        globalPieRecyclerAdapter = StatisticsGlobalPieAdapter()
        binding.globalPieRecycler.adapter = globalPieRecyclerAdapter

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                var flag=true
                viewModel.states.collect { state ->
                    globalPieList.clear()

                    state.regionWasteData?.let {
                        globalPieList.add(addRegionWasteData(it))
                    }
                    state.incomeWasteData?.let {
                        globalPieList.add(addIncomeWasteData(it))

                    }
                    state.wasteCompositionData?.let {
                        globalPieList.add(addWasteCompositionData(it))
                    }

                    //Indian data bar graph
                    state.indiaWasteTreatmentData?.let {
//                       setupIndianBarData(it)

                        if(flag){
//                            set(it)
                            createGroupedBarChart(it)
//                            binding.barGraph.setChart(cartesian)
                            flag=false
                        }

                    }
                   if(state.regionWasteData!=null && state.incomeWasteData!=null && state.wasteCompositionData!=null){
                       globalPieRecyclerAdapter.submitList(globalPieList)
                   }
                }

            }
        }

    }

    private fun set(data: StatisticsIndiaWasteTreatmentDto){
        lifecycleScope.launch(Dispatchers.IO) {
            val cartesian: Cartesian = AnyChart.column()

            // Prepared data for each category (collected, landfilled, etc.)
            val collectedData = mutableListOf<DataEntry>()
            val landfilledData = mutableListOf<DataEntry>()
            val generatedData = mutableListOf<DataEntry>()
            val treatedData = mutableListOf<DataEntry>()

            for (item in data.data) {
                collectedData.add(ValueDataEntry(item.state, item.collected))
                landfilledData.add(ValueDataEntry(item.state, item.landfilled))
                generatedData.add(ValueDataEntry(item.state, item.solid_waste_generated))
                treatedData.add(ValueDataEntry(item.state, item.treated))

            }

            // Created series for each category
            val collectedSeries: Column = cartesian.column(collectedData)
            collectedSeries.name("Collected")

            val landfilledSeries: Column = cartesian.column(landfilledData)
            landfilledSeries.name("Landfilled")

            val generatedSeries: Column = cartesian.column(generatedData)
            generatedSeries.name("Generated")

            val treatedSeries: Column = cartesian.column(treatedData)
            treatedSeries.name("Treated")

            collectedSeries.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0.0)
                .offsetY(5.0)
                .format("\${%Value}{groupsSeparator: }")

            landfilledSeries.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0.0)
                .offsetY(5.0)
                .format("\${%Value}{groupsSeparator: }")

            generatedSeries.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0.0)
                .offsetY(5.0)
                .format("\${%Value}{groupsSeparator: }")

            treatedSeries.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0.0)
                .offsetY(5.0)
                .format("\${%Value}{groupsSeparator: }")

            cartesian.animation(true)
            cartesian.title(data.title)
            cartesian.xScroller(true)
            cartesian.xScroller().allowRangeChange(true)
            cartesian.xScroller().maxHeight(50)

            cartesian.yScale().minimum(0.0)

            cartesian.yAxis(0).labels().format("\${%Value}{groupsSeparator: }")

            cartesian.tooltip().positionMode(TooltipPositionMode.POINT)
            cartesian.interactivity().hoverMode(HoverMode.BY_X)

            cartesian.xAxis(0).title("Product")
            cartesian.yAxis(0).title("Revenue")

            withContext(Dispatchers.Main){
                binding.barGraph.setChart(cartesian)
            }
        }
    }

    private fun setupIndianBarData(data: StatisticsIndiaWasteTreatmentDto) {
        val cartesian = AnyChart.column()

        val myList=data.data.map{ item->
            listOf(
                ValueDataEntry("Collected",item.collected),
                ValueDataEntry("Treated",item.treated),
                ValueDataEntry("Generated",item.solid_waste_generated),
                ValueDataEntry("LandFilled",item.landfilled),
            )
        }

        myList.forEachIndexed { index, valueDataEntry ->
            if(index<4){
                val series = cartesian.column(valueDataEntry)
                series.name(data.data[index].state)
            }

        }

        cartesian.title(data.title)
        cartesian.xAxis(0).title("States")
        cartesian.yAxis(0).title("Values (TPD)")

        binding.barGraph.setChart(cartesian)
    }

    private fun createGroupedBarChart(data: StatisticsIndiaWasteTreatmentDto){
        val cartesian: Cartesian = AnyChart.column()

        // Prepared data for each category (collected, landfilled, etc.)
        val collectedData = mutableListOf<DataEntry>()
        val landfilledData = mutableListOf<DataEntry>()
        val generatedData = mutableListOf<DataEntry>()
        val treatedData = mutableListOf<DataEntry>()

        for (item in data.data) {
                collectedData.add(ValueDataEntry(item.state, item.collected))
                landfilledData.add(ValueDataEntry(item.state, item.landfilled))
                generatedData.add(ValueDataEntry(item.state, item.solid_waste_generated))
                treatedData.add(ValueDataEntry(item.state, item.treated))

        }

        // Created series for each category
        val collectedSeries: Column = cartesian.column(collectedData)
        collectedSeries.name("Collected")

        val landfilledSeries: Column = cartesian.column(landfilledData)
        landfilledSeries.name("Landfilled")

        val generatedSeries: Column = cartesian.column(generatedData)
        generatedSeries.name("Generated")

        val treatedSeries: Column = cartesian.column(treatedData)
        treatedSeries.name("Treated")

        // Configured chart appearance
        cartesian.animation(true)
        cartesian.xScroller(true)
        cartesian.xScroller().allowRangeChange(true)
        cartesian.xScroller().maxHeight(50)
        cartesian.title(data.title)

        cartesian.xZoom(7)

        //extra properties
//        cartesian.yScale().minimum(0.0)
//        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }")
//        cartesian.tooltip().positionMode(TooltipPositionMode.POINT)
//        cartesian.interactivity().hoverMode(HoverMode.BY_X)

        cartesian.xAxis(0).title("State")
        cartesian.yAxis(0).title("Waste Amount(TPD)")
        cartesian.background().fill("#121212")

        binding.barGraph.setChart(cartesian)
    }

    private fun addWasteCompositionData(data: StatisticsWasteCompositionDto): StatisticsGlobalPieModel {
        val category = data.data.map { it.category }
        val percentage = data.data.map { it.percentage }
        return StatisticsGlobalPieModel(category, percentage, data.title, bgColor, strokeColor)
    }

    private fun addIncomeWasteData(data: StatisticsIncomeWasteDto): StatisticsGlobalPieModel {
        val incomeLevel = data.data.map { it.income_level }
        val percentage = data.data.map { it.percentage }
        return StatisticsGlobalPieModel(incomeLevel, percentage, data.title, bgColor, strokeColor)
    }

    private fun addRegionWasteData(data: StatisticsRegionWasteDto): StatisticsGlobalPieModel {
        val region = data.data.map { it.region }
        val percentage = data.data.map { it.percentage }
        return StatisticsGlobalPieModel(region, percentage, data.title, bgColor, strokeColor)
    }

    private fun hexColor(@ColorRes myCustomColor: Int): String {
        val colorInt = getColor(requireContext(), myCustomColor)
        val hashedColor = String.format("#%06X", 0xFFFFFF and colorInt)
        return hashedColor
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}

