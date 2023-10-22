package com.example.quokka_app.ui.patientprofiles

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
    private lateinit var searchEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        Log.d("PatientProfilesFragment", "onCreateView() called")

        _binding = FragmentPatientprofilesBinding.inflate(inflater, container, false)
        val view = binding.root
        searchEditText = binding.searchEditText

        recyclerView = binding.recyclerviewPatientprofiles
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        patientProfilesDataClassArrayList = arrayListOf()
        patientProfilesAdapter = PatientProfilesAdapter(patientProfilesDataClassArrayList, ItemClickListener())
        recyclerView.adapter = patientProfilesAdapter

        eventChangeListener()
        setupSearch()
        return view
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No need to implement anything here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                patientProfilesAdapter.filter(query)
            }

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                if (query.isEmpty()) {
                    patientProfilesAdapter.setProfiles(patientProfilesDataClassArrayList)
                }
            }
        })
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
                        Log.d("PatientProfilesFragment", "Firestore Document Added: $patientId")
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
