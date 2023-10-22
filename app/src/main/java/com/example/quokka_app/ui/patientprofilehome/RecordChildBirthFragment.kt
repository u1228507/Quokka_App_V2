package com.example.quokka_app.ui.patientprofilehome

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.quokka_app.R
import com.example.quokka_app.databinding.FragmentRecordChildBirthBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class RecordChildBirthFragment : Fragment() {
    private lateinit var binding: FragmentRecordChildBirthBinding
    private val calendar = Calendar.getInstance()
    private var isDOBFieldClicked: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecordChildBirthBinding.inflate(inflater, container, false)
        val view = binding.root
        val patientId = arguments?.getString("patientId") ?: ""

        binding.buttonSave.setOnClickListener {
            val firstName = binding.recordchildbirthTextinputeditFirstname.text.toString()
            val middleName = binding.recordchildbirthTextinputeditMiddlename.text.toString()
            val lastName = binding.recordchildbirthTextinputeditLastname.text.toString()
            val dateOfBirth = binding.recordchildbirthTextinputeditDob.text.toString()
            val sexOfChild = binding.recordchildbirthDropdownSexofchild.text.toString()
            val childPatientId = UUID.randomUUID().toString()


            // Error Messages:
            var isValid = true
            if (firstName.isEmpty()) {
                binding.recordchildbirthTextinputeditFirstname.error = "First Name is required"
                isValid = false
                Toast.makeText(requireContext(), "First Name is required", Toast.LENGTH_SHORT).show()
            }
            if (lastName.isEmpty()) {
                binding.recordchildbirthTextinputeditLastname.error = "Last Name is required"
                isValid = false
                Toast.makeText(requireContext(), "Last Name is required", Toast.LENGTH_SHORT).show()
            }
            if (dateOfBirth.isEmpty()) {
                binding.recordchildbirthTextinputeditDob.error = "Date of Birth is required"
                isValid = false
                Toast.makeText(requireContext(), "Date of Birth is required", Toast.LENGTH_SHORT).show()
            }

            if (firstName.isNotEmpty() && lastName.isNotEmpty() && dateOfBirth.isNotEmpty() && isValid) {
                val db = FirebaseFirestore.getInstance()
                val childGeneralInfoRef = db.collection("Patient Profiles")
                    .document(patientId)
                    .collection("Child")
                    .document("Child General Info")

                val data = hashMapOf(
                    "firstName" to firstName,
                    "middleName" to middleName,
                    "lastName" to lastName,
                    "dateOfBirth" to dateOfBirth,
                    "sexofchild" to sexOfChild,
                    "childpatientid" to childPatientId
                )

                childGeneralInfoRef.set(data)
                    .addOnSuccessListener { _ ->
                        Toast.makeText(
                            requireContext(),
                            "Newborn General Information saved successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        val motherProfileFragment = MotherProfileFragment()
                        val args = Bundle()
                        args.putString("patientId", patientId)
                        motherProfileFragment.arguments = args
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.patientprofilehome_fragmentContainer, motherProfileFragment)
                            .addToBackStack(null)
                            .commit()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            requireContext(),
                            "Failed to save Newborn General Information profile: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }

        binding.recordchildbirthTextinputeditDob.setOnClickListener {
            isDOBFieldClicked = true
            showDatePickerDialog()
        }

        return view
    }

    private fun showDatePickerDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val minYear = year - 1

        val datePickerDialog = DatePickerDialog(requireContext(),
            R.style.PurpleDatePickerDialog,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val selectedDate = String.format(Locale.US, "%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
                binding.recordchildbirthTextinputeditDob.setText(selectedDate)
            }, year, month, day)

        // Set max date
        datePickerDialog.datePicker.maxDate = calendar.timeInMillis

        // Set min date as one year back
        datePickerDialog.datePicker.minDate = Calendar.getInstance().apply {
            set(minYear, month, day)
        }.timeInMillis

        datePickerDialog.show()
    }

    override fun onResume() {
        super.onResume()
        val sexofchild = resources.getStringArray(R.array.motherprofile_recordchildbirth_ynu)
        val arrayAdapter =
            ArrayAdapter(requireContext(), R.layout.newpatientinputs_dropdownmenus, sexofchild)
        binding.recordchildbirthDropdownSexofchild.setAdapter(arrayAdapter)
    }


}
