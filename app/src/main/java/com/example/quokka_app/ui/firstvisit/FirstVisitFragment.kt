package com.example.quokka_app.ui.firstvisit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.quokka_app.R
import com.example.quokka_app.databinding.FragmentFirstVisitBinding
import com.google.firebase.firestore.FirebaseFirestore

class FirstVisitFragment : Fragment(R.layout.fragment_first_visit) {
    private var _binding: FragmentFirstVisitBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstVisitBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.firstvisitButtonSavefirstvisit.setOnClickListener {
            val dateOfVisit = binding.texteditFirstvisitDateofvisit.text.toString()
            val height = binding.texteditFirstvisitHeight.text.toString()
            val weight = binding.texteditFirstvisitWeight.text.toString()
            val temperature = binding.texteditFirstvisitTemperature.text.toString()
            val systolic = binding.texteditFirstvisitSystolic.text.toString()
            val diastolic = binding.texteditFirstvisitDiastolic.text.toString()

            val firstVisitData = FirstVisitDataClass(
                dateofvisit = dateOfVisit,
                height = height,
                weight = weight,
                temperature = temperature,
                bloodpressure = "$systolic/$diastolic"
            )

            val firestore = FirebaseFirestore.getInstance()
            val patientId = arguments?.getString("patientId")

            if (patientId != null) {
                val patientProfilesCollection = firestore.collection("Patient Profiles")
                val patientDocument = patientProfilesCollection.document(patientId)
                val visitsCollection = patientDocument.collection("Visits")
                val visitDocument = visitsCollection.document("Visit 1") // Change "Visit 1" to an appropriate name or handle multiple visits

                visitDocument.set(firstVisitData)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "First Visit saved successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Failed to save first visit: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
        return root
    }
}


