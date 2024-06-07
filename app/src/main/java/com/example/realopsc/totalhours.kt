package com.example.realopsc

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class totalhours : AppCompatActivity() {
    var id: Int? = null
    var punchInTime: Time? = null
    var punchOutTime: Time? = null
    private val db = FirebaseFirestore.getInstance()

    private lateinit var totalHoursTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_totalhours)
        val btn1 = findViewById<Button>(R.id.closeButton)
        btn1.setOnClickListener {
            val intent = Intent(this, AllCategories::class.java)
            startActivity(intent)
        }

        fetchPunchTimesFromFirestore()
    }

    private fun fetchPunchTimesFromFirestore() {
        val documentId = "your_document_id_here"
        db.collection("punch_times").document(documentId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    punchInTime = document.getDate("punchInTime") as Time?
                    punchOutTime = document.getDate("punchOutTime") as Time?
                    calculateAndDisplayTotalHours()
                } else {
                    Toast.makeText(this, "No such document!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun savePunchTimesToFirestore() {
        if (punchInTime != null && punchOutTime != null) {
            val punchTimes = hashMapOf(
                "punchInTime" to punchInTime.toString(),
                "punchOutTime" to punchOutTime.toString()
            )

            db.collection("punch_times")
                .add(punchTimes)
                .addOnSuccessListener {
                    Toast.makeText(this, "Times added successfully!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, AllCategories::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to add times: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
        } else {
            Toast.makeText(this, "Punch-in and Punch-out times are required.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun calculateAndDisplayTotalHours() {
        if (punchInTime != null && punchOutTime != null) {
            val duration = punchOutTime!!.time - punchInTime!!.time
            val hours = (duration / (1000 * 60 * 60)).toInt()
            val minutes = (duration / (1000 * 60)) % 60
            totalHoursTextView.text = getString(R.string.total_hours_format, hours, minutes)
        } else {
            totalHoursTextView.text = getString(R.string.invalid_punch_times)
        }
    }

}
