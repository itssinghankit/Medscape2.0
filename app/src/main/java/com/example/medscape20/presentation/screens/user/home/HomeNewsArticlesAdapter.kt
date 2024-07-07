package com.example.medscape20.presentation.screens.user.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medscape20.R
import com.example.medscape20.domain.models.HomeArticleModel

class HomeNewsArticlesAdapter(
    private val newsArticlesList: List<HomeArticleModel>,
    val context: Context
) : RecyclerView.Adapter<HomeNewsArticlesAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.article_title)
        val description = itemView.findViewById<TextView>(R.id.article_description)
        val image = itemView.findViewById<ImageView>(R.id.article_img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_articles_recycler_view_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return newsArticlesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(newsArticlesList[position].urlToImage).into(holder.image)
        holder.title.text = newsArticlesList[position].title
        holder.description.text = newsArticlesList[position].description
    }
}