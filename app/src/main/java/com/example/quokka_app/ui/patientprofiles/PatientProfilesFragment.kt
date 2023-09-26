package com.example.quokka_app.ui.patientprofiles

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quokka_app.databinding.FragmentPatientprofilesBinding
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPatientprofilesBinding.inflate(inflater, container, false)
        val view = binding.root

        recyclerView = binding.recyclerviewPatientprofiles
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        patientProfilesDataClassArrayList = arrayListOf()
        patientProfilesAdapter = PatientProfilesAdapter(patientProfilesDataClassArrayList)
        recyclerView.adapter = patientProfilesAdapter

        EventChangeListener()

        return view
    }

    private fun EventChangeListener() {
        db = FirebaseFirestore.getInstance()
        db.collection("Patient Profiles")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("Firestore Error", error.message.toString())
                    return@addSnapshotListener
                }
                val sortedList = mutableListOf<PatientProfilesDataClass>()
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        // Fetch the image URL from Firestore and set it in PatientProfilesDataClass
                        val patientProfile = dc.document.toObject(PatientProfilesDataClass::class.java)
                        patientProfile.imageUrl = dc.document.getString("imageUrl")
                        sortedList.add(patientProfile)
                    }
                }
                sortedList.sortBy { it.lastname }
                val startInsertPosition = patientProfilesDataClassArrayList.size
                patientProfilesDataClassArrayList.addAll(sortedList)
                patientProfilesAdapter.notifyItemRangeInserted(startInsertPosition, sortedList.size)
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

