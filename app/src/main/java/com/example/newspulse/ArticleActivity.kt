package com.example.newspulse

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class ArticleActivity : BaseActivity() {  // ✅ Extend BaseActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        val title = intent.getStringExtra("title") ?: getString(R.string.no_title)
        val desc = intent.getStringExtra("desc") ?: getString(R.string.no_description)
        val imageResId = intent.getIntExtra("imageResId", R.drawable.sample_news)

        val titleTextView: TextView = findViewById(R.id.title)
        val contentTextView: TextView = findViewById(R.id.content)
        val articleImageView: ImageView = findViewById(R.id.ivArticle)

        // Set text and image
        titleTextView.text = title
        contentTextView.text = desc
        articleImageView.setImageResource(imageResId)
    }
}
