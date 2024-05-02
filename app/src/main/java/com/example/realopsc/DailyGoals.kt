package com.example.realopsc

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DailyGoals : AppCompatActivity() {
    private lateinit var addButton: Button
    private lateinit var minGoalInput: EditText
    private lateinit var maxGoalInput: EditText
    private lateinit var dateInput: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_daily_goals)
        minGoalInput = findViewById(R.id.mingoals)
        maxGoalInput = findViewById(R.id.maxgoals)
        dateInput = findViewById(R.id.todayDate)
        addButton = findViewById<Button>(R.id.addgoals)

        addButton.setOnClickListener {
            val intent = Intent(this, AllCategories::class.java)
            startActivity(intent)}

    }
    }
