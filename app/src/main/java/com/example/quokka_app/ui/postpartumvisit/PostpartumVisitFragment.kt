package com.example.quokka_app.ui.postpartumvisit

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.quokka_app.R
import com.example.quokka_app.databinding.FragmentPostpartumVisitBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class PostpartumVisitFragment : Fragment(R.layout.fragment_postpartum_visit) {
    private lateinit var binding: FragmentPostpartumVisitBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostpartumVisitBinding.inflate(inflater, container, false)
        val view = binding.root
        val patientId = arguments?.getString("patientId") ?: ""

        // Alert System:
        // Temperature Alert
        binding.postpartumTexteditTemperature.setOnFocusChangeListener { _, temphasFocus ->
            if (!temphasFocus) {
                val temperatureValue =
                    binding.postpartumTexteditTemperature.text.toString().toDoubleOrNull()
                if (temperatureValue != null && (temperatureValue < 35 || temperatureValue > 38.0)) {
                    showTemperatureConfirmationDialog(temperatureValue)
                }
            }
        }

        // Systolic BP Alert
        binding.postpartumTexteditSystolic.setOnFocusChangeListener { _, systolichasFocus ->
            if (!systolichasFocus) {
                val systolicBloodPressureValue =
                    binding.postpartumTexteditSystolic.text.toString().toDoubleOrNull()
                if (systolicBloodPressureValue != null && (systolicBloodPressureValue < 95.0 || systolicBloodPressureValue > 140.0)) {
                    showBloodPressureConfirmationSystolic(systolicBloodPressureValue)
                }
            }
        }
        // Diastolic BP Alert
        binding.postpartumTexteditDiastolic.setOnFocusChangeListener { _, diastolichasFocus ->
            if (!diastolichasFocus) {
                val diastolicBloodPressureValue =
                    binding.postpartumTexteditDiastolic.text.toString().toDoubleOrNull()
                if (diastolicBloodPressureValue != null && (diastolicBloodPressureValue < 60.0 || diastolicBloodPressureValue > 92.0)) {
                    showBloodPressureConfirmationDiastolic(diastolicBloodPressureValue)
                }
            }
        }
        // Heart Rate Alert
        binding.postpartumTexteditHeartrate.setOnFocusChangeListener { _, heartRatehasFocus ->
            if (!heartRatehasFocus) {
                val heartRateValue =
                    binding.postpartumTexteditHeartrate.text.toString().toDoubleOrNull()
                if (heartRateValue != null && (heartRateValue < 60.0 || heartRateValue > 105.0)) {
                    showHeartRateConfirmation(heartRateValue)
                }
            }
        }
        // Child  Heart Rate Alert:
        binding.postpartumTexteditChildheartrate.setOnFocusChangeListener { _, childHRhasFocus ->
            if (!childHRhasFocus) {
                val childHRValue =
                    binding.postpartumTexteditChildheartrate.text.toString().toDoubleOrNull()
                if (childHRValue != null && (childHRValue < 90.0 || childHRValue > 165.0)) {
                    showChildHRConfirmation(childHRValue)
                }
            }
        }

        // Child Temperature Alert:
        binding.postpartumTexteditChildtemperature.setOnFocusChangeListener { _, childtemphasFocus ->
            if (!childtemphasFocus) {
                val childtemperatureValue =
                    binding.postpartumTexteditChildtemperature.text.toString().toDoubleOrNull()
                if (childtemperatureValue != null && (childtemperatureValue < 36.4 || childtemperatureValue > 37.6)) {
                    showChildTempConfirmation(childtemperatureValue)
                }
            }
        }

        // Child Systolic Alert:
        binding.postpartumTexteditChildsystolic.setOnFocusChangeListener { _, childsystolichasFocus ->
            if (!childsystolichasFocus) {
                val childsystolicValue =
                    binding.postpartumTexteditChildsystolic.text.toString().toDoubleOrNull()
                if (childsystolicValue != null && (childsystolicValue < 51 || childsystolicValue > 86)) {
                    showchildBloodPressureConfirmationSystolic(childsystolicValue)
                }
            }
        }

        // Child Diastolic Alert:
        binding.postpartumTexteditChilddiastolic.setOnFocusChangeListener { _, childdiastolichasFocus ->
            if (!childdiastolichasFocus) {
                val childdiastolicValue =
                    binding.postpartumTexteditChilddiastolic.text.toString().toDoubleOrNull()
                if (childdiastolicValue != null && (childdiastolicValue < 28 || childdiastolicValue > 61)) {
                    showchildBloodPressureConfirmationDiastolic(childdiastolicValue)
                }
            }
        }

        // Child  Respiratory Rate Alert:
        binding.postpartumTexteditBreathingrate.setOnFocusChangeListener { _, childRRhasFocus ->
            if (!childRRhasFocus) {
                val childRRValue =
                    binding.postpartumTexteditBreathingrate.text.toString().toDoubleOrNull()
                if (childRRValue != null && (childRRValue < 25.0 || childRRValue > 58.0)) {
                    showChildRRConfirmation(childRRValue)
                }
            }
        }

        binding.postpartumButtonSave.setOnClickListener {
            val height = binding.postpartumTexteditHeight.text.toString()
            val weight = binding.postpartumTexteditWeight.text.toString()
            val temp = binding.postpartumTexteditTemperature.text.toString()
            val systolic = binding.postpartumTexteditSystolic.text.toString()
            val diastolic = binding.postpartumTexteditDiastolic.text.toString()
            val heartrate = binding.postpartumTexteditHeartrate.text.toString()
            val postpartother = binding.postpartumTexteditOthercomments.text.toString()
            val childheight = binding.postpartumTexteditChildheight.text.toString()
            val childweight = binding.postpartumTexteditChildweight.text.toString()
            val childtemp = binding.postpartumTexteditChildtemperature.text.toString()
            val childsystolic = binding.postpartumTexteditChildsystolic.text.toString()
            val childdiastolic = binding.postpartumTexteditChilddiastolic.text.toString()
            val childheartrate = binding.postpartumTexteditChildheartrate.text.toString()
            val headcircumf = binding.postpartumTexteditHeadcirc.text.toString()
            val respirrate = binding.postpartumTexteditBreathingrate.text.toString()
            val feedings = binding.postpartumTexteditFeeding.text.toString()
            val childother = binding.postpartumTexteditChildothercomments.toString()

            val postpartumVisitData = PostpartumDataClass(
                height = height,
                weight = weight,
                temp = temp,
                systolic = systolic,
                diastolic = diastolic,
                heartrate = heartrate,
                postpartother = postpartother,
                date = getCurrentDateTimeAsString()
            )
            if ((systolic.isNotBlank() && diastolic.isBlank()) ||
                (systolic.isBlank() && diastolic.isNotBlank())
            ) {
                Toast.makeText(
                    requireContext(),
                    "Please enter both systolic and diastolic values or leave both blank",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener

            }

            val firestore = FirebaseFirestore.getInstance()
            if (patientId.isNotEmpty()) {
                val patientProfilesCollection = firestore.collection("Patient Profiles")
                val patientDocument = patientProfilesCollection.document(patientId)
                val visitsCollection = patientDocument.collection("Visits")
                val visitDocument =
                    visitsCollection.document("Postpartum Visit: ${postpartumVisitData.date}")

                visitDocument.set(postpartumVisitData)
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "Postpartum Visit saved successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        val childCollection = patientDocument.collection("Child")
                        val postpartumVisitsCollection =
                            childCollection.document("Visits").collection("PostpartumVisits")
                        val postpartumChildVisitDocument =
                            postpartumVisitsCollection.document("Postpartum Visit: ${getCurrentDateTimeAsString()}")
                        val childData = mapOf(
                            "childheight" to childheight,
                            "childweight" to childweight,
                            "childtemp" to childtemp,
                            "childsystolic" to childsystolic,
                            "childdiastolic" to childdiastolic,
                            "childheartrate" to childheartrate,
                            "childheadcircumf" to headcircumf,
                            "respirrate" to respirrate,
                            "feedings" to feedings,
                            "childother" to childother
                        )

                        postpartumChildVisitDocument.set(childData)
                            .addOnSuccessListener {}
                            .addOnFailureListener {}


                        binding.postpartumTexteditHeight.text?.clear()
                        binding.postpartumTexteditWeight.text?.clear()
                        binding.postpartumTexteditTemperature.text?.clear()
                        binding.postpartumTexteditSystolic.text?.clear()
                        binding.postpartumTexteditDiastolic.text?.clear()
                        binding.postpartumTexteditHeartrate.text?.clear()
                        binding.postpartumTexteditOthercomments.text?.clear()
                        binding.postpartumTexteditChildheight.text?.clear()
                        binding.postpartumTexteditChildweight.text?.clear()
                        binding.postpartumTexteditChildtemperature.text?.clear()
                        binding.postpartumTexteditChildsystolic.text?.clear()
                        binding.postpartumTexteditChilddiastolic.text?.clear()
                        binding.postpartumTexteditChildheartrate.text?.clear()
                        binding.postpartumTexteditHeadcirc.text?.clear()
                        binding.postpartumTexteditBreathingrate.text?.clear()
                        binding.postpartumTexteditFeeding.text?.clear()
                        binding.postpartumTexteditChildothercomments.text?.clear()
                        val fragmentManager = parentFragmentManager
                        fragmentManager.popBackStack()

                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            requireContext(),
                            "Failed to save postpartum visit: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }

        return view
    }
    private fun getCurrentDateTimeAsString(): String {
        val dateFormat = SimpleDateFormat("ddMMyyyyHHmm", Locale.getDefault())
        val date = System.currentTimeMillis()
        return dateFormat.format(date)
    }

    // Alert System Functions
    // Temperature
    private fun showTemperatureConfirmationDialog(temperatureValue: Double) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Temperature Warning")
        alertDialogBuilder.setMessage("The inputted temperature ($temperatureValue °C) is outside the normal range (35 to 38 °C). Are you sure this is the intended value?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            val visitDate = getCurrentDateTimeAsString()
            val patientId = arguments?.getString("patientId") ?: ""
            val alertsToSave = listOf("temperature" to temperatureValue.toString())
            saveAlerts(patientId, "Mother", visitDate, alertsToSave)
        }
        alertDialogBuilder.setNegativeButton("No") { _, _ ->
            binding.postpartumTexteditTemperature.text?.clear()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showBloodPressureConfirmationSystolic(systolicBloodPressureValue: Double) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Systolic Blood Pressure Warning")
        alertDialogBuilder.setMessage("The inputted systolic blood pressure value ($systolicBloodPressureValue mmHg) is outside the normal range (95-140 mmHg). Are you sure this is the intended value?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            val visitDate = getCurrentDateTimeAsString()
            val patientId = arguments?.getString("patientId") ?: ""
            val alertsToSave = listOf("systolic" to systolicBloodPressureValue.toString())
            saveAlerts(patientId, "Mother", visitDate, alertsToSave)
        }
        alertDialogBuilder.setNegativeButton("No") { _, _ ->
            binding.postpartumTexteditSystolic.text?.clear()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showBloodPressureConfirmationDiastolic(diastolicBloodPressureValue: Double) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Diastolic Blood Pressure Warning")
        alertDialogBuilder.setMessage("The inputted diastolic blood pressure value ($diastolicBloodPressureValue mmHg) is outside the normal range (60-92 mmHg). Are you sure this is the intended value?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            val visitDate = getCurrentDateTimeAsString()
            val patientId = arguments?.getString("patientId") ?: ""
            val alertsToSave = listOf("diastolic" to diastolicBloodPressureValue.toString())
            saveAlerts(patientId, "Mother", visitDate, alertsToSave)
        }
        alertDialogBuilder.setNegativeButton("No") { _, _ ->
            binding.postpartumTexteditDiastolic.text?.clear()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showHeartRateConfirmation(heartRateValue: Double) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Heart Rate Warning")
        alertDialogBuilder.setMessage("The inputted heart rate value ($heartRateValue bpm) is outside the normal range (60 to 105 bpm). Are you sure this is the intended value?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            val visitDate = getCurrentDateTimeAsString()
            val patientId = arguments?.getString("patientId") ?: ""
            val alertsToSave = listOf("heartrate" to heartRateValue.toString())
            saveAlerts(patientId, "Mother", visitDate, alertsToSave)
        }
        alertDialogBuilder.setNegativeButton("No") { _, _ ->
            binding.postpartumTexteditHeartrate.text?.clear()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showChildHRConfirmation(childHRValue: Double) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Child Heart Rate Warning")
        alertDialogBuilder.setMessage("The inputted child heart rate value ($childHRValue bpm) is outside the normal range (90 to 165 bpm). Are you sure this is the intended value?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            val visitDate = getCurrentDateTimeAsString()
            val patientId = arguments?.getString("patientId") ?: ""
            val alertsToSave = listOf("childhr" to childHRValue.toString())
            saveAlerts(patientId, "Child", visitDate, alertsToSave)
        }
        alertDialogBuilder.setNegativeButton("No") { _, _ ->
            binding.postpartumTexteditChildheartrate.text?.clear()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showChildTempConfirmation(childTempValue: Double) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Child Temperature Warning")
        alertDialogBuilder.setMessage("The inputted temperature ($childTempValue °C) is outside the normal range (36.4 to 37.6 °C). Are you sure this is the intended value?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            val visitDate = getCurrentDateTimeAsString()
            val patientId = arguments?.getString("patientId") ?: ""
            val alertsToSave = listOf("childtemp" to childTempValue.toString())
            saveAlerts(patientId, "Child", visitDate, alertsToSave)
        }
        alertDialogBuilder.setNegativeButton("No") { _, _ ->
            binding.postpartumTexteditChildtemperature.text?.clear()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showchildBloodPressureConfirmationSystolic(childsystolicBloodPressureValue: Double) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Child Systolic Blood Pressure Warning")
        alertDialogBuilder.setMessage("The inputted child's systolic blood pressure value ($childsystolicBloodPressureValue mmHg) is outside the normal range (51-86 mmHg). Are you sure this is the intended value?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            val visitDate = getCurrentDateTimeAsString()
            val patientId = arguments?.getString("patientId") ?: ""
            val alertsToSave = listOf("childsystolic" to childsystolicBloodPressureValue.toString())
            saveAlerts(patientId, "Child", visitDate, alertsToSave)
        }
        alertDialogBuilder.setNegativeButton("No") { _, _ ->
            binding.postpartumTexteditChildsystolic.text?.clear()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showchildBloodPressureConfirmationDiastolic(childdiastolicBloodPressureValue: Double) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Child Diastolic Blood Pressure Warning")
        alertDialogBuilder.setMessage("The inputted child's diastolic blood pressure value ($childdiastolicBloodPressureValue mmHg) is outside the normal range (28-61 mmHg). Are you sure this is the intended value?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            val visitDate = getCurrentDateTimeAsString()
            val patientId = arguments?.getString("patientId") ?: ""
            val alertsToSave = listOf("childdiastolic" to childdiastolicBloodPressureValue.toString())
            saveAlerts(patientId, "Child", visitDate, alertsToSave)
        }
        alertDialogBuilder.setNegativeButton("No") { _, _ ->
            binding.postpartumTexteditDiastolic.text?.clear()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showChildRRConfirmation(childRRValue: Double) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Child Respiratory Rate Warning")
        alertDialogBuilder.setMessage("The inputted child respiratory rate value ($childRRValue bpm) is outside the normal range (25 to 58 bpm). Are you sure this is the intended value?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            val visitDate = getCurrentDateTimeAsString()
            val patientId = arguments?.getString("patientId") ?: ""
            val alertsToSave = listOf("childrr" to childRRValue.toString())
            saveAlerts(patientId, "Child", visitDate, alertsToSave)
        }
        alertDialogBuilder.setNegativeButton("No") { _, _ ->
            binding.postpartumTexteditBreathingrate.text?.clear()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun saveAlerts(
        patientId: String,
        visitType: String,
        visitDate: String,
        alertsToSave: List<Pair<String, String>>
    ) {
        val firestore = FirebaseFirestore.getInstance()
        val patientProfilesCollection = firestore.collection("Patient Profiles")
        val patientDocument = patientProfilesCollection.document(patientId)
        val visitSubcollection = if (visitType == "Mother") {
            patientDocument.collection("Visits")
        } else {
            patientDocument.collection("Child").document("Visits").collection("PostpartumVisits")
        }
        val visitDocument = visitSubcollection.document("Postpartum Visit: $visitDate")
        val alertsCollection = visitDocument.collection("Alerts")
        val existingAlertsDocument = alertsCollection.document("HealthAlerts")

        existingAlertsDocument.get()
            .addOnSuccessListener { documentSnapshot ->
                val batch = firestore.batch()
                val alertsMap = HashMap<String, String>()

                if (documentSnapshot.exists()) {
                    val existingData = documentSnapshot.data
                    if (existingData != null) {
                        alertsMap.putAll(existingData as Map<String, String>)
                    }
                }
                for ((fieldName, alertValue) in alertsToSave) {
                    alertsMap[fieldName] = alertValue
                }
                batch.set(existingAlertsDocument, alertsMap)

                batch.commit()
                    .addOnSuccessListener {
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            requireContext(),
                            "Failed to save health alerts: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Failed to retrieve existing health alerts: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}