package com.example.newspulse

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initializing Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val fullName = findViewById<EditText>(R.id.FullName)
        val email = findViewById<EditText>(R.id.RegisterEmail)
        val password = findViewById<EditText>(R.id.RegisterPassword)
        val confirmPassword = findViewById<EditText>(R.id.ConfirmPassword)
        val registerBtn = findViewById<Button>(R.id.btnRegister)
        val backToLogin = findViewById<TextView>(R.id.BackToLogin)

        backToLogin.setOnClickListener {
            // Open LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        registerBtn.setOnClickListener {
            val name = fullName.text.toString().trim()
            val userEmail = email.text.toString().trim()
            val pass = password.text.toString().trim()
            val confirmPass = confirmPassword.text.toString().trim()

            if (name.isEmpty() || userEmail.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else if (pass != confirmPass) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                // Create user in Firebase Authentication
                auth.createUserWithEmailAndPassword(userEmail, pass)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val currentUser = auth.currentUser
                            val userId = currentUser!!.uid

                            // Redirect immediately to login
                            Toast.makeText(
                                this,
                                "Account Registered Successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()

                            // Save extra user info in Realtime Database
                            val userRef = database.reference.child("users").child(userId)
                            val userData = mapOf(
                                "fullName" to name,
                                "email" to userEmail
                            )
                            userRef.setValue(userData).addOnFailureListener { dbEx ->
                                // showing DB saving error (user can still log in)
                                Toast.makeText(
                                    this,
                                    "Failed to save user info: ${dbEx.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            // Handle registration failure
                            Toast.makeText(
                                this,
                                "Registration Failed: ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }
    }
}
