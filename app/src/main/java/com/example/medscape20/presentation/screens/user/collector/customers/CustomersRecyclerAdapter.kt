package com.example.medscape20.presentation.screens.user.collector.customers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medscape20.data.remote.dto.user.collector.customers.CustomersResDto
import com.example.medscape20.databinding.CustomersRecyclerItemBinding

class CustomersRecyclerAdapter(private val onItemClick: (String) -> Unit) :
    ListAdapter<CustomersResDto, CustomersRecyclerAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(
        private val binding: CustomersRecyclerItemBinding,
        private val onItemClick: (String) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CustomersResDto) {
            binding.apply {
               name.text=item.name
                address.text=item.address
                cityState.text="${item.city}, ${item.state}"
            }

            if(item.metal == true) binding.metal.isChecked =true
            if(item.plastic == true) binding.plastic.isChecked =true
            if(item.general == true) binding.general.isChecked =true
            if(item.medical == true) binding.medical.isChecked =true

            Glide.with(binding.root.context).load(item.avatar).into(binding.avatar)

            itemView.setOnClickListener { onItemClick(item.uid) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CustomersRecyclerItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<CustomersResDto>() {
        override fun areItemsTheSame(oldItem: CustomersResDto, newItem: CustomersResDto): Boolean =
            oldItem.uid == newItem.uid

        override fun areContentsTheSame(oldItem: CustomersResDto, newItem: CustomersResDto): Boolean =
            oldItem == newItem
    }
}