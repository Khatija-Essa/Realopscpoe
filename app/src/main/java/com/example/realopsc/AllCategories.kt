package com.example.realopsc

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.realopsc.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AllCategories : AppCompatActivity() {


    private val activityScope = CoroutineScope(Job() + Dispatchers.Main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_categories)

        val btn1  = findViewById<Button>(R.id.add_task_button1)
        btn1.setOnClickListener {
            val intent = Intent(this, Add_Task::class.java)
            startActivity(intent)}

        val btn2  = findViewById<Button>(R.id.add_task_button2)
        btn2.setOnClickListener {
            val intent = Intent(this, Add_Task::class.java)
            startActivity(intent)}

        val btn3  = findViewById<Button>(R.id.add_task_button3)
        btn3.setOnClickListener {
            val intent = Intent(this, Add_Task::class.java)
            startActivity(intent)}

        val btn4  = findViewById<Button>(R.id.add_task_button4)
        btn4.setOnClickListener {
            val intent = Intent(this, Add_Task::class.java)
            startActivity(intent)}

        val btn5  = findViewById<Button>(R.id.total_hours_button1)
        btn5.setOnClickListener {
            val intent = Intent(this, totalhours::class.java)
            startActivity(intent)}

        val btn6  = findViewById<Button>(R.id.total_hours_button2)
        btn6.setOnClickListener {
            val intent = Intent(this, totalhours::class.java)
            startActivity(intent)}

        val btn7  = findViewById<Button>(R.id.total_hours_button3)
        btn7.setOnClickListener {
            val intent = Intent(this, totalhours::class.java)
            startActivity(intent)}

        val btn8  = findViewById<Button>(R.id.total_hours_button4)
        btn8.setOnClickListener {
            val intent = Intent(this, totalhours::class.java)
            startActivity(intent)}

        val btn9  = findViewById<ImageButton>(R.id.imageButton)
        btn9.setOnClickListener {
            val intent = Intent(this, PomodoroActivity::class.java)
            startActivity(intent)}

     }
}
