package com.example.quokka_app.ui.patientprofilehome


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quokka_app.R
import com.example.quokka_app.databinding.FragmentChildProfileBinding
import com.google.firebase.firestore.FirebaseFirestore



class ChildProfileFragment : Fragment(R.layout.fragment_child_profile) {

    private var binding: FragmentChildProfileBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChildProfileBinding.inflate(inflater, container, false)
        val view = binding?.root
        val patientId = arguments?.getString("patientId")

        // Pulling Patient Info From Database
        if (patientId != null) {
            val db = FirebaseFirestore.getInstance()
            val generalInfoRef = db.collection("Patient Profiles")
                .document(patientId)
                .collection("Child")
                .document("Child General Info")

            generalInfoRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val data = documentSnapshot.data

                        // First Name
                        val firstName = data?.get("firstName") as? String ?: " "
                        val firstNameTextView = binding?.childpersonalinfoFirstname
                        val firstNameLabel = getString(R.string.motherprofile_motherinfo_motherfirstname, firstName)
                        firstNameTextView?.text = firstNameLabel

                        // Middle Name
                        val middleName = data?.get("middleName") as? String ?: " "
                        val middleNameTextView = binding?.childpersonalinfoMiddlename
                        val middleNameLabel = getString(R.string.motherprofile_motherinfo_mothermiddlename, middleName)
                        middleNameTextView?.text = middleNameLabel

                        // Last Name
                        val lastName = data?.get("lastName") as? String ?: " "
                        val lastNameTextView = binding?.childpersonalinfoLastname
                        val lastNameLabel = getString(R.string.motherprofile_motherinfo_motherlastname, lastName)
                        lastNameTextView?.text = lastNameLabel

                        // Date of Birth
                        val dob = data?.get("dateOfBirth") as? String ?: " "
                        val dobTextView = binding?.childpersonalinfoDob
                        val dobLabel = getString(R.string.motherprofile_motherinfo_motherdob, dob)
                        dobTextView?.text = dobLabel

                        // Sex of Child
                        val sexofchild = data?.get("sexOfChild") as String
                        val sexofchildTextView = binding?.childpersonalinfoSex
                        val sexofchildLabel = getString(R.string.childpersonalinfo_sexInput, sexofchild)
                        sexofchildTextView?.text = sexofchildLabel
                    }

                }
        }
        return view!!
    }
}