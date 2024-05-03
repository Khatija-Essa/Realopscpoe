package com.example.realopsc

import android.content.Intent
import android.graphics.ColorSpace.Model
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Tasks
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class Add_Task : AppCompatActivity() {
    private lateinit var taskRecycler: RecyclerView
    private lateinit var addTask: Button
    private lateinit var taskAdapter: Taskadapter
    private lateinit var noDataImage: ImageView
    private lateinit var calendar: ImageView
    private lateinit var imageView: ImageView
    private lateinit var button: FloatingActionButton
    private val db = FirebaseFirestore.getInstance()
    private val models = mutableListOf<model>()
    lateinit var bottomNav: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_task)
        taskRecycler = findViewById(R.id.taskRecycler)
        taskAdapter = Taskadapter(models)
        taskRecycler.adapter = taskAdapter
        val layoutManager = LinearLayoutManager(this)
        taskRecycler.layoutManager = layoutManager

        val addTask = findViewById<Button>(R.id.addTask)
        addTask.setOnClickListener {
            val intent = Intent(this, create_timesheet::class.java)
            startActivity(intent)
            fetchTask()
        }

        val dailyGoals = findViewById<Button>(R.id.dailyGoals)
        dailyGoals.setOnClickListener {
            val intent = Intent(this, DailyGoals::class.java)
            startActivity(intent)
        }


    }

    private fun fetchTask() {
        db.collection("users")
            .get()
            .addOnSuccessListener { resuts ->
                for (document in resuts.documents) {
                    val Model = document.toObject(model::class.java)!!
                    models.add(Model)
                }
                taskAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->

            }

    }

}
