package com.example.medscape20.presentation.screens.user.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medscape20.databinding.HomeArticleNewsSearchRecyclerItemBinding
import com.example.medscape20.domain.models.ArticleModel

class HomeSearchArticleAdapter(private val oldArticlesList: ArrayList<ArticleModel>) :
    RecyclerView.Adapter<HomeSearchArticleAdapter.ViewHolder>() {

    class ViewHolder(private val binding: HomeArticleNewsSearchRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ArticleModel) {
            Glide.with(binding.root.context).load(item.url).into(binding.articleImg)
            binding.articleTitle.text = item.title
            binding.source.text = item.source
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HomeArticleNewsSearchRecyclerItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(oldArticlesList[position])
    }

    override fun getItemCount(): Int {
        return oldArticlesList.size
    }
}