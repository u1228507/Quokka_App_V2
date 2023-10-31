package com.example.quokka_app.ui.patientprofiles

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quokka_app.R
import com.squareup.picasso.Picasso
import java.util.Locale

class PatientProfilesAdapter(
    private var patientprofilelist: List<PatientProfilesDataClass>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<PatientProfilesAdapter.MyViewHolder>() {

    private var filteredProfiles: List<PatientProfilesDataClass> = patientprofilelist

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
        Log.d("PatientProfilesAdapter", "Binding data for position $position")

        val patientProfile: PatientProfilesDataClass = filteredProfiles[position]
        holder.firstName.text = patientProfile.firstname
        holder.lastName.text = patientProfile.lastname
        holder.dateOfBirth.text = patientProfile.dateofbirth.toString()

        val imageUrl = patientProfile.imageUrl
        if (imageUrl?.isNotEmpty() == true) {
            Picasso.get().load(imageUrl).error(R.drawable.baseline_person_24_purple).into(holder.imageView)
        } else {
            holder.imageView.setImageResource(R.drawable.baseline_person_24_purple)
        }
    }

    override fun getItemCount(): Int {
        return filteredProfiles.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val firstName: TextView = itemView.findViewById(R.id.first_Name)
        val lastName: TextView = itemView.findViewById(R.id.last_Name)
        val dateOfBirth: TextView = itemView.findViewById(R.id.date_Of_Birth)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(filteredProfiles[position])
            }
        }
    }

    fun filter(query: String) {
        val trimmedQuery = query.trim().lowercase(Locale.getDefault())

        filteredProfiles = if (trimmedQuery.isBlank()) {
            patientprofilelist
        } else {
            // Filter profiles based on the query
            patientprofilelist.filter { profile ->
                val fullName = "${profile.firstname} ${profile.lastname}".lowercase(Locale.getDefault())
                val words = trimmedQuery.split(" ")
                words.all { word -> fullName.contains(word) }
            }
        }

        notifyDataSetChanged()
    }
    fun setProfiles(profiles: List<PatientProfilesDataClass>) {
        patientprofilelist = profiles
        notifyDataSetChanged()
    }
}
