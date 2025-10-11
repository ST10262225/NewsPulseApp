package com.example.newspulse

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DownloadedArticlesActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_downloaded_articles)

        val backbtn = findViewById<ImageButton>(R.id.btnBack)
        val deletebtn = findViewById<ImageButton>(R.id.btnDelete)

        // Back back to home button
        backbtn.setOnClickListener{
            finish() //closes this activity
        }

        // Delete all downloads
        deletebtn.setOnClickListener{
            Toast.makeText(this, "All downloads deleted", Toast.LENGTH_SHORT).show()
        }
    }
}