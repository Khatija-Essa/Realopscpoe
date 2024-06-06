package com.example.realopsc

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.sql.Time

class totalhours : AppCompatActivity() {
    var id: Int? = null
    var punchInTime: Time? = null
    var punchOutTime: Time? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_totalhours)
        val btn1  = findViewById<Button>(R.id.closeButton)
        btn1.setOnClickListener {
            val intent = Intent(this, AllCategories::class.java)
            startActivity(intent)}
        }
    }
