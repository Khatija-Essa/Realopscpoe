package com.example.realopsc

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.Locale

class DailyGoals : AppCompatActivity() {
    private lateinit var addButton: Button
    private lateinit var minGoalInput: EditText
    private lateinit var maxGoalInput: EditText
    private lateinit var dateInput: EditText
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_daily_goals)
        minGoalInput = findViewById(R.id.mingoals)
        maxGoalInput = findViewById(R.id.maxgoals)
        dateInput = findViewById(R.id.todayDate)
        addButton = findViewById(R.id.addgoals)

        addButton.setOnClickListener {
            val intent = Intent(this, AllCategories::class.java)
            startActivity(intent)}
        dateInput.setOnClickListener {
            showDatePicker()
        }

    }
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = String.format(
                Locale.getDefault(),
                "%02d/%02d/%04d",
                selectedDay,
                selectedMonth + 1,
                selectedYear
            )
            dateInput.setText(selectedDate)
        }, year, month, day)
        datePicker.show()
    }
    private fun saveGoalsToFirestore() {
        val minGoals = minGoalInput.text.toString().toIntOrNull()
        val maxGoals = maxGoalInput.text.toString().toIntOrNull()
        val date = dateInput.text.toString()

        if (minGoals != null && maxGoals != null && date.isNotEmpty()) {
            val goals = hashMapOf(
                "minGoals" to minGoals,
                "maxGoals" to maxGoals,
                "date" to date
            )

            db.collection("daily_goals")
                .add(goals)
                .addOnSuccessListener {
                    Toast.makeText(this, "Goals added successfully!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, AllCategories::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to add goals: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Please enter valid goals and date.", Toast.LENGTH_SHORT).show()
        }
    }
}
