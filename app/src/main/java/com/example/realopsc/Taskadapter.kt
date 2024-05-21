package com.example.realopsc

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage

class Taskadapter(private var userList: List<model>) : RecyclerView.Adapter<Taskadapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.mtitle)
        private val description: TextView = itemView.findViewById(R.id.mdescription)
        private val imageUrl: ImageView = itemView.findViewById(R.id.imageView3)
        private val date: TextView = itemView.findViewById(R.id.mdate)
        private val start: TextView = itemView.findViewById(R.id.mstarttime)
        private val end: TextView = itemView.findViewById(R.id.mendtime)

        fun bind(model: model) {
            Log.d("FirebaseUrl", "URL: ${model.imageUrl}")

            // Directly load the image URL into the ImageView using Glide
            Glide.with(itemView.context)
                .load(model.imageUrl)
                .into(imageUrl)

            title.text = model.title
            description.text = model.description
            date.text = model.date
            start.text = model.start
            end.text = model.end
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = userList[position]
        holder.bind(model)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun updateData(newList: List<model>) {
        userList = newList
        notifyDataSetChanged()
    }
}
