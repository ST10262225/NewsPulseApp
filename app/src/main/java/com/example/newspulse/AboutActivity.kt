package com.example.newspulse

import android.os.Bundle
import android.widget.Button

class AboutActivity : BaseActivity() {  // ✅ Extend BaseActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val backBtn: Button = findViewById(R.id.btnBack)
        backBtn.setOnClickListener {
            finish() // closes this activity and returns to previous screen
        }
    }
}
