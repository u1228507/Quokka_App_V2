package com.example.quokka_app.ui.patientprofiles

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quokka_app.R
import com.squareup.picasso.Picasso

class PatientProfilesAdapter(
    private val patientprofilelist: ArrayList<PatientProfilesDataClass>,
    private val listener: OnItemClickListener // Add the listener parameter
) : RecyclerView.Adapter<PatientProfilesAdapter.MyViewHolder>() {

    // Define the listener interface
    interface OnItemClickListener {
        fun onItemClick(patientProfile: PatientProfilesDataClass)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.layout_patientprofiles_cardview,
            parent, false
        )
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val patientProfile: PatientProfilesDataClass = patientprofilelist[position]
        val item = patientprofilelist[position]
        holder.firstName.text = patientProfile.firstname
        holder.lastName.text = patientProfile.lastname
        holder.dateOfBirth.text = patientProfile.dateofbirth.toString()

        val imageUrl = item.imageUrl.toString()
        if (imageUrl.isNotEmpty()) {
            Picasso.get().load(imageUrl).error(R.drawable.baseline_person_24_purple).into(holder.imageView)
        } else {
            holder.imageView.setImageResource(R.drawable.baseline_person_24_purple)
        }
    }

    override fun getItemCount(): Int {
        return patientprofilelist.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val firstName: TextView = itemView.findViewById(R.id.first_Name)
        val lastName: TextView = itemView.findViewById(R.id.last_Name)
        val dateOfBirth: TextView = itemView.findViewById(R.id.date_Of_Birth)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)

        init {
            itemView.setOnClickListener(this) // Set a click listener on the itemView
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(patientprofilelist[position])
            }
        }
    }
}
