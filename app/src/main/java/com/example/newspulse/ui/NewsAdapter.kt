package com.example.newspulse.ui

import android.content.Intent
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newspulse.R
import com.example.newspulse.data.News
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class NewsAdapter(
    private val newsList: MutableList<News>,
    private val onDownloadClick: (News) -> Unit
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.newsTitle)
        val description: TextView = itemView.findViewById(R.id.newsDescription)
        val image: ImageView = itemView.findViewById(R.id.newsImage)
        val downloadBtn: ImageButton = itemView.findViewById(R.id.btnDownload)
        val shareBtn: ImageButton = itemView.findViewById(R.id.btnShare)

        var originalTitle: String = ""
        var originalDescription: String = ""
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = newsList[position]
        holder.originalTitle = article.title ?: ""
        holder.originalDescription = article.description ?: ""

        holder.title.text = holder.originalTitle
        holder.description.text = holder.originalDescription

        Glide.with(holder.itemView.context)
            .load(article.imageUrl ?: "")
            .placeholder(R.drawable.sample_news)
            .error(R.drawable.sample_news)
            .into(holder.image)

        // Download click
        holder.downloadBtn.setOnClickListener {
            try {
                onDownloadClick(article)
            } catch (e: Exception) {
                Toast.makeText(holder.itemView.context, "Failed to download article", Toast.LENGTH_SHORT).show()
            }
        }

        // Share click
        holder.shareBtn.setOnClickListener {
            val shareText = "${holder.originalTitle}\n\n${holder.originalDescription}"
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            holder.itemView.context.startActivity(
                Intent.createChooser(intent, "Share article via")
            )
        }

        // Automatic offline translation
        val prefs = holder.itemView.context.getSharedPreferences("SettingsPref", android.content.Context.MODE_PRIVATE)
        val lang = prefs.getString("appLanguage", "en") ?: "en"

        if (lang != "en") {
            val targetLang = when (lang) {
                "fr" -> TranslateLanguage.FRENCH
                "es" -> TranslateLanguage.SPANISH
                "pt" -> TranslateLanguage.PORTUGUESE
                "af" -> TranslateLanguage.AFRIKAANS
                else -> TranslateLanguage.ENGLISH
            }

            safeTranslate(holder, targetLang, lang)
        }

        // Long press to manually select language
        holder.itemView.setOnLongClickListener {
            showLanguagePicker(holder)
            true
        }
    }

    override fun getItemCount(): Int = newsList.size

    private fun showLanguagePicker(holder: NewsViewHolder) {
        val context = holder.itemView.context
        val languages = arrayOf(
            "English 🇬🇧",
            "Spanish 🇪🇸",
            "Portuguese 🇵🇹",
            "Afrikaans 🇿🇦",
            "French 🇫🇷"
        )

        AlertDialog.Builder(context)
            .setTitle("Choose translation language")
            .setItems(languages) { _, which ->
                when (which) {
                    0 -> {
                        holder.title.text = holder.originalTitle
                        holder.description.text = holder.originalDescription
                        Toast.makeText(context, "Showing English 🇬🇧", Toast.LENGTH_SHORT).show()
                    }
                    1 -> safeTranslate(holder, TranslateLanguage.SPANISH, "Spanish 🇪🇸")
                    2 -> safeTranslate(holder, TranslateLanguage.PORTUGUESE, "Portuguese 🇵🇹")
                    3 -> safeTranslate(holder, TranslateLanguage.AFRIKAANS, "Afrikaans 🇿🇦")
                    4 -> safeTranslate(holder, TranslateLanguage.FRENCH, "French 🇫🇷")
                }
            }
            .show()
    }

    private fun safeTranslate(holder: NewsViewHolder, targetLang: String, langName: String? = null) {
        try {
            val context = holder.itemView.context
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(targetLang)
                .build()

            val translator = Translation.getClient(options)

            langName?.let { Toast.makeText(context, "Preparing $it translation...", Toast.LENGTH_SHORT).show() }

            translator.downloadModelIfNeeded()
                .addOnSuccessListener {
                    translator.translate(holder.originalTitle)
                        .addOnSuccessListener { translatedTitle ->
                            translator.translate(holder.originalDescription)
                                .addOnSuccessListener { translatedDesc ->
                                    holder.title.text = translatedTitle ?: holder.originalTitle
                                    holder.description.text = translatedDesc ?: holder.originalDescription
                                    langName?.let { Toast.makeText(context, "Translated to $it ✅", Toast.LENGTH_SHORT).show() }
                                }
                                .addOnFailureListener {
                                    holder.title.text = holder.originalTitle
                                    holder.description.text = holder.originalDescription
                                }
                        }
                        .addOnFailureListener {
                            holder.title.text = holder.originalTitle
                            holder.description.text = holder.originalDescription
                        }
                }
                .addOnFailureListener {
                    holder.title.text = holder.originalTitle
                    holder.description.text = holder.originalDescription
                }

        } catch (e: Exception) {
            holder.title.text = holder.originalTitle
            holder.description.text = holder.originalDescription
        }
    }

    fun updateNews(newList: List<News>) {
        newsList.clear()
        newsList.addAll(newList)
        notifyDataSetChanged()
    }
}
