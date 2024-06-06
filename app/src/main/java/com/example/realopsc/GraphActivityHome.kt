package com.example.realopsc

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class GraphActivityHome: AppCompatActivity() {


    private lateinit var firestore: FirebaseFirestore
    private lateinit var submitData: Button
    private lateinit var showGraph: Button
    private lateinit var inputData: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_graph_home)

        firestore = FirebaseFirestore.getInstance()
        submitData = findViewById(R.id.submitData)
        showGraph = findViewById(R.id.showGraph)
        inputData = findViewById(R.id.inputData)

        submitData.setOnClickListener {
            val dataString = inputData.text.toString()
            try {
                val data = dataString.toDouble()
                firestore.collection("data").add(mapOf("value" to data))
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
            }
        }

        showGraph.setOnClickListener {
            startActivity(Intent(this, GraphActivity::class.java))
        }
    }
}