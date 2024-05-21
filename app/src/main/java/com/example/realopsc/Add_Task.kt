package com.example.realopsc

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.firestore.FirebaseFirestore

class Add_Task : AppCompatActivity() {
    private lateinit var taskRecycler: RecyclerView
    private lateinit var addTask: Button
    private lateinit var taskAdapter: Taskadapter
    private lateinit var noDataImage: ImageView
    private lateinit var calendar: ImageView
    private lateinit var imageView: ImageView
    private lateinit var button: FloatingActionButton
    private val db = FirebaseFirestore.getInstance()
    private val itemList = mutableListOf<model>()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_task)
        taskRecycler = findViewById(R.id.taskRecycler)
        taskAdapter = Taskadapter(itemList)
        taskRecycler.adapter = taskAdapter
        val layoutManager = LinearLayoutManager(this)
        taskRecycler.layoutManager = layoutManager

        loadProfileData()
        val addTask = findViewById<Button>(R.id.addTask)
        addTask.setOnClickListener {
            fetchTask()
            val intent = Intent(this, create_timesheet::class.java)
            startActivity(intent)

        }

        val dailyGoals = findViewById<Button>(R.id.dailyGoals)
        dailyGoals.setOnClickListener {
            val intent = Intent(this, DailyGoals::class.java)
            startActivity(intent)
        }


    }

    private fun fetchTask() {
        db.collection("models")
            .get()
            .addOnSuccessListener { resuts ->
                for (document in resuts.documents) {
                    val Model = document.toObject(model::class.java)!!
                    itemList.add(Model)
                }
                taskAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->

            }

    }
    private fun loadProfileData() {
        db.collection("models").get().addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                for (document in documents) {
                    if (document.exists()) {
                        val tit = document.getString("title")
                        val tdescrip = document.getString("description")
                        val tdate = document.getString("date")
                        val startt = document.getString("start")
                        val endt = document.getString("end")
                        val imgt = document.getString("imageUrl")

                        val models = model(tit, tdescrip, tdate, startt, endt, imgt)
                        itemList.add(models)
                    }
                }
                taskAdapter.notifyDataSetChanged()
            }
        }
    }
}