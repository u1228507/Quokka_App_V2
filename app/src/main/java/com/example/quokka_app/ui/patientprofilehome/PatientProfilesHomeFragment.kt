package com.example.quokka_app.ui.patientprofilehome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.quokka_app.R
import com.example.quokka_app.databinding.FragmentPatientProfilesHomeBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class PatientProfilesHomeFragment : Fragment() {
    private var binding: FragmentPatientProfilesHomeBinding? = null
    private val initialMotherProfileFragment = MotherProfileFragment()
    private val args = Bundle()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPatientProfilesHomeBinding.inflate(inflater, container, false)

        val firstName = arguments?.getString("firstname")
        val lastName = arguments?.getString("lastname")
        val imageUrl = arguments?.getString("imageUrl")
        val dateOfBirth = arguments?.getString("dateofbirth")

        val patientId = arguments?.getString("patientId") ?: ""
        args.putString("patientId", patientId)
        initialMotherProfileFragment.arguments = args

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
        childFragmentManager.beginTransaction()
            .replace(R.id.patientprofilehome_fragmentContainer, initialMotherProfileFragment)
            .commit()

// Profile Buttons:
        val motherButton = binding?.patientprofilehomeButtonMotherprofile
        val childButton = binding?.patientprofilehomeButtonChildprofile

        motherButton?.setOnClickListener {
            args.putString("patientId", patientId)
            replaceFragment(MotherProfileFragment(),args)
            val db = FirebaseFirestore.getInstance()
            val generalInfoRef = db.collection("Patient Profiles")
                .document(patientId)
                .collection("Patient Profile Information")
                .document("General Info")
            generalInfoRef.get().addOnSuccessListener { motherinformation ->
                if (motherinformation.exists()){
                    val motherfirstName = arguments?.getString("firstname")
                    val motherlastName = arguments?.getString("lastname")
                    val motherimageUrl = arguments?.getString("imageUrl")
                    val motherdateOfBirth = arguments?.getString("dateofbirth")
                    if (motherfirstName != null) {
                        val firstNameLabel = getString(R.string.patientprofileshome_label_firstname, motherfirstName)
                        textViewFirstName?.text = firstNameLabel
                    }
                    if (motherlastName != null) {
                        val lastNameLabel = getString(R.string.patientprofileshome_label_lastname, motherlastName)
                        textViewLastName?.text = lastNameLabel
                    }
                    if (motherdateOfBirth != null) {
                        val dateOfBirthLabel = getString(R.string.patientprofileshome_label_dateofbirth, motherdateOfBirth)
                        textViewDateOfBirth?.text = dateOfBirthLabel
                    }

                    if (!motherimageUrl.isNullOrEmpty()) {
                        Picasso.get().load(motherimageUrl).into(imageViewPatient)
                    } else {
                        imageViewPatient?.setImageResource(R.drawable.baseline_person_24_purple)
                    }
                }
            }

        }
        childButton?.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val childGeneralInfoRef = db.collection("Patient Profiles")
                .document(patientId)
                .collection("Child")
                .document("Child General Info")

            childGeneralInfoRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val childInfo = documentSnapshot.data
                        val childfirstName = childInfo?.get("firstName") as? String
                        val childlastName = childInfo?.get("lastName") as? String
                        val childdateOfBirth = childInfo?.get("dateOfBirth") as? String
                        val sexOfChild = childInfo?.get("sexOfChild") as? String

                        if (childfirstName != null) {
                            val childFirstNameLabel = getString(R.string.patientprofileshome_label_firstname, childfirstName)
                            textViewFirstName?.text = childFirstNameLabel
                        } else {
                            textViewFirstName?.text = getString(R.string.patientprofilehome_label_firstname_1)
                        }

                        if (childlastName != null) {
                            val childlastNameLabel = getString(R.string.patientprofileshome_label_lastname, childlastName)
                            textViewLastName?.text = childlastNameLabel
                        } else {
                            textViewLastName?.text = getString(R.string.patientprofilehome_label_lastname_1)
                        }

                        if (childdateOfBirth != null) {
                            binding?.pphDateofbirth?.text = childdateOfBirth
                        } else {
                            binding?.pphDateofbirth?.text = getString(R.string.patientprofilehome_label_dob_1)
                        }

                        if (sexOfChild != null) {
                            when (sexOfChild) {
                                "Male" -> {
                                    imageViewPatient?.setImageResource(R.drawable.babyfeet_blue)
                                }
                                "Female" -> {
                                    imageViewPatient?.setImageResource(R.drawable.babyfeet_pink)
                                }
                                else -> {
                                    imageViewPatient?.setImageResource(R.drawable.babyfeet_yellow)
                                }
                            }
                        }
                    } else {
                        textViewFirstName?.text = getString(R.string.patientprofilehome_label_firstname_1)
                        textViewLastName?.text = getString(R.string.patientprofilehome_label_lastname_1)
                        binding?.pphDateofbirth?.text = getString(R.string.patientprofilehome_label_dob_1)
                        imageViewPatient?.setImageResource(R.drawable.babyfeet_yellow)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        requireContext(),
                        "Failed to retrieve child information: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            replaceFragment(ChildProfileFragment(), args)
        }



        return binding?.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun replaceFragment(fragment: Fragment, args: Bundle? = null) {
        fragment.arguments = args
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.patientprofilehome_fragmentContainer, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


}

