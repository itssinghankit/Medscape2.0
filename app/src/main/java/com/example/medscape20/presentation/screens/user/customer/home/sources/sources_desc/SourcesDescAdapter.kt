package com.example.medscape20.presentation.screens.user.customer.home.sources.sources_desc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.medscape20.data.remote.dto.user.customer.home.sources.Subtopic
import com.example.medscape20.databinding.SourcesDescSubtopicRecyclerItemBinding

class SourcesDescAdapter :
    ListAdapter<Subtopic, SourcesDescAdapter.ViewHolder>(DiffUtilCallBack()) {

    class ViewHolder(private val binding: SourcesDescSubtopicRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Subtopic) {
            binding.apply {
                subtitle.text = item.subtopic
                description.text = item.sub_description
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SourcesDescSubtopicRecyclerItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffUtilCallBack : DiffUtil.ItemCallback<Subtopic>() {
        override fun areItemsTheSame(oldItem: Subtopic, newItem: Subtopic): Boolean {
            return oldItem.subtopic == newItem.subtopic
        }

        override fun areContentsTheSame(oldItem: Subtopic, newItem: Subtopic): Boolean {
            return oldItem == newItem
        }

    }

}