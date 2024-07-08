package com.example.medscape20.presentation.screens.user.articles

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medscape20.R
import com.example.medscape20.domain.models.ArticleModel
import com.google.android.material.card.MaterialCardView

class ArticlesNewsArticlesAdapter ( private val newsArticlesList: ArrayList<ArticleModel>,
val context: Context,
private val listener: OnArticlesArticleClicked
) : RecyclerView.Adapter<ArticlesNewsArticlesAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.article_title)
        val description = itemView.findViewById<TextView>(R.id.article_description)
        val image = itemView.findViewById<ImageView>(R.id.article_img)
        val author = itemView.findViewById<TextView>(R.id.author)
        val source = itemView.findViewById<TextView>(R.id.source)
        val article = itemView.findViewById<MaterialCardView>(R.id.article)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.articles_recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return newsArticlesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(newsArticlesList[position].urlToImage).into(holder.image)
        holder.title.text = newsArticlesList[position].title
        holder.description.text = newsArticlesList[position].description
        holder.source.text=newsArticlesList[position].source
        holder.author.text=newsArticlesList[position].author

        holder.article.setOnClickListener {
            newsArticlesList[position].url?.let {
                listener.onClicked(it)
            }
        }

    }

    fun updateData(newItems: List<ArticleModel>) {
        newsArticlesList.clear()
        newsArticlesList.addAll(newItems)
        notifyDataSetChanged()
    }
}

interface OnArticlesArticleClicked {
    fun onClicked(url: String)
}