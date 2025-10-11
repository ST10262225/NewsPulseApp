package com.example.newspulse

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val backBtn: Button = findViewById(R.id.btnBack)
        backBtn.setOnClickListener {
            finish() // closes this activity and returns to previous screen
        }
    }
}
