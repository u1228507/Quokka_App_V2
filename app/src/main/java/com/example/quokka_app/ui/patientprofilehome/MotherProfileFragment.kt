package com.example.quokka_app.ui.patientprofilehome

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.quokka_app.R
import com.example.quokka_app.databinding.FragmentMotherProfileBinding
import com.google.firebase.firestore.FirebaseFirestore

class MotherProfileFragment : Fragment(R.layout.fragment_mother_profile) {
    private lateinit var binding: FragmentMotherProfileBinding
    private val recordChildsBirthFragment = RecordChildBirthFragment()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMotherProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        val patientId = arguments?.getString("patientId") ?: ""

        val args = Bundle()
        args.putString("patientId", patientId)
        recordChildsBirthFragment.arguments = args

        val recordChildsBirthButton = binding.motherprofileButtonRecordchildsbirth

        if (patientId.isNotBlank()) {
            isChildGeneralInfoExist(patientId)
        }

        recordChildsBirthButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.patientprofilehome_fragmentContainer, recordChildsBirthFragment)
                .addToBackStack(null)
                .commit()
        }


        return view
    }

    private fun isChildGeneralInfoExist(patientId: String) {
        val db = FirebaseFirestore.getInstance()
        val childGeneralInfoRef = db.collection("Patient Profiles")
            .document(patientId)
            .collection("Child")
            .document("Child General Info")

        childGeneralInfoRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    val isChildInfoExists = document != null && document.exists()

                    if (isChildInfoExists) {
                        binding.motherprofileButtonRecordchildsbirth.visibility = View.GONE
                    } else {
                        binding.motherprofileButtonRecordchildsbirth.visibility = View.VISIBLE
                    }
                } else {
                    Log.e("MotherProfileFragment", "Error checking Child General Info", task.exception)
                }
            }
    }

}