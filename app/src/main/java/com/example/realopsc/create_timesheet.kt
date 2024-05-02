package com.example.realopsc

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.realopsc.databinding.ActivityAddTaskBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Month
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class create_timesheet : AppCompatActivity() {

    private lateinit var addTaskTitle: EditText
    private lateinit var addTaskDescription: EditText
    private lateinit var taskDate: EditText
    private lateinit var taskTime: EditText
    private lateinit var taskendTime: EditText
    private lateinit var addTaskbtn: Button
    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0
    private var mHour: Int = 0
    private var mMinute: Int = 0
    private lateinit var timePickerDialog: TimePickerDialog
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var imgPreview: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: Taskadapter
    private val tasks = mutableListOf<model>()
    private val calendar = Calendar.getInstance()
    private lateinit var chosenImageUri: Uri
    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore
    private lateinit var model: model


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_timesheet)
        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()
        addTaskTitle = findViewById(R.id.addTaskTitle)
        addTaskDescription = findViewById(R.id.addTaskDescription)
        taskDate = findViewById(R.id.taskDate)
        taskendTime = findViewById(R.id.taskendTime)
        taskTime = findViewById(R.id.taskTime)
        addTaskbtn = findViewById(R.id.addTaskbtn)
        imgPreview = findViewById(R.id.imageView)

        imgPreview.setOnClickListener {
            pickImageFromGallery()
        }

        taskDate.setOnClickListener {
            showDatePicker()
        }

        taskTime.setOnClickListener() {

            TimePickerDialog(b = true)
        }

        taskendTime.setOnClickListener() {
            TimePickerDialog(b = false)
        }

        addTaskbtn.setOnClickListener {
            if (::chosenImageUri.isInitialized) {
                val storageRef =
                    storage.reference.child("images/${UUID.randomUUID()}") // Ensure the path is unique for each image
                storageRef.putFile(chosenImageUri).addOnSuccessListener { snapshot ->
                    // After a successful upload, get the download URL
                    snapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString() // This is the URL you want to save
                        val title = addTaskTitle.text.toString()
                        val description = addTaskDescription.text.toString()
                        val date = taskDate.text.toString()
                        val start = taskTime.text.toString()
                        val end = taskendTime.text.toString()
                        saveImageUrlInFirestore(downloadUrl, start, end, date, description, title)
                        Log.d(
                            "saveImageUrlInFirestore",
                            "Image upload successful, handle success case"
                        )
                    }
                }.addOnFailureListener { exception ->
                    Log.d("saveImageUrlInFirestore", "Image upload failed, exception: $exception")
                }
            } else {
                Log.d("image upload", "No image detected")
            }


        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_IMAGE_PICKER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE_PICKER && resultCode == RESULT_OK) {
            chosenImageUri = data?.data!!
            imgPreview.setImageURI(chosenImageUri)
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
            taskDate.setText(selectedDate)
        }, year, month, day)
        datePicker.show()
    }

    private fun TimePickerDialog(b: Boolean) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            val selectedTime =
                String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
            if (b) {
                taskTime.setText(selectedTime)

            } else {
                taskendTime.setText(selectedTime)
            }
        }, this.mHour, this.mMinute, true)
        timePickerDialog.show()
    }

    private fun saveImageUrlInFirestore(
        imageUrl: String, titles: String, descrip: String,
        date: String, stime: String, etime: String
    ) {
        val imageData = hashMapOf(
            "imageUrl" to imageUrl,
            "title" to titles,
            "description" to descrip,
            "date" to date,
            "start" to stime,
            "end" to etime
        )
        firestore.collection("users") // Replace with your collection name
            .add(imageData)
            .addOnSuccessListener { documentReference ->
                Log.d("saving to firestore", "Image URL and other data saved in Firestore")
            }
            .addOnFailureListener { exception ->
                Log.d("saving to firestore", "Image URL and other data saved in Firestore")
            }

    }

    companion object {
        private const val REQUEST_CODE_IMAGE_PICKER = 100
    }


   
}




