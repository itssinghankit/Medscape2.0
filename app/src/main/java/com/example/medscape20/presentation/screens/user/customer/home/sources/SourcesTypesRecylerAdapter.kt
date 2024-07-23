package com.example.medscape20.presentation.screens.user.customer.home.sources

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medscape20.data.remote.dto.user.customer.home.sources.SourcesDto
import com.example.medscape20.databinding.RecyclerItemSourcesTypesBinding

class SourcesTypesRecylerAdapter(private val onItemClick: (Int) -> Unit) :
    ListAdapter<SourcesDto, SourcesTypesRecylerAdapter.ViewModel>(DiffUtilCallBack()) {

    class ViewModel(
        private val binding: RecyclerItemSourcesTypesBinding,
        onItemClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }

        fun bind(item: SourcesDto) {
            binding.apply {
                topic.text = item.topic
                description.text = item.description
                Glide.with(binding.root.context).load(item.image).into(binding.sourceImg)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewModel {
        val binding = RecyclerItemSourcesTypesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewModel(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewModel, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffUtilCallBack : DiffUtil.ItemCallback<SourcesDto>() {
        override fun areItemsTheSame(oldItem: SourcesDto, newItem: SourcesDto): Boolean {
            return oldItem.topic == newItem.topic
        }

        override fun areContentsTheSame(oldItem: SourcesDto, newItem: SourcesDto): Boolean {
            return oldItem == newItem
        }

    }

}