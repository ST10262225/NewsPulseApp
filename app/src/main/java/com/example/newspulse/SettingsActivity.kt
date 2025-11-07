package com.example.newspulse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.messaging.FirebaseMessaging

class SettingsActivity : BaseActivity() {

    private lateinit var darkModeSwitch: Switch
    private lateinit var notificationsSwitch: Switch
    private lateinit var languageSpinner: Spinner
    private lateinit var fontSizeSpinner: Spinner
    private lateinit var aboutBtn: Button
    private lateinit var logoutBtn: Button

    private val languages = arrayOf("English", "Français", "Español", "Português", "Afrikaans")
    private val langCodes = arrayOf("en", "fr", "es", "pt", "af")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("SettingsPref", Context.MODE_PRIVATE)

        // ✅ Apply saved font size first!
        val savedFont = sharedPref.getFloat("fontScale", 1.0f)
        applySavedFontScale(savedFont)

        // ✅ Apply dark mode setting
        val isDarkMode = sharedPref.getBoolean("darkMode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        setContentView(R.layout.activity_settings)

        // Initialize views
        darkModeSwitch = findViewById(R.id.switchDarkMode)
        notificationsSwitch = findViewById(R.id.switchNotifications)
        languageSpinner = findViewById(R.id.spinnerLanguage)
        fontSizeSpinner = findViewById(R.id.fontSizeSpinner)
        aboutBtn = findViewById(R.id.btnAbout)
        logoutBtn = findViewById(R.id.btnLogout)

        darkModeSwitch.isChecked = isDarkMode
        notificationsSwitch.isChecked = sharedPref.getBoolean("notificationsEnabled", true)

        // ✅ Dark mode toggle
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("darkMode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        // ✅ Notifications toggle
        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("notificationsEnabled", isChecked).apply()
            Toast.makeText(
                this,
                if (isChecked) getString(R.string.notifications_enabled)
                else getString(R.string.notifications_disabled),
                Toast.LENGTH_SHORT
            ).show()

            if (isChecked)
                FirebaseMessaging.getInstance().subscribeToTopic("news_updates")
            else
                FirebaseMessaging.getInstance().unsubscribeFromTopic("news_updates")
        }

        // ✅ Language Spinner setup
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        val savedLang = sharedPref.getString("appLanguage", "en") ?: "en"
        val savedIndex = langCodes.indexOf(savedLang)
        if (savedIndex >= 0) languageSpinner.setSelection(savedIndex)

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedLangCode = langCodes[position]
                val currentLang = sharedPref.getString("appLanguage", "en")

                if (selectedLangCode != currentLang) {
                    sharedPref.edit().putString("appLanguage", selectedLangCode).apply()
                    LocaleHelper.setLocale(this@SettingsActivity, selectedLangCode)
                    restartActivity()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // ✅ Font Size Spinner – Full UI apply + save
        val fontSizes = arrayOf("Small", "Medium", "Large")
        val fontAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fontSizes)
        fontAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fontSizeSpinner.adapter = fontAdapter

        when (savedFont) {
            0.85f -> fontSizeSpinner.setSelection(0)
            1.0f -> fontSizeSpinner.setSelection(1)
            1.15f -> fontSizeSpinner.setSelection(2)
        }

        fontSizeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val newScale = when (position) {
                    0 -> 0.85f
                    1 -> 1.0f
                    2 -> 1.15f
                    else -> 1.0f
                }

                if (newScale != savedFont) {
                    sharedPref.edit().putFloat("fontScale", newScale).apply()
                    applyFontSizeInstantly(newScale)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        aboutBtn.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }

        logoutBtn.setOnClickListener {
            sharedPref.edit().putBoolean("isLoggedIn", false).apply()
            Toast.makeText(this, getString(R.string.logged_out), Toast.LENGTH_SHORT).show()
            restartActivity(LoginActivity::class.java)
        }
    }

    // ✅ Rebuild the UI after applying settings changes
    private fun restartActivity(target: Class<*> = MainActivity::class.java) {
        val intent = Intent(this, target)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    // ✅ Apply font globally (before layout inflated)
    private fun applySavedFontScale(scale: Float) {
        val config = resources.configuration
        config.fontScale = scale
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    // ✅ Apply font & refresh instantly
    private fun applyFontSizeInstantly(scale: Float) {
        applySavedFontScale(scale)
        recreate()
    }
}
