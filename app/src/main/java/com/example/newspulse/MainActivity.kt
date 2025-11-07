package com.example.newspulse

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.newspulse.BaseActivity

class MainActivity : BaseActivity() { // ✅ Extend BaseActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Delay 3 seconds and go to LoginActivity
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 3000)
    }
}
