package com.example.newspulse.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newspulse.R
import com.example.newspulse.data.DownloadedArticle

class DownloadedArticlesAdapter(
    private val articles: MutableList<DownloadedArticle>,
    private val onDeleteClick: (DownloadedArticle) -> Unit,
    private val onLongPress: ((DownloadedArticle) -> Unit)? = null // optional extra callback
) : RecyclerView.Adapter<DownloadedArticlesAdapter.DownloadedViewHolder>() {

    inner class DownloadedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.newsTitle)
        val description: TextView = itemView.findViewById(R.id.newsDescription)
        val image: ImageView = itemView.findViewById(R.id.newsImage)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadedViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_downloaded_article, parent, false)
        return DownloadedViewHolder(view)
    }

    override fun onBindViewHolder(holder: DownloadedViewHolder, position: Int) {
        val article = articles[position]

        // ✅ Select title & description based on current language
        val titleText = when (article.language) {
            "en" -> article.title
            "fr" -> article.title_fr ?: article.title
            "es" -> article.title_es ?: article.title
            "pt" -> article.title_pt ?: article.title
            "af" -> article.title_af ?: article.title
            else -> article.title
        }

        val descriptionText = when (article.language) {
            "en" -> article.description
            "fr" -> article.description_fr ?: article.description
            "es" -> article.description_es ?: article.description
            "pt" -> article.description_pt ?: article.description
            "af" -> article.description_af ?: article.description
            else -> article.description
        }

        holder.title.text = titleText
        holder.description.text = descriptionText

        Glide.with(holder.itemView.context)
            .load(article.imageUrl)
            .placeholder(R.drawable.sample_news)
            .into(holder.image)

        holder.btnDelete.setOnClickListener {
            onDeleteClick(article)
        }

        holder.itemView.setOnLongClickListener {
            onLongPress?.invoke(article)
            true
        }
    }


    override fun getItemCount(): Int = articles.size

    // Allows deletion of a specific article
    fun removeArticle(article: DownloadedArticle) {
        val index = articles.indexOf(article)
        if (index != -1) {
            articles.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    // Replace entire dataset
    fun updateData(newArticles: List<DownloadedArticle>) {
        articles.clear()
        articles.addAll(newArticles)
        notifyDataSetChanged()
    }

    // Update a single article in-place (useful after changing language/translation)
    fun updateArticle(updatedArticle: DownloadedArticle) {
        val index = articles.indexOfFirst { it.id == updatedArticle.id }
        if (index != -1) {
            articles[index] = updatedArticle
            notifyItemChanged(index)
        }
    }
}
