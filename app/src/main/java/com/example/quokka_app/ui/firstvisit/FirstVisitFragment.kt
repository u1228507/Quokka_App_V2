package com.example.quokka_app.ui.firstvisit

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.quokka_app.R
import com.example.quokka_app.databinding.FragmentFirstVisitBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.Locale

class FirstVisitFragment : Fragment(R.layout.fragment_first_visit) {
    private var _binding: FragmentFirstVisitBinding? = null
    private val binding get() = _binding!!
    private var isDueDateFieldClicked = false
    private val alertsToSave = mutableListOf<Pair<String, String>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View {
        _binding = FragmentFirstVisitBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Data from New Patient Profile
        val firstName = arguments?.getString("firstname")
        val lastName = arguments?.getString("lastname")
        val imageUrl = arguments?.getString("imageUrl")
        val dateOfBirth = arguments?.getString("dateofbirth")
        val patientId = arguments?.getString("patientId")
        Log.d("Pegasus", "patientId: $patientId")


        // Alert System:
        // Temperature Alert
        binding.texteditFirstvisitTemperature.setOnFocusChangeListener { _, temphasFocus ->
            if (!temphasFocus) {
                val temperatureValue =
                    binding.texteditFirstvisitTemperature.text.toString().toDoubleOrNull()
                if (temperatureValue != null && (temperatureValue < 35.0 || temperatureValue > 38.0)) {
                    showTemperatureConfirmationDialog(temperatureValue)
                }
            }
        // Systolic BP Alert
        binding.texteditFirstvisitSystolic.setOnFocusChangeListener { _, systolichasFocus ->
            if (!systolichasFocus) {
                val systolicBloodPressureValue =
                    binding.texteditFirstvisitSystolic.text.toString().toDoubleOrNull()
                if (systolicBloodPressureValue != null && (systolicBloodPressureValue < 90.0 || systolicBloodPressureValue > 140.0)) {
                    showBloodPressureConfirmationSystolic(systolicBloodPressureValue)
                }
            }
        // Diastolic BP Alert
        binding.texteditFirstvisitDiastolic.setOnFocusChangeListener{_, diastolichasFocus ->
            if (!diastolichasFocus) {
                val diastolicBloodPressureValue = binding.texteditFirstvisitDiastolic.text.toString().toDoubleOrNull()
                if (diastolicBloodPressureValue !=null && (diastolicBloodPressureValue < 55.0 || diastolicBloodPressureValue > 95.0)){
                    showBloodPressureConfirmationDiastolic(diastolicBloodPressureValue)
                }
            }
        }
        // Heart Rate Alert
        binding.texteditFirstvisitHeartrate.setOnFocusChangeListener { _, heartRatehasFocus ->
            if (!heartRatehasFocus){
                val heartRateValue = binding.texteditFirstvisitHeartrate.text.toString().toDoubleOrNull()
                if (heartRateValue !=null && (heartRateValue <65.0 || heartRateValue > 110.0)){
                    showHeartRateConfirmation(heartRateValue)
                }
            }
        }
        // Fetal Heart Rate Alert:
        binding.texteditFirstvisitFetalheartrate.setOnFocusChangeListener{_, fetalHRhasFocus ->
            if (!fetalHRhasFocus){
                val fetalHRValue = binding.texteditFirstvisitFetalheartrate.text.toString().toDoubleOrNull()
                if (fetalHRValue !=null && (fetalHRValue < 100.0 || fetalHRValue > 160.0)){
                    showFetalHRConfirmation(fetalHRValue)
                }
            }
        }
        }

        }

        binding.firstvisitButtonSavefirstvisit.setOnClickListener {
            val height = binding.texteditFirstvisitHeight.text.toString()
            val weight = binding.texteditFirstvisitWeight.text.toString()
            val temperature = binding.texteditFirstvisitTemperature.text.toString()
            val systolic = binding.texteditFirstvisitSystolic.text.toString()
            val diastolic = binding.texteditFirstvisitDiastolic.text.toString()
            val heartrate = binding.texteditFirstvisitHeartrate.text.toString()
            val urinalysis = binding.firstvisitTextinputUrinalysis.text.toString()
            val hemoglobin = binding.firstvisitTextinputHemoglobin.text.toString()
            val antibodies = binding.firstvisitTextinputAntibodies.text.toString()
            val bloodtype = binding.firstvisitTextinputBloodtype.text.toString()
            val bloodtypelabs = binding.firstvisitTextinputConditionalBloodwork.text.toString()
            val hepB = binding.firstvisitTextinputHepb.text.toString()
            val hepBLabs = binding.firstvisitTextinputConditionalHepb.text.toString()
            val hiv = binding.firstvisitTextinputHiv.text.toString()
            val hivlabs = binding.firstvisitTextinputConditionalHiv.text.toString()
            val syph = binding.firstvisitTextinputSyph.text.toString()
            val syphlabs = binding.firstvisitTextinputConditionalSyph.text.toString()
            val fetalHeartRate = binding.texteditFirstvisitHeartrate.text.toString()
            val fetalPosition = binding.firstvisitTextinputFetalposition.text.toString()
            val fetalMovement = binding.firstvisitTextinputFetalmove.text.toString()
            val dueDate = binding.texteditFirstvisitDuedate.text.toString()

            val firstVisitData = FirstVisitDataClass(
                height = height,
                weight = weight,
                temperature = temperature,
                bloodpressure = "$systolic/$diastolic",
                heartrate = heartrate,
                urinalysis = urinalysis,
                hemoglobin = hemoglobin,
                bloodtype = bloodtype,
                bloodtypelabs = bloodtypelabs,
                antibodies = antibodies,
                hepb = hepB,
                hepblabs = hepBLabs,
                hiv = hiv,
                hivlabs = hivlabs,
                syph = syph,
                syphlabs = syphlabs,
                duedate = dueDate
            )

           if (alertsToSave.isNotEmpty()) {
             saveAlerts(alertsToSave)
            }
            val firestore = FirebaseFirestore.getInstance()

            if (patientId != null) {
                val patientProfilesCollection = firestore.collection("Patient Profiles")
                val patientDocument = patientProfilesCollection.document(patientId)
                val visitsCollection = patientDocument.collection("Visits")
                val visitDocument = visitsCollection.document("Prenatal Visit 1")

                visitDocument.set(firstVisitData)
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "First Visit saved successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        val childCollection = patientDocument.collection("Child")
                        val prenatalVisitsCollection =
                            childCollection.document("Visits").collection("PrenatalVisits")
                        val prenatalChildVisitDocument =
                            prenatalVisitsCollection.document("PrenatalChildVisit1")

                        val fetalData = mapOf(
                            "fetalHeartRate" to fetalHeartRate,
                            "fetalPosition" to fetalPosition,
                            "fetalMovement" to fetalMovement
                        )

                        prenatalChildVisitDocument.set(fetalData)
                            .addOnSuccessListener {}
                            .addOnFailureListener {}
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            requireContext(),
                            "Failed to save first visit: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }

            val bundle = Bundle()
            bundle.putString("firstname",firstName)
            bundle.putString("lastname", lastName)
            bundle.putString("imageUrl", imageUrl)
            bundle.putString("dateofbirth", dateOfBirth)
            bundle.putString("patientid", patientId)
            findNavController().navigate(R.id.action_firstvisitfragment_to_patientprofilesfragment, bundle)


        }
        // Conditional Drop Down Menus
        // Blood Type Question
        val conditionalInputLayoutBloodType = binding.firstvisitLayoutConditionalBloodwork
        val initialInputBloodType = binding.firstvisitTextinputBloodtype
        val conditionalInputBloodType = binding.firstvisitTextinputConditionalBloodwork
        val conditionalTextBloodType = binding.firstvisitConditionalBloodtype
        initialInputBloodType.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val initialInputText = s.toString()
                if (initialInputText.equals("unknown", ignoreCase = true)) {
                    conditionalInputLayoutBloodType.visibility = View.VISIBLE
                    conditionalInputBloodType.visibility = View.VISIBLE
                    conditionalTextBloodType.visibility = View.VISIBLE
                } else {
                    conditionalInputLayoutBloodType.visibility = View.GONE
                    conditionalInputBloodType.visibility = View.GONE
                    conditionalTextBloodType.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Hepatitis B Question
        val conditionalInputLayoutHepB = binding.firstvisitLayoutConditionalHepb
        val initialInputHepB = binding.firstvisitTextinputHepb
        val conditionalInputHepB = binding.firstvisitTextinputConditionalHepb
        val conditionalTextHepB = binding.firstvisitConditionalHepb

        initialInputHepB.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val initialInputText = s.toString()
                if (initialInputText.equals("unknown", ignoreCase = true)) {
                    conditionalInputLayoutHepB.visibility = View.VISIBLE
                    conditionalInputHepB.visibility = View.VISIBLE
                    conditionalTextHepB.visibility = View.VISIBLE
                } else {
                    conditionalInputLayoutHepB.visibility = View.GONE
                    conditionalInputHepB.visibility = View.GONE
                    conditionalTextHepB.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // HIV Question
        val conditionalInputLayoutHiv = binding.firstvisitLayoutConditionalHiv
        val initialInputHiv = binding.firstvisitTextinputHiv
        val conditionalInputHiv = binding.firstvisitTextinputConditionalHiv
        val conditionalTextHiv = binding.firstvisitConditionalHiv

        initialInputHiv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val initialInputText = s.toString()
                if (initialInputText.equals("unknown", ignoreCase = true)) {
                    conditionalInputLayoutHiv.visibility = View.VISIBLE
                    conditionalInputHiv.visibility = View.VISIBLE
                    conditionalTextHiv.visibility = View.VISIBLE
                } else {
                    conditionalInputLayoutHiv.visibility = View.GONE
                    conditionalInputHiv.visibility = View.GONE
                    conditionalTextHiv.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Syphilis Question
        val conditionalInputLayoutSyph = binding.firstvisitLayoutConditionalSyph
        val initialInputSyph = binding.firstvisitTextinputSyph
        val conditionalInputSyph = binding.firstvisitTextinputConditionalSyph
        val conditionalTextSyph = binding.firstvisitConditionalSyph
        initialInputSyph.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val initialInputText = s.toString()
                if (initialInputText.equals("unknown", ignoreCase = true)) {
                    conditionalInputLayoutSyph.visibility = View.VISIBLE
                    conditionalInputSyph.visibility = View.VISIBLE
                    conditionalTextSyph.visibility = View.VISIBLE
                } else {
                    conditionalInputLayoutSyph.visibility = View.GONE
                    conditionalInputSyph.visibility = View.GONE
                    conditionalTextSyph.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Calendar View: Preventing Manual User Input of Dates
        binding.texteditFirstvisitDuedate.isFocusable = false
        binding.layoutinputFirstvisitDuedate.isClickable = true

        // Calendar View Listening For Selecting Date Input
        binding.texteditFirstvisitDuedate.setOnClickListener {
            isDueDateFieldClicked = true
            showDatePickerDialog()
        }

        return root
    }

    // Drop Down Menus
    override fun onResume() {
        super.onResume()
        // Urinalysis
        val urinalysis = resources.getStringArray(R.array.firstvisit_dropdownoptions_ynu)
        val arrayAdapterUrinalysis =
            ArrayAdapter(requireContext(), R.layout.newpatientinputs_dropdownmenus, urinalysis)
        binding.firstvisitTextinputUrinalysis.setAdapter(arrayAdapterUrinalysis)

// Hemoglobin
        val hemoglobin =
            resources.getStringArray(R.array.firstvisit_dropdownoptions_ynu)
        val arrayAdapterHemoglobin =
            ArrayAdapter(
                requireContext(),
                R.layout.newpatientinputs_dropdownmenus,
                hemoglobin
            )
        binding.firstvisitTextinputHemoglobin.setAdapter(arrayAdapterHemoglobin)

        // Antibodies
        val antibodies =
            resources.getStringArray(R.array.firstvisit_dropdownoptions_ynu)
        val arrayAdapterAntibodies =
            ArrayAdapter(
                requireContext(),
                R.layout.newpatientinputs_dropdownmenus,
                antibodies
            )
        binding.firstvisitTextinputAntibodies.setAdapter(arrayAdapterAntibodies)

        // Bloodtype
        val bloodtype =
            resources.getStringArray(R.array.firstvisit_dropdownoptions_bloodtype)
        val arrayAdapterBloodType =
            ArrayAdapter(
                requireContext(),
                R.layout.newpatientinputs_dropdownmenus,
                bloodtype
            )
        binding.firstvisitTextinputBloodtype.setAdapter(arrayAdapterBloodType)

        // Conditional: Unknown Bloodtype
        val unkBloodType =
            resources.getStringArray(R.array.firstvisit_dropdownoptions_ynu)
        val arrayAdapterUnkBloodType = ArrayAdapter(
            requireContext(),
            R.layout.newpatientinputs_dropdownmenus,
            unkBloodType
        )
        binding.firstvisitTextinputConditionalBloodwork.setAdapter(
            arrayAdapterUnkBloodType
        )

        // Hep B
        val hepb = resources.getStringArray(R.array.firstvisit_dropdownoptions_ynu)
        val arrayAdapterHepb =
            ArrayAdapter(
                requireContext(),
                R.layout.newpatientinputs_dropdownmenus,
                hepb
            )
        binding.firstvisitTextinputHepb.setAdapter(arrayAdapterHepb)

        // Conditional: Unknown Hep B
        val unkHepB = resources.getStringArray(R.array.firstvisit_dropdownoptions_ynu)
        val arrayAdapterUnkHepB =
            ArrayAdapter(
                requireContext(),
                R.layout.newpatientinputs_dropdownmenus,
                unkHepB
            )
        binding.firstvisitTextinputConditionalHepb.setAdapter(arrayAdapterUnkHepB)

        // HIV
        val hiv = resources.getStringArray(R.array.firstvisit_dropdownoptions_ynu)
        val arrayAdapterHiv =
            ArrayAdapter(requireContext(), R.layout.newpatientinputs_dropdownmenus, hiv)
        binding.firstvisitTextinputHiv.setAdapter(arrayAdapterHiv)

        // Conditional: Unknown HIV
        val unkHiv = resources.getStringArray(R.array.firstvisit_dropdownoptions_ynu)
        val arrayAdapterUnkHiv =
            ArrayAdapter(
                requireContext(),
                R.layout.newpatientinputs_dropdownmenus,
                unkHiv
            )
        binding.firstvisitTextinputConditionalHiv.setAdapter(arrayAdapterUnkHiv)

        // Syphilis
        val syph = resources.getStringArray(R.array.firstvisit_dropdownoptions_ynu)
        val arrayAdapterSyph =
            ArrayAdapter(
                requireContext(),
                R.layout.newpatientinputs_dropdownmenus,
                syph
            )
        binding.firstvisitTextinputSyph.setAdapter(arrayAdapterSyph)

        // Conditional: Unknown Syphilis
        val unkSyph = resources.getStringArray(R.array.firstvisit_dropdownoptions_ynu)
        val arrayAdapterUnkSyph =
            ArrayAdapter(
                requireContext(),
                R.layout.newpatientinputs_dropdownmenus,
                unkSyph
            )
        binding.firstvisitTextinputConditionalSyph.setAdapter(arrayAdapterUnkSyph)

        val fetalposition =
            resources.getStringArray(R.array.firstvisit_dropdownoptions_fetalposition)
        val arrayAdapterFetalPosition =
            ArrayAdapter(
                requireContext(),
                R.layout.newpatientinputs_dropdownmenus,
                fetalposition
            )
        binding.firstvisitTextinputFetalposition.setAdapter(arrayAdapterFetalPosition)

        val fetalmove = resources.getStringArray(R.array.firstvisit_dropdownoptions_ynu)
        val arrayAdapterFetalMove =
            ArrayAdapter(
                requireContext(),
                R.layout.newpatientinputs_dropdownmenus,
                fetalmove
            )
        binding.firstvisitTextinputFetalmove.setAdapter(arrayAdapterFetalMove)
    }

    // Calendar View
    private fun showDatePickerDialog() {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val selectedDate = String.format(Locale.US, "%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
                if (isDueDateFieldClicked) {
                    binding.texteditFirstvisitDuedate.setText(selectedDate)
                }
            }, year, month, day
        )
        val minDate = currentDate.timeInMillis
        datePickerDialog.datePicker.minDate = minDate
        datePickerDialog.show()
    }

    // Alert System Functions
    // Temperature
    private fun showTemperatureConfirmationDialog(temperatureValue: Double) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Temperature Warning")
        alertDialogBuilder.setMessage("The inputted temperature ($temperatureValue °C) is outside the normal range (35 to 38 °C). Are you sure this is the intended value?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            alertsToSave.add("Temperature" to temperatureValue.toString())
        }
        alertDialogBuilder.setNegativeButton("No") { _, _ ->
            binding.texteditFirstvisitTemperature.text?.clear()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showBloodPressureConfirmationSystolic(systolicBloodPressureValue: Double) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Systolic Blood Pressure Warning")
        alertDialogBuilder.setMessage("The inputted systolic blood pressure value ($systolicBloodPressureValue mmHg) is outside the normal range (90-140 mmHg). Are you sure this is the intended value?")
        alertDialogBuilder.setPositiveButton("Yes"){_, _ ->
            alertsToSave.add("Systolic" to systolicBloodPressureValue.toString())

        }
        alertDialogBuilder.setNegativeButton("No"){_,_ ->
            binding.texteditFirstvisitSystolic.text?.clear()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showBloodPressureConfirmationDiastolic(diastolicBloodPressureValue: Double) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Diastolic Blood Pressure Warning")
        alertDialogBuilder.setMessage("The inputted diastolic blood pressure value ($diastolicBloodPressureValue mmHg) is outside the normal range (55-140 mmHg). Are you sure this is the intended value?")
        alertDialogBuilder.setPositiveButton("Yes"){_, _ ->
            alertsToSave.add("Diastolic" to diastolicBloodPressureValue.toString())

        }
        alertDialogBuilder.setNegativeButton("No"){_,_ ->
            binding.texteditFirstvisitDiastolic.text?.clear()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showHeartRateConfirmation(heartRateValue: Double) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Heart Rate Warning")
        alertDialogBuilder.setMessage("The inputted heart rate value ($heartRateValue mmHg) is outside the normal range (65 to 114 bpm). Are you sure this is the intended value?")
        alertDialogBuilder.setPositiveButton("Yes"){_, _ ->
            alertsToSave.add("Heart Rate" to heartRateValue.toString())
        }
        alertDialogBuilder.setNegativeButton("No"){_,_ ->
            binding.texteditFirstvisitHeartrate.text?.clear()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showFetalHRConfirmation(fetalHRValue: Double) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Fetal Heart Rate Warning")
        alertDialogBuilder.setMessage("The inputted fetal heart rate value ($fetalHRValue mmHg) is outside the normal range (110 to 160 bpm). Are you sure this is the intended value?")
        alertDialogBuilder.setPositiveButton("Yes"){_, _ ->
            alertsToSave.add("Fetal Heart Rate" to fetalHRValue.toString())
        }
        alertDialogBuilder.setNegativeButton("No"){_,_ ->
            binding.texteditFirstvisitFetalheartrate.text?.clear()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun saveAlerts(alertsToSave: List<Pair<String, String>>) {
        val patientId = arguments?.getString("patientId")
        val firestore = FirebaseFirestore.getInstance()
        val patientProfilesCollection = firestore.collection("Patient Profiles")
        val patientDocument = patientProfilesCollection.document(patientId ?: "")
        val prenatalVisitsCollection = patientDocument.collection("Child").document("Visits").collection("PrenatalVisits")
        val prenatalChildVisitDocument = prenatalVisitsCollection.document("PrenatalChildVisit1")
        val alertsCollection = prenatalChildVisitDocument.collection("Notifications")

        val alertsMap = HashMap<String, String>()
        for ((fieldName, alertValue) in alertsToSave) {
            alertsMap[fieldName] = alertValue
        }

        val batch = firestore.batch()

            val newAlertDocument = alertsCollection.document("Health Alerts")
            batch.set(newAlertDocument, alertsMap)

        batch.commit()
            .addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    "Health alerts saved successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Failed to save health alerts: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }}
