package com.example.quokka_app.ui.patientprofilehome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.quokka_app.R
import com.squareup.picasso.Picasso


class PatientProfilesHomeFragment : Fragment(R.layout.fragment_patient_profiles_home) {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_patient_profiles_home, container, false)
        val firstName = arguments?.getString("firstname")
        val lastName = arguments?.getString("lastname")
        val imageUrl = arguments?.getString("imageUrl")
        val dateofbirth = arguments?.getString("dateofbirth")

        // Find the TextView and ImageView by their IDs
        val textViewFirstName = view.findViewById<TextView>(R.id.pph_firstname)
        val textViewLastName = view.findViewById<TextView>(R.id.pph_lastname)
        val textViewDateOfBirth = view.findViewById<TextView>(R.id.pph_dateofbirth)
        val imageViewPatient = view.findViewById<ImageView>(R.id.PatientProfilePicture)

        // Display the patientName in the TextView
        if (firstName != null) {
            textViewFirstName.text = "First Name: $firstName"
        }
        if (lastName != null) {
            textViewLastName.text = "Last Name: $lastName"
        }
        if (dateofbirth != null) {
            textViewDateOfBirth.text = "Date of Birth: $dateofbirth"
        }
        if (imageUrl != null) {
            Picasso.get().load(imageUrl).into(imageViewPatient)
        }

        return view
    }
}