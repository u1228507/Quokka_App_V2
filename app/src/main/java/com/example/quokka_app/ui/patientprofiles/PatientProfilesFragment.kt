package com.example.quokka_app.ui.patientprofiles

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quokka_app.R
import com.example.quokka_app.databinding.FragmentPatientprofilesBinding
import com.example.quokka_app.ui.patientprofilehome.PatientProfilesHomeFragment
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class PatientProfilesFragment : Fragment() {
    private var _binding: FragmentPatientprofilesBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var patientProfilesDataClassArrayList: ArrayList<PatientProfilesDataClass>
    private lateinit var patientProfilesAdapter: PatientProfilesAdapter
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPatientprofilesBinding.inflate(inflater, container, false)
        val view = binding.root

        recyclerView = binding.recyclerviewPatientprofiles
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        patientProfilesDataClassArrayList = arrayListOf()
        patientProfilesAdapter = PatientProfilesAdapter(patientProfilesDataClassArrayList, ItemClickListener())
        recyclerView.adapter = patientProfilesAdapter

        eventChangeListener()
        return view
    }

    private fun eventChangeListener() {
        db = FirebaseFirestore.getInstance()
        db.collection("Patient Profiles")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                val totalDocs = value!!.documentChanges.size
                var processedDocs = 0
                val sortedList = mutableListOf<PatientProfilesDataClass>() // ...

                for (dc: DocumentChange in value.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val patientId = dc.document.id
                        Log.d("PatientId", "Patient ID: $patientId") // Log the patient ID
                        val generalInfoRef = db.collection("Patient Profiles")
                            .document(patientId)
                            .collection("Patient Profile Information")
                            .document("General Info")

                        generalInfoRef.get().addOnSuccessListener { generalInfoSnapshot ->
                            val firstname = generalInfoSnapshot.getString("firstname")
                            val lastname = generalInfoSnapshot.getString("lastname")
                            val dateofbirth = generalInfoSnapshot.getString("dateofbirth")
                            val imageUrl = generalInfoSnapshot.getString("imageUrl")

                            val patientProfile = PatientProfilesDataClass(
                                firstname ?: "",
                                lastname ?: "",
                                dateofbirth ?: "",
                                imageUrl ?: "",
                                patientId
                            )
                            sortedList.add(patientProfile)

                            // Check if all documents have been processed
                            if (++processedDocs == totalDocs) {
                                sortedList.sortBy { it.lastname }
                                patientProfilesDataClassArrayList.clear()
                                patientProfilesDataClassArrayList.addAll(sortedList)
                                patientProfilesAdapter.notifyDataSetChanged()
                            }
                        }
                            .addOnFailureListener { e ->
                                Log.e("Firestore Error", e.message.toString())

                                // Ensure the counter is incremented even if there's an error
                                if (++processedDocs == totalDocs) {
                                    sortedList.sortBy { it.lastname }
                                    patientProfilesDataClassArrayList.clear()
                                    patientProfilesDataClassArrayList.addAll(sortedList)
                                    patientProfilesAdapter.notifyDataSetChanged()
                                }
                            }
                    }
                }
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class ItemClickListener : PatientProfilesAdapter.OnItemClickListener {
        override fun onItemClick(patientProfile: PatientProfilesDataClass) {
            val bundle = Bundle()
            bundle.putString("firstname", patientProfile.firstname)
            bundle.putString("lastname", patientProfile.lastname)
            bundle.putString("dateofbirth", patientProfile.dateofbirth)
            bundle.putString("imageUrl", patientProfile.imageUrl)

            val patientProfilesHomeFragment = PatientProfilesHomeFragment()
            patientProfilesHomeFragment.arguments = bundle

            findNavController().navigate(R.id.action_patientProfilesFragment_to_patientProfilesHomeFragment, bundle)
        }
    }
}
