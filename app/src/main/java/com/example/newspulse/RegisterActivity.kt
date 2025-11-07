package com.example.newspulse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Views
        val fullName = findViewById<EditText>(R.id.FullName)
        val email = findViewById<EditText>(R.id.RegisterEmail)
        val password = findViewById<EditText>(R.id.RegisterPassword)
        val confirmPassword = findViewById<EditText>(R.id.ConfirmPassword)
        val registerBtn = findViewById<Button>(R.id.btnRegister)
        val backToLogin = findViewById<TextView>(R.id.BackToLogin)
        val languageSpinner = findViewById<Spinner>(R.id.languageSpinner)

        // 🟢 Language spinner setup
        val languageNames = resources.getStringArray(R.array.language_names)
        val languageCodes = resources.getStringArray(R.array.language_codes)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            languageNames
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        languageSpinner.adapter = adapter

        val prefs = getSharedPreferences("SettingsPref", Context.MODE_PRIVATE)
        val savedLang = prefs.getString("appLanguage", "en") ?: "en"
        val langIndex = languageCodes.indexOf(savedLang)
        if (langIndex >= 0) languageSpinner.setSelection(langIndex)

        // 🟢 Spinner listener → Save language and restart app globally
        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedLangCode = languageCodes[position]
                val currentLang = prefs.getString("appLanguage", "en")
                if (selectedLangCode != currentLang) {
                    prefs.edit().putString("appLanguage", selectedLangCode).apply()
                    // Restart the whole app to apply new language across all screens
                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // 🟢 Back to login
        backToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // 🟢 Register button logic
        registerBtn.setOnClickListener {
            val name = fullName.text.toString().trim()
            val userEmail = email.text.toString().trim()
            val pass = password.text.toString().trim()
            val confirmPass = confirmPassword.text.toString().trim()

            when {
                name.isEmpty() || userEmail.isEmpty() || pass.isEmpty() || confirmPass.isEmpty() ->
                    Toast.makeText(this, getString(R.string.enter_all_fields), Toast.LENGTH_SHORT).show()

                pass != confirmPass ->
                    Toast.makeText(this, getString(R.string.passwords_not_match), Toast.LENGTH_SHORT).show()

                !android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches() ->
                    Toast.makeText(this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show()

                pass.length < 6 ->
                    Toast.makeText(this, getString(R.string.password_too_short), Toast.LENGTH_SHORT).show()

                else -> {
                    auth.createUserWithEmailAndPassword(userEmail, pass)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val currentUser = auth.currentUser!!
                                val userId = currentUser.uid
                                val userRef = database.reference.child("users").child(userId)
                                val userData = mapOf("fullName" to name, "email" to userEmail)

                                userRef.setValue(userData).addOnFailureListener { dbEx ->
                                    Toast.makeText(
                                        this,
                                        getString(R.string.failed_save_user_info, dbEx.message),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                Toast.makeText(
                                    this,
                                    getString(R.string.account_registered),
                                    Toast.LENGTH_SHORT
                                ).show()

                                // 🟢 After registration, go to Login (language applied)
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(
                                    this,
                                    getString(R.string.registration_failed, task.exception?.message),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                }
            }
        }
    }
}
