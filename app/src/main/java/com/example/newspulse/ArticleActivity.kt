package com.example.newspulse

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ArticleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        val title = intent.getStringExtra("title")
        val desc = intent.getStringExtra("desc")
        val imageResId = intent.getIntExtra("imageResId", R.drawable.sample_news)

        val titleTextView: TextView = findViewById(R.id.title)
        val contentTextView: TextView = findViewById(R.id.content)
        val articleImageView: ImageView = findViewById(R.id.ivArticle)

        titleTextView.text = title
        contentTextView.text = desc
        articleImageView.setImageResource(imageResId)
    }
}
