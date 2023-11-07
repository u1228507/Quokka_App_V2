package com.example.quokka_app.ui.patientprofilehome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quokka_app.R
import com.example.quokka_app.databinding.FragmentMotherPersonalInfoBinding
import com.google.firebase.firestore.FirebaseFirestore


class MotherPersonalInfoFragment : Fragment(R.layout.fragment_mother_personal_info) {
    private var binding: FragmentMotherPersonalInfoBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMotherPersonalInfoBinding.inflate(inflater, container, false)
        val view = binding?.root
        val patientId = arguments?.getString("patientId")

        // Pulling Patient Info From Database
        if (patientId != null) {
            val db = FirebaseFirestore.getInstance()
            val generalInfoRef = db.collection("Patient Profiles")
                .document(patientId)
                .collection("Patient Profile Information")
                .document("General Info")

            generalInfoRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val data = documentSnapshot.data

                        // First Name
                        val firstName = data?.get("firstname") as? String ?: " "
                        val firstNameTextView = binding?.motherprofileMotherinfoMotherfirstname2
                        val firstNameLabel = getString(R.string.motherprofile_motherinfo_motherfirstname, firstName)
                        firstNameTextView?.text = firstNameLabel

                        // Middle Name
                        val middleName = data?.get("middlename") as? String ?: " "
                        val middleNameTextView = binding?.motherprofileMotherinfoMothermiddlename2
                        val middleNameLabel = getString(R.string.motherprofile_motherinfo_mothermiddlename, middleName)
                        middleNameTextView?.text = middleNameLabel

                        // Last Name
                        val lastName = data?.get("lastname") as? String ?: " "
                        val lastNameTextView = binding?.motherprofileMotherinfoMotherlastname2
                        val lastNameLabel = getString(R.string.motherprofile_motherinfo_motherlastname, lastName)
                        lastNameTextView?.text = lastNameLabel

                        // Date of Birth
                        val dob = data?.get("dateofbirth") as? String ?: " "
                        val dobTextView = binding?.motherprofileMotherinfoMotherdob2
                        val dobLabel = getString(R.string.motherprofile_motherinfo_motherdob, dob)
                        dobTextView?.text = dobLabel

                        // Village
                        val mothervillage = data?.get("mothersvillage") as? String ?: " "
                        val mothervillageTextView = binding?.motherprofileMotherinfoVillage2
                        val mothervillageLabel = getString(R.string.motherprofile_motherinfo_village, mothervillage)
                        mothervillageTextView?.text = mothervillageLabel

                        // Mother Phone Number
                        val motherphone = data?.get("mothersphonenumber") as? String ?: " "
                        val motherphoneTextView = binding?.motherprofileMotherinfoMotherphonenumber2
                        val motherphoneLabel = getString(R.string.motherprofile_motherinfo_motherphonenumber, motherphone)
                        motherphoneTextView?.text = motherphoneLabel

                        // Father's First Name
                        val fatherfirst = data?.get("fathersfirstname") as? String ?: " "
                        val fatherfirstTextView = binding?.motherprofileMotherinfoFatherfirstname2
                        val fatherfirstLabel = getString(R.string.motherprofile_motherinfo_fatherfirstname, fatherfirst)
                        fatherfirstTextView?.text = fatherfirstLabel

                        // Father's Middle Name
                        val fathermiddle = data?.get("fathersmiddlename") as? String ?: " "
                        val fathermiddleTextView = binding?.motherprofileMotherinfoFathermiddlename2
                        val fathermiddleLabel = getString(R.string.motherprofile_motherinfo_fathermiddlename, fathermiddle)
                        fathermiddleTextView?.text = fathermiddleLabel

                        // Fathers Last Name
                        val fatherlast = data?.get("fatherslastname") as? String ?: " "
                        val fatherlastTextView = binding?.motherprofileMotherinfoFatherlastname2
                        val fatherlastLabel = getString(R.string.motherprofile_motherinfo_fatherlastname, fatherlast)
                        fatherlastTextView?.text = fatherlastLabel

                        // Fathers Last Name
                        val fathervillage = data?.get("fathersvillage") as? String ?: " "
                        val fathervillageTextView = binding?.motherprofileMotherinfoFathervillage2
                        val fathervillageLabel = getString(R.string.motherprofile_motherinfo_fathervillage, fathervillage)
                        fathervillageTextView?.text = fathervillageLabel

                        // Father's Phone Number
                        val fatherphone = data?.get("fathersphonenumber") as? String ?: " "
                        val fatherphoneTextView = binding?.motherprofileMotherinfoFatherphonenumber2
                        val fatherphoneLabel = getString(R.string.motherprofile_motherinfo_fatherphonenumber, fatherphone)
                        fatherphoneTextView?.text = fatherphoneLabel

                        // FCHW's First Name
                        val fchwfirst = data?.get("fchwfirstname") as? String ?: " "
                        val fchwfirstTextView = binding?.motherprofileMotherinfoFchwfirstname2
                        val fchwfirstLabel = getString(R.string.motherprofile_motherinfo_fchwfirstname, fchwfirst)
                        fchwfirstTextView?.text = fchwfirstLabel

                        // FCHW's Last Name
                        val fchwlast = data?.get("fchwlastname") as? String ?: " "
                        val fchwlastTextView = binding?.motherprofileMotherinfoFchwlastname2
                        val fchwlastLabel = getString(R.string.motherprofile_motherinfo_fchwlastname, fchwlast)
                        fchwlastTextView?.text = fchwlastLabel

                        // FCHW's Phone Number
                        val fchwphone = data?.get("fchwphonenumber") as? String ?: " "
                        val fchwphoneTextView = binding?.motherprofileMotherinfoFchwphonenumber2
                        val fchwphoneLabel = getString(R.string.motherprofile_motherinfo_fchwphonenumber, fchwphone)
                        fchwphoneTextView?.text = fchwphoneLabel

                    }

                }
        }


        return view
    }
}