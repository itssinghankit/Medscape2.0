package com.example.medscape20.presentation.screens.user.statistics

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.example.medscape20.databinding.StatisticsPieRecyclerItemBinding

class StatisticsGlobalPieAdapter() :
    ListAdapter<StatisticsGlobalPieModel, StatisticsGlobalPieAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(
        private val binding: StatisticsPieRecyclerItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: StatisticsGlobalPieModel) {
            val pie = AnyChart.pie()
            val dataEntries = item.value.zip(item.percent).map {
                ValueDataEntry(it.first, it.second)
            }
            pie.apply {
                data(dataEntries)
                title(item.title)
                background().fill(item.bgColor)
                stroke(item.strokeColor)
            }
            binding.pie.setChart(pie)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StatisticsPieRecyclerItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<StatisticsGlobalPieModel>() {
        override fun areItemsTheSame(
            oldItem: StatisticsGlobalPieModel,
            newItem: StatisticsGlobalPieModel
        ): Boolean =
            oldItem.title == newItem.title

        override fun areContentsTheSame(
            oldItem: StatisticsGlobalPieModel,
            newItem: StatisticsGlobalPieModel
        ): Boolean =
            oldItem == newItem
    }

}