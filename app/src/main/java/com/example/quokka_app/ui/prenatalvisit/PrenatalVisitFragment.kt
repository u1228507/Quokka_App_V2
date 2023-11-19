package com.example.quokka_app.ui.prenatalvisit

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.quokka_app.R
import com.example.quokka_app.databinding.FragmentPrenatalVisitBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class PrenatalVisitFragment : Fragment() {
    private lateinit var binding: FragmentPrenatalVisitBinding
    private var temperatureEdited = false
    private var systolicEdited = false
    private var diastolicEdited = false
    private var heartRateEdited = false
    private var fetalHREdited = false
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrenatalVisitBinding.inflate(inflater, container, false)
        val view = binding.root
        val patientId = arguments?.getString("patientId") ?: ""
        progressBar = binding.prenatalProgressBar

        // Alert System:
        // Temperature Alert
        binding.prenatalTexteditTemperature.setOnFocusChangeListener { _, temphasFocus ->
            if (!temphasFocus) {
                temperatureEdited = true
                val temperatureValue =
                    binding.prenatalTexteditTemperature.text.toString().toDoubleOrNull()
                if (temperatureValue != null && (temperatureValue < 35.0 || temperatureValue > 38.0)) {
                    showTemperatureConfirmationDialog(temperatureValue)
                }
            }
        }
        // Systolic BP Alert
        binding.prenatalTexteditSystolic.setOnFocusChangeListener { _, systolichasFocus ->
            if (!systolichasFocus) {
                systolicEdited = true
                val systolicBloodPressureValue =
                    binding.prenatalTexteditSystolic.text.toString().toDoubleOrNull()
                if (systolicBloodPressureValue != null && (systolicBloodPressureValue < 90.0 || systolicBloodPressureValue > 140.0)) {
                    showBloodPressureConfirmationSystolic(systolicBloodPressureValue)
                }
            }
        }
        // Diastolic BP Alert
        binding.prenatalTexteditDiastolic.setOnFocusChangeListener { _, diastolichasFocus ->
            if (!diastolichasFocus) {
                diastolicEdited = true
                val diastolicBloodPressureValue =
                    binding.prenatalTexteditDiastolic.text.toString().toDoubleOrNull()
                if (diastolicBloodPressureValue != null && (diastolicBloodPressureValue < 55.0 || diastolicBloodPressureValue > 95.0)) {
                    showBloodPressureConfirmationDiastolic(diastolicBloodPressureValue)
                }
            }
        }
        // Heart Rate Alert
        binding.prenatalTexteditHeartrate.setOnFocusChangeListener { _, heartRatehasFocus ->
            if (!heartRatehasFocus) {
                heartRateEdited = true
                val heartRateValue =
                    binding.prenatalTexteditHeartrate.text.toString().toDoubleOrNull()
                if (heartRateValue != null && (heartRateValue < 65.0 || heartRateValue > 110.0)) {
                    showHeartRateConfirmation(heartRateValue)
                }
            }
        }
        // Fetal Heart Rate Alert:
        binding.prenatalTexteditFetalheartrate.setOnFocusChangeListener { _, fetalHRhasFocus ->
            if (!fetalHRhasFocus) {
                fetalHREdited = true
                val fetalHRValue =
                    binding.prenatalTexteditFetalheartrate.text.toString().toDoubleOrNull()
                if (fetalHRValue != null && (fetalHRValue < 100.0 || fetalHRValue > 160.0)) {
                    showFetalHRConfirmation(fetalHRValue)
                }
            }
        }

        binding.prenatalButtonSave.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val height = binding.prenatalTexteditHeight.text.toString()
            val weight = binding.prenatalTexteditWeight.text.toString()
            val temp = binding.prenatalTexteditTemperature.text.toString()
            val systolic = binding.prenatalTexteditSystolic.text.toString()
            val diastolic = binding.prenatalTexteditDiastolic.text.toString()
            val heartrate = binding.prenatalTexteditHeartrate.text.toString()
            val fundalheight = binding.prenatalTexteditFundalheight.text.toString()
            val prenatalother = binding.prenatalTexteditOthercomments.text.toString()
            val fetalhr = binding.prenatalTexteditFetalheartrate.text.toString()
            val fetalmove = binding.prenatalTexteditFetalmove.text.toString()
            val fetalpos = binding.prenatalTexteditFetalposition.text.toString()
            val fetalcomments = binding.prenatalTexteditChildothercomments.text.toString()

            val prenatalVisitData = PrenatalVisitDataClass(
                height = height,
                weight = weight,
                temp = temp,
                systolic = systolic,
                diastolic = diastolic,
                heartrate = heartrate,
                fundalheight = fundalheight,
                prenatalother = prenatalother,
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
                    visitsCollection.document("Prenatal Visit: ${prenatalVisitData.date}")

                visitDocument.set(prenatalVisitData)
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "Prenatal Visit saved successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        val childCollection = patientDocument.collection("Child")
                        val prenatalVisitsCollection =
                            childCollection.document("Visits").collection("PrenatalVisits")
                        val prenatalChildVisitDocument =
                            prenatalVisitsCollection.document("Prenatal Visit: ${getCurrentDateTimeAsString()}")
                        val fetalData = mapOf(
                            "fetalHeartRate" to fetalhr,
                            "fetalPosition" to fetalpos,
                            "fetalMovement" to fetalmove,
                            "fetalcomments" to fetalcomments
                        )

                        prenatalChildVisitDocument.set(fetalData)
                            .addOnSuccessListener {}
                            .addOnFailureListener {}

                        binding.prenatalTexteditHeight.text?.clear()
                        binding.prenatalTexteditWeight.text?.clear()
                        binding.prenatalTexteditTemperature.text?.clear()
                        binding.prenatalTexteditSystolic.text?.clear()
                        binding.prenatalTexteditDiastolic.text?.clear()
                        binding.prenatalTexteditHeartrate.text?.clear()
                        binding.prenatalTexteditFundalheight.text?.clear()
                        binding.prenatalTexteditOthercomments.text?.clear()
                        binding.prenatalTexteditFetalheartrate.text?.clear()
                        binding.prenatalTexteditFetalmove.setText(R.string.prenatal_input_unknown)
                        binding.prenatalTexteditFetalmove.setSelection(0)
                        binding.prenatalTexteditFetalposition.setText(R.string.prenatal_input_unknown)
                        binding.prenatalTexteditFetalposition.setSelection(0)
                        progressBar.visibility = View.GONE
                        val fragmentManager = parentFragmentManager
                        fragmentManager.popBackStack()

                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            requireContext(),
                            "Failed to save prenatal visit: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
        return view
    }


    override fun onResume() {
        super.onResume()
        val fetalmovement = resources.getStringArray(R.array.prenatal_fetalmovement)
        val arrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.newpatientinputs_dropdownmenus, fetalmovement
        )
        binding.prenatalTexteditFetalmove.setAdapter(arrayAdapter)

        val fetalposition = resources.getStringArray(R.array.prenatal_header_fetalposition)
        val fetalposarrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.newpatientinputs_dropdownmenus, fetalposition
        )
        binding.prenatalTexteditFetalposition.setAdapter(fetalposarrayAdapter)
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
            binding.prenatalTexteditTemperature.text?.clear()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showBloodPressureConfirmationSystolic(systolicBloodPressureValue: Double) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Systolic Blood Pressure Warning")
        alertDialogBuilder.setMessage("The inputted systolic blood pressure value ($systolicBloodPressureValue mmHg) is outside the normal range (90-140 mmHg). Are you sure this is the intended value?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            val visitDate = getCurrentDateTimeAsString()
            val patientId = arguments?.getString("patientId") ?: ""
            val alertsToSave = listOf("systolic" to systolicBloodPressureValue.toString())
            saveAlerts(patientId, "Mother", visitDate, alertsToSave)
        }
        alertDialogBuilder.setNegativeButton("No") { _, _ ->
            binding.prenatalTexteditSystolic.text?.clear()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showBloodPressureConfirmationDiastolic(diastolicBloodPressureValue: Double) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Diastolic Blood Pressure Warning")
        alertDialogBuilder.setMessage("The inputted diastolic blood pressure value ($diastolicBloodPressureValue mmHg) is outside the normal range (55-95 mmHg). Are you sure this is the intended value?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            val visitDate = getCurrentDateTimeAsString()
            val patientId = arguments?.getString("patientId") ?: ""
            val alertsToSave = listOf("diastolic" to diastolicBloodPressureValue.toString())
            saveAlerts(patientId, "Mother", visitDate, alertsToSave)
        }
        alertDialogBuilder.setNegativeButton("No") { _, _ ->
            binding.prenatalTexteditDiastolic.text?.clear()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showHeartRateConfirmation(heartRateValue: Double) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Heart Rate Warning")
        alertDialogBuilder.setMessage("The inputted heart rate value ($heartRateValue bpm) is outside the normal range (65 to 115 bpm). Are you sure this is the intended value?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            val visitDate = getCurrentDateTimeAsString()
            val patientId = arguments?.getString("patientId") ?: ""
            val alertsToSave = listOf("heartrate" to heartRateValue.toString())
            saveAlerts(patientId, "Mother", visitDate, alertsToSave)
        }
        alertDialogBuilder.setNegativeButton("No") { _, _ ->
            binding.prenatalTexteditHeartrate.text?.clear()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showFetalHRConfirmation(fetalHRValue: Double) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Fetal Heart Rate Warning")
        alertDialogBuilder.setMessage("The inputted fetal heart rate value ($fetalHRValue bpm) is outside the normal range (110 to 160 bpm). Are you sure this is the intended value?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            val visitDate = getCurrentDateTimeAsString()
            val patientId = arguments?.getString("patientId") ?: ""
            val alertsToSave = listOf("fetalhr" to fetalHRValue.toString())
            saveAlerts(patientId, "Child", visitDate, alertsToSave)
        }
        alertDialogBuilder.setNegativeButton("No") { _, _ ->
            binding.prenatalTexteditFetalheartrate.text?.clear()
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
            patientDocument.collection("Child").document("Visits").collection("PrenatalVisits")
        }
        val visitDocument = visitSubcollection.document("Prenatal Visit: $visitDate")
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


