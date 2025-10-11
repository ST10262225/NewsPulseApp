package com.example.newsapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.newspulse.Headline
import com.example.newspulse.R

class HeadlineAdapter(
    private val headlines: List<Headline>,
    private val onItemClick: (Headline) -> Unit
) : RecyclerView.Adapter<HeadlineAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.headlineImage)
        val title: TextView = itemView.findViewById(R.id.headlineTitle)
        val desc: TextView = itemView.findViewById(R.id.headlineDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder  {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_headline, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val headline = headlines[position]

        holder.title.text = headline.title
        holder.desc.text = headline.description

        // static local image
        holder.image.setImageResource(headline.imageResId)

        // handle click
        holder.itemView.setOnClickListener {
            onItemClick(headline)
        }
    }

    override fun getItemCount() = headlines.size
}
