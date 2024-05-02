package com.example.realopsc

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage

class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
    private val title: TextView = itemView.findViewById(R.id.mtitle)
    private val description: TextView = itemView.findViewById(R.id.mdescription)
    private val imageView3: ImageView = itemView.findViewById(R.id.imageView3)
    private val date: TextView = itemView.findViewById(R.id.mdate)
    private val stime: TextView = itemView.findViewById(R.id.mstarttime)
    private val etime: TextView = itemView.findViewById(R.id.mendtime)

    fun bind(model: model) {
        Log.d("FirebaseUrl","URL:${model.imageUrl}")
        val storageReference =
            FirebaseStorage.getInstance().getReferenceFromUrl(model.imageUrl)

        storageReference.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(itemView.context)
                .load(model.imageUrl + ".jpeg")
                .into(imageView3)
            title.text ="${model.title}"
            description.text ="${model.description}"
            date.text ="${model.date}"
            stime.text="${model.start}"
            etime.text = "${model.end}"
        }

    }

}
