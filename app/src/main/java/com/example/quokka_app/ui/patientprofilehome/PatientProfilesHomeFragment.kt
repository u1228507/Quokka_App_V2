package com.example.quokka_app.ui.patientprofilehome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quokka_app.R
import com.example.quokka_app.databinding.FragmentPatientProfilesHomeBinding
import com.squareup.picasso.Picasso

class PatientProfilesHomeFragment : Fragment() {
    private var binding: FragmentPatientProfilesHomeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPatientProfilesHomeBinding.inflate(inflater, container, false)

        val firstName = arguments?.getString("firstname")
        val lastName = arguments?.getString("lastname")
        val imageUrl = arguments?.getString("imageUrl")
        val dateOfBirth = arguments?.getString("dateofbirth")

        val textViewFirstName = binding?.pphFirstname
        val textViewLastName = binding?.pphLastname
        val textViewDateOfBirth = binding?.pphDateofbirth
        val imageViewPatient = binding?.PatientProfilePicture

        if (firstName != null) {
            val firstNameLabel = getString(R.string.patientprofileshome_label_firstname, firstName)
            textViewFirstName?.text = firstNameLabel
        }
        if (lastName != null) {
            val lastNameLabel = getString(R.string.patientprofileshome_label_lastname, lastName)
            textViewLastName?.text = lastNameLabel
        }
        if (dateOfBirth != null) {
            val dateOfBirthLabel = getString(R.string.patientprofileshome_label_dateofbirth, dateOfBirth)
            textViewDateOfBirth?.text = dateOfBirthLabel
        }

        if (!imageUrl.isNullOrEmpty()) {
            Picasso.get().load(imageUrl).into(imageViewPatient)
        } else {
            imageViewPatient?.setImageResource(R.drawable.baseline_person_24_purple)
        }
        return binding?.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
