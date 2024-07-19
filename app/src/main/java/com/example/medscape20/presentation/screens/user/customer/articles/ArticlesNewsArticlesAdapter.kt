package com.example.medscape20.presentation.screens.user.customer.articles

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medscape20.databinding.RecyclerItemArticlesBinding
import com.example.medscape20.domain.models.ArticleModel


class ArticlesNewsArticlesAdapter(private val onItemClick: (String) -> Unit) :
    ListAdapter<ArticleModel, ArticlesNewsArticlesAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(
        private val binding: RecyclerItemArticlesBinding,
        private val onItemClick: (String) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ArticleModel) {
            binding.apply {
                articleTitle.text = item.title
                articleDescription.text = item.description
                author.text = item.author
                source.text = item.source
            }
            Glide.with(binding.root.context).load(item.urlToImage).into(binding.articleImg)

            itemView.setOnClickListener { onItemClick(item.url ?: "") }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerItemArticlesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<ArticleModel>() {
        override fun areItemsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean =
            oldItem.url == newItem.url

        override fun areContentsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean =
            oldItem == newItem
    }
}
