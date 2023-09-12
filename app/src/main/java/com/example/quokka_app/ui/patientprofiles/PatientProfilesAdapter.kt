package com.example.quokka_app.ui.patientprofiles

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quokka_app.R

class PatientProfilesAdapter(private val patientprofilelist: ArrayList<PatientProfilesDataClass>) :
    RecyclerView.Adapter<PatientProfilesAdapter.MyViewHolder>() {

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
        holder.firstName.text = patientProfile.first_Name
        holder.lastName.text = patientProfile.last_Name
        holder.dateOfBirth.text = patientProfile.date_Of_Birth.toString()
    }

    override fun getItemCount(): Int {
        return patientprofilelist.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val firstName: TextView = itemView.findViewById(R.id.first_Name)
        val lastName: TextView = itemView.findViewById(R.id.last_Name)
        val dateOfBirth: TextView = itemView.findViewById(R.id.date_Of_Birth)
    }
}

