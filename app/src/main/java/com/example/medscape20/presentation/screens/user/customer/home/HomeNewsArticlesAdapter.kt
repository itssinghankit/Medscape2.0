package com.example.medscape20.presentation.screens.user.customer.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medscape20.databinding.RecyclerItemHomeArticlesBinding
import com.example.medscape20.domain.models.ArticleModel

class HomeNewsArticlesAdapter(private val onItemClick: (String) -> Unit) :
    ListAdapter<ArticleModel, HomeNewsArticlesAdapter.ViewHolder>(DiffUtilCallback()) {

    inner class ViewHolder(
        private val binding: RecyclerItemHomeArticlesBinding,
        private val onItemClick: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ArticleModel) {
            binding.articleTitle.text = item.title
            binding.articleDescription.text = item.description
            Glide.with(binding.root.context).load(item.urlToImage).into(binding.articleImg)

            itemView.setOnClickListener { onItemClick(item.url!!) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerItemHomeArticlesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<ArticleModel>() {
        override fun areItemsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean =
            oldItem.url == newItem.url

        override fun areContentsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean =
            oldItem == newItem

    }

}