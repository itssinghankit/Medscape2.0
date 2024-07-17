package com.example.medscape20.presentation.screens.user.customer.home.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medscape20.R
import com.example.medscape20.data.remote.dto.user.category.Subtopic

class CategoryRecyclerViewAdapter(private val list: List<Subtopic>) :
    RecyclerView.Adapter<CategoryRecyclerViewAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subTitle = itemView.findViewById<TextView>(R.id.subtitle)
        val description = itemView.findViewById<TextView>(R.id.description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_recycler_view_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.subTitle.text = list[position].subtopic_name
        holder.description.text = list[position].description
    }
}