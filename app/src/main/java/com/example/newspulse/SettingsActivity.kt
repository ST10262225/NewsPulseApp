package com.example.newspulse

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {

    private lateinit var darkModeSwitch: Switch

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        // Load saved dark mode preference BEFORE setting content view
        val sharedPref = getSharedPreferences("SettingsPref", Context.MODE_PRIVATE)
        val isDarkMode = sharedPref.getBoolean("darkMode", false)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        darkModeSwitch = findViewById(R.id.switchDarkMode)
        val notificationsSwitch: Switch = findViewById(R.id.switchNotifications)
        val fontSizeSpinner: Spinner = findViewById(R.id.fontSizeSpinner)
        val aboutBtn: Button = findViewById(R.id.btnAbout)
        val logoutBtn: Button = findViewById(R.id.btnLogout)

        // Set switch position based on saved preference
        darkModeSwitch.isChecked = isDarkMode

        // Dark Mode toggle
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPref.edit()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                editor.putBoolean("darkMode", true)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                editor.putBoolean("darkMode", false)
            }
            editor.apply()
        }

        // Notifications
        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            val message = if (isChecked) "Notifications Enabled" else "Notifications Disabled"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }




        // About App
        aboutBtn.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }

        // Logout
        logoutBtn.setOnClickListener {
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()

            // Clear saved login session if any
            val sharedPref = getSharedPreferences("SettingsPref", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putBoolean("isLoggedIn", false)
            editor.apply()

            // Start LoginActivity and clear back stack
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // close current SettingsActivity
        }

    }
}
