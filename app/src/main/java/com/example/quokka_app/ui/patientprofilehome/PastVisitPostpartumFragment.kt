package com.example.quokka_app.ui.patientprofilehome

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quokka_app.R
import com.example.quokka_app.ui.pastvisits.OnItemClickListener
import com.example.quokka_app.ui.pastvisits.PastVisitsAdapter
import com.example.quokka_app.ui.pastvisits.PastVisitsPostpartumDataClass
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class PastVisitPostpartumFragment : Fragment(), OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var pastVisitsAdapter: PastVisitsAdapter
    private val postpartumVisitList: MutableList<PastVisitsPostpartumDataClass> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_past_visit_postpartum, container, false)
        recyclerView = view.findViewById(R.id.pastvisits_recyclerView_postpartum)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        pastVisitsAdapter = PastVisitsAdapter(postpartumVisitList, this)
        recyclerView.adapter = pastVisitsAdapter

        val patientId = arguments?.getString("patientId") ?: ""

        if (patientId.isNotEmpty()) {
            fetchPostpartumVisitsFromFirestore(patientId) { fetchedPostpartumVisitList ->
                postpartumVisitList.clear()
                postpartumVisitList.addAll(fetchedPostpartumVisitList)
                pastVisitsAdapter.notifyDataSetChanged()
            }
        } else {
            Log.e("TestLog", "Patient ID is empty or null")
        }

        return view
    }

    private fun fetchPostpartumVisitsFromFirestore(
        patientId: String,
        callback: (List<PastVisitsPostpartumDataClass>) -> Unit
    ) {
        val firestore = FirebaseFirestore.getInstance()
        val patientProfilesCollection = firestore.collection("Patient Profiles")
        val patientDocument = patientProfilesCollection.document(patientId)
        val postpartumVisitsCollection = patientDocument.collection("Visits")

        postpartumVisitsCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val postpartumVisitList = mutableListOf<PastVisitsPostpartumDataClass>()
                val dateFormat = SimpleDateFormat("ddMMyyyyHHmm", Locale.getDefault())

                for (document in querySnapshot.documents) {
                    val date = document.getString("date")
                    if (date != null && document.id.startsWith("Postpartum Visit:")) {
                        val parsedDate = dateFormat.parse(date)
                        val postpartumVisit = PastVisitsPostpartumDataClass(parsedDate, document.id)
                        postpartumVisitList.add(postpartumVisit)
                    } else {
                        Log.d("TestLog", "Skipped document with id: ${document.id}, date: $date")
                    }
                }
                if (postpartumVisitList.isEmpty()) {
                    Log.d("Error", "Issue")

                }
                callback(postpartumVisitList)
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Failed to fetch Postpartum Visits: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                callback(emptyList())
            }
    }

    override fun onButtonClick(position: Int) {
        val navController = findNavController()
        val bundle = Bundle().apply{
            putString("patientId", arguments?.getString("patientId"))
            putString("visitId", postpartumVisitList[position].documentId)
        }
        navController.navigate(R.id.action_pastVisitPostpartumFragment_to_pastPostpartumVisitsDetailsFragment, bundle)
    }
}
