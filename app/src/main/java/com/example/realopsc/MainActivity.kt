package com.example.realopsc

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val signUpButton: Button = findViewById(R.id.SignUp)
        signUpButton.setOnClickListener {
            // Create Intent to navigate to SignUpActivity
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent) // Start SignUpActivity
        }

        val loginButton: Button = findViewById(R.id.Login)

        // Set onClickListener for the Login button
        loginButton.setOnClickListener {
            // Create Intent to navigate to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent) // Start LoginActivity
        }
    }
    }
