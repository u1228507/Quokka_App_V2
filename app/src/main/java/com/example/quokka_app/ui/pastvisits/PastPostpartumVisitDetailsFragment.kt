package com.example.quokka_app.ui.pastvisits

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.quokka_app.R
import com.google.firebase.firestore.FirebaseFirestore


class PastPostpartumVisitDetailsFragment : Fragment(R.layout.fragment_past_postpartum_visit_details) {
    private lateinit var textViewVisitDetails: TextView
    private lateinit var textViewVisitAlerts: TextView
    private lateinit var textViewChildVisitDetails: TextView
    private lateinit var textViewVisitChildAlerts: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.fragment_past_postpartum_visit_details, container, false)

        // Initialize UI elements
        textViewVisitDetails = view.findViewById(R.id.textViewVisitDetails)
        textViewVisitAlerts = view.findViewById(R.id.textViewVisitAlerts)
        textViewChildVisitDetails = view.findViewById(R.id.textViewChildVisitDetails)
        textViewVisitChildAlerts = view.findViewById(R.id.textViewVisitChildAlerts)

        // Get data from arguments
        val patientId = arguments?.getString("patientId") ?: ""
        val visitId = arguments?.getString("visitId") ?: ""

        // Fetch and display visit details
        if (patientId.isNotEmpty() && visitId.isNotEmpty()) {
            fetchPostpartumVisitDetailsFromFirestore(patientId, visitId)
            fetchAlertsFromFirestore(patientId, visitId)
            fetchChildVisitDetailsFromFirestore(patientId, visitId)
            fetchChildAlertsFromFirestore(patientId, visitId)
        }

        return view
    }

    private fun fetchPostpartumVisitDetailsFromFirestore(patientId: String, visitId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val patientProfilesCollection = firestore.collection("Patient Profiles")
        val patientDocument = patientProfilesCollection.document(patientId)
        val postpartumVisitsCollection = patientDocument.collection("Visits")
        val visitDocument = postpartumVisitsCollection.document(visitId)

        visitDocument.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val motherheight = documentSnapshot.getString("height")
                    val motherweight = documentSnapshot.getString("weight")
                    val mothertemp = documentSnapshot.getString("temp")
                    val mothersystolic = documentSnapshot.getString("systolic")
                    val motherdiastolic = documentSnapshot.getString("diastolic")
                    val motherheartrate = documentSnapshot.getString("heartrate")
                    val motherother = documentSnapshot.getString("postpartother")

                    val detailsText = """
                Height: $motherheight
                Weight: $motherweight
                Temperature: $mothertemp
                Systolic: $mothersystolic
                Diastolic: $motherdiastolic
                Heart Rate: $motherheartrate
                Other: $motherother
            """.trimIndent()

                    textViewVisitDetails.text = detailsText

                } else {
                    textViewVisitDetails.text = "Visit details not found."
                }
            }
            .addOnFailureListener { e ->
                textViewVisitDetails.text = "Failed to fetch visit details: ${e.message}"
            }
    }

    private fun fetchAlertsFromFirestore(patientId: String, visitId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val alertsCollection = firestore
            .collection("Patient Profiles")
            .document(patientId)
            .collection("Visits")
            .document(visitId)
            .collection("Alerts")

        // Assuming there's a document called "HealthAlerts" containing all alerts
        val healthAlertsDocument = alertsCollection.document("HealthAlerts")

        healthAlertsDocument.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Fetch the content of the "HealthAlerts" field
                    val healthAlertsMap = documentSnapshot.data

                    // Logging for debugging
                    Log.d("AlertsDebug", "Fetched HealthAlerts: $healthAlertsMap")

                    // Build the alerts text dynamically based on available fields
                    val alertsText = buildAlertsText(healthAlertsMap)
                    textViewVisitAlerts.text = alertsText
                } else {
                    Log.d("AlertsDebug", "Document 'HealthAlerts' does not exist.")
                }
            }
            .addOnFailureListener { e ->
                // Handle failure, show an error message, etc.
                textViewVisitAlerts.text = "Failed to fetch alerts: ${e.message}"
            }
    }

    private fun buildAlertsText(healthAlertsMap: Map<String, Any>?): String {
        if (healthAlertsMap.isNullOrEmpty()) {
            return "No Health Alerts."
        }

        val alertsList = mutableListOf<String>()

        // Check each possible alert field independently
        val heartRate = healthAlertsMap["heartrate"]
        if (heartRate != null && heartRate.toString().isNotEmpty()) {
            alertsList.add("Heart Rate: $heartRate")
        }

        val temperature = healthAlertsMap["temperature"]
        if (temperature != null && temperature.toString().isNotEmpty()) {
            alertsList.add("Temperature: $temperature")
        }

        val diastolic = healthAlertsMap["diastolic"]
        if (diastolic != null && diastolic.toString().isNotEmpty()) {
            alertsList.add("Diastolic: $diastolic")
        }

        val systolic = healthAlertsMap["systolic"]
        if (systolic != null && systolic.toString().isNotEmpty()) {
            alertsList.add("Systolic: $systolic")
        }

        return if (alertsList.isNotEmpty()) {
            alertsList.joinToString("\n")
        } else {
            "No Health Alerts."
        }
    }

    private fun fetchChildVisitDetailsFromFirestore(patientId: String, visitId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val childVisitsCollection = firestore
            .collection("Patient Profiles")
            .document(patientId)
            .collection("Child")
            .document("Visits")
            .collection("PostpartumVisits")

        // Assuming there's a document with the specified visitId
        val childVisitDocument = childVisitsCollection.document(visitId)

        childVisitDocument.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Fetch the content of the child visit details
                    val childVisitDetailsMap = documentSnapshot.data

                    // Logging for debugging
                    Log.d("ChildVisitDebug", "Fetched Child Visit Details: $childVisitDetailsMap")

                    // Log fields inside the document
                    for ((key, value) in childVisitDetailsMap.orEmpty()) {
                        Log.d("ChildVisitDebug", "Field: $key, Value: $value")
                    }

                    // Retrieve specific fields
                    val childHeight = documentSnapshot.getString("childheight")
                    val childWeight = documentSnapshot.getString("childweight")
                    val childTemp = documentSnapshot.getString("childtemp")
                    val childSystolic = documentSnapshot.getString("childsystolic")
                    val childDiastolic = documentSnapshot.getString("childdiastolic")
                    val childHeartRate = documentSnapshot.getString("childheartrate")
                    val childHeadCircumference = documentSnapshot.getString("childheadcircumf")
                    val childRespiratoryRate = documentSnapshot.getString("respirrate")
                    val childFeedings = documentSnapshot.getString("feedings")

                    // Build the child visit details text
                    val childVisitDetailsText = buildChildVisitDetailsText(
                        childHeight,
                        childWeight,
                        childTemp,
                        childSystolic,
                        childDiastolic,
                        childHeartRate,
                        childHeadCircumference,
                        childRespiratoryRate,
                        childFeedings,
                    )

                    // Update the UI with the child visit details
                    textViewChildVisitDetails.text = childVisitDetailsText
                } else {
                    Log.d("ChildVisitDebug", "Child visit details not found.")
                }
            }
            .addOnFailureListener { e ->
                // Handle failure, show an error message, etc.
                textViewChildVisitDetails.text = "Failed to fetch child visit details: ${e.message}"
            }
    }


    private fun buildChildVisitDetailsText(
        childHeight: String?,
        childWeight: String?,
        childTemp: String?,
        childSystolic: String?,
        childDiastolic: String?,
        childHeartRate: String?,
        childHeadCircumference: String?,
        childRespiratoryRate: String?,
        childFeedings: String?,
    ): String {
        val detailsList = mutableListOf<String>()

        if (!childHeight.isNullOrEmpty()) {
            detailsList.add("Height (cm): $childHeight")
        }

        if (!childWeight.isNullOrEmpty()) {
            detailsList.add("Weight (kg): $childWeight")
        }

        if (!childTemp.isNullOrEmpty()) {
            detailsList.add("Temperature (C): $childTemp")
        }

        if (!childSystolic.isNullOrEmpty()) {
            detailsList.add("Systolic (mmHg): $childSystolic")
        }

        if (!childDiastolic.isNullOrEmpty()) {
            detailsList.add("Diastolic (mmHg): $childDiastolic")
        }

        if (!childHeartRate.isNullOrEmpty()) {
            detailsList.add("Heart Rate (bpm): $childHeartRate")
        }

        if (!childHeadCircumference.isNullOrEmpty()) {
            detailsList.add("Head Circumference (cm): $childHeadCircumference")
        }

        if (!childRespiratoryRate.isNullOrEmpty()) {
            detailsList.add("Respiratory Rate (breaths per min): $childRespiratoryRate")
        }

        if (!childFeedings.isNullOrEmpty()) {
            detailsList.add("Feedings Per Day: $childFeedings")
        }

        return if (detailsList.isNotEmpty()) {
            detailsList.joinToString("\n")
        } else {
            "No Child Visit Details."
        }
    }

    private fun fetchChildAlertsFromFirestore(patientId: String, visitId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val childAlertsCollection = firestore
            .collection("Patient Profiles")
            .document(patientId)
            .collection("Child")
            .document("Visits")
            .collection("PostpartumVisits")
            .document(visitId)
            .collection("Alerts")

        // Assuming there's a document called "HealthAlerts" containing all alerts
        val healthAlertsDocument = childAlertsCollection.document("HealthAlerts")

        healthAlertsDocument.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Fetch the content of the "HealthAlerts" field
                    val childHealthAlertsMap = documentSnapshot.data

                    // Logging for debugging
                    Log.d("ChildAlertsDebug", "Fetched Child Health Alerts: $childHealthAlertsMap")

                    // Build the child health alerts text dynamically based on available fields
                    val childAlertsText = buildChildAlertsText(childHealthAlertsMap)

                    // Update the UI with the child health alerts
                    textViewVisitChildAlerts.text = childAlertsText
                } else {
                    Log.d("ChildAlertsDebug", "Document 'HealthAlerts' does not exist.")
                }
            }
            .addOnFailureListener { e ->
                // Handle failure, show an error message, etc.
                Log.e("ChildAlertsDebug", "Failed to fetch child health alerts: ${e.message}")
                // Update the UI accordingly
                textViewVisitChildAlerts.text = "Failed to fetch child health alerts"
            }
    }


    private fun buildChildAlertsText(childHealthAlertsMap: Map<String, Any>?): String {
        if (childHealthAlertsMap.isNullOrEmpty()) {
            return "No Child Health Alerts."
        }

        val alertsList = mutableListOf<String>()

        // Check each potential child alert variable independently
        val childDiastolic = childHealthAlertsMap["childdiastolic"]
        if (childDiastolic != null && childDiastolic.toString().isNotEmpty()) {
            alertsList.add("Child Diastolic: $childDiastolic")
        }

        val childHR = childHealthAlertsMap["childhr"]
        if (childHR != null && childHR.toString().isNotEmpty()) {
            alertsList.add("Child Heart Rate: $childHR")
        }

        val childRR = childHealthAlertsMap["childrr"]
        if (childRR != null && childRR.toString().isNotEmpty()) {
            alertsList.add("Child Respiratory Rate: $childRR")
        }

        val childSystolic = childHealthAlertsMap["childsystolic"]
        if (childSystolic != null && childSystolic.toString().isNotEmpty()) {
            alertsList.add("Child Systolic: $childSystolic")
        }

        val childTemp = childHealthAlertsMap["childtemp"]
        if (childTemp != null && childTemp.toString().isNotEmpty()) {
            alertsList.add("Child Temperature: $childTemp")
        }

        return if (alertsList.isNotEmpty()) {
            alertsList.joinToString("\n")
        } else {
            "No Child Health Alerts."
        }
    }








}
