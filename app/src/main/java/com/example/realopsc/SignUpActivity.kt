package com.example.realopsc

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etConfPass: EditText
    private lateinit var etPass: EditText
    private lateinit var btnSignUp: Button
    private lateinit var tvRedirectLogin: TextView
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        etEmail = findViewById(R.id.etSEmailAddress)
        etPass = findViewById(R.id.etSPassword)
        etConfPass = findViewById(R.id.etSConfPassword)
        btnSignUp = findViewById(R.id.btnSign_in)
        tvRedirectLogin = findViewById(R.id.tvRedirectLogin)

        // Initialising auth object
        auth = Firebase.auth

        // Set click listener for the sign-up button
        btnSignUp.setOnClickListener {
            signUpUser()
        }

        // Set click listener for the text view to redirect to the login page
        tvRedirectLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    // Function to handle user sign-up
    private fun signUpUser() {
        val email = etEmail.text.toString().trim()
        val pass = etPass.text.toString().trim()
        val confirmPassword = etConfPass.text.toString().trim()

        // Validate email and password fields
        if (email.isBlank() || pass.isBlank() || confirmPassword.isBlank()) {
            Toast.makeText(this, "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if passwords match
        if (pass != confirmPassword) {
            Toast.makeText(this, "Password and Confirm Password do not match", Toast.LENGTH_SHORT).show()
            return
        }

        // Create user account with Firebase authentication
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign-up successful, display a toast message
                Toast.makeText(this, "Successfully Signed Up", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                // Finish the SignUpActivity to prevent returning to it when pressing the back button
                finish()
            } else {
                // Sign-up failed, display an error message toast
                Toast.makeText(this, "Sign Up Failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
