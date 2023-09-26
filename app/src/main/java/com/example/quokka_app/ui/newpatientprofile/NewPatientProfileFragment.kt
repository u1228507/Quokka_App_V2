package com.example.quokka_app.ui.newpatientprofile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.example.quokka_app.R
import com.example.quokka_app.databinding.FragmentNewpatientprofilesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import android.app.DatePickerDialog
import android.widget.DatePicker
import java.util.Calendar
import java.util.Locale


class NewPatientProfileFragment : Fragment(R.layout.fragment_newpatientprofiles) {
    private var _binding: FragmentNewpatientprofilesBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var imagePicker: ActivityResultLauncher<Intent>
    private val calendar = Calendar.getInstance()
    private var selectedImageURI: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewpatientprofilesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Patient Profile Image
        imagePicker = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult())
        {result ->
            if(result.resultCode == Activity.RESULT_OK){
                val data : Intent? = result.data
                if(data != null) {
                    selectedImageURI = data.data
                    binding.PatientProfilePicture.setImageURI(selectedImageURI)
                }
            }
        }

        // Select image when button is clicked
        binding.buttonPatientprofileimage.setOnClickListener{
            selectImage()
        }

        // Save Button Listener
        auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        binding.buttonSave.setOnClickListener {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val firstName = binding.textinputeditFirstname.text.toString()
                val lastName = binding.textinputeditLastname.text.toString()
                val dateOfBirth = binding.textinputeditDob.text.toString()
                if (firstName.isNotEmpty() && lastName.isNotEmpty() && dateOfBirth.isNotEmpty()) {
                    val lastmenstcycleText = binding.inputDropdownLastmenstcycle.text.toString()
                    val motherbirthdefectText = binding.inputDropdownMotherbirthdefect.text.toString()
                    val firstpregText = binding.inputDropdownFirstpreg.text.toString()
                    val alcoholconsumpText = binding.inputDropdownAlcoholconsump.text.toString()
                    val smokingText = binding.inputDropdownSmoking.text.toString()
                    val drugsText = binding.inputDropdownDrugs.text.toString()
                    val pregseizuresText = binding.inputDropdownSeizures.text.toString()

                    val newPatientProfile = NewPatienProfileDataClass(
                        firstname = firstName,
                        middlename = binding.textinputeditMiddlename.text.toString(),
                        lastname = lastName,
                        dateofbirth = dateOfBirth,
                        mothersvillage = binding.textinputeditMothersvillage.text.toString(),
                        mothersphonenumber = binding.textinputeditMotherscontactnumber.text.toString(),
                        fathersfirstname = binding.textinputeditFathersfirstname.text.toString(),
                        fathersmiddlename = binding.textinputeditFathermiddlename.text.toString(),
                        fatherslastname = binding.textinputeditFatherlastname.text.toString(),
                        fathersvillage = binding.textinputeditFathersvillage.text.toString(),
                        fathersphonenumber = binding.textinputeditFatherscontactnumber.text.toString(),
                        fchwfirstname = binding.textinputeditFchwfirstname.text.toString(),
                        fchwlastname = binding.textinputeditFchwfirstname.text.toString(),
                        fchwphonenumber = binding.textinputeditFchwcontactnumber.text.toString(),
                        lastmenstcycle = lastmenstcycleText,
                        lastmenstcycledate = binding.inputDropdownLastmenstcycleYes.text.toString(),
                        motherbirthdefect = motherbirthdefectText,
                        motherbirthdefecttype = binding.inputDropdownMotherbirthdefectYes.text.toString(),
                        firstpregnancy = firstpregText,
                        numpregn = binding.inputFirstpregNumprevpreg.text.toString(),
                        livingchildren = binding.inputFirstpregNumlivchil.text.toString(),
                        lowbirthweight = binding.inputFirstpregLowweight.text.toString(),
                        stillborns = binding.inputFirstpregStillborns.text.toString(),
                        miscarriages = binding.inputFirstpregMiscarriages.text.toString(),
                        csections = binding.inputFirstpregCsections.text.toString(),
                        postpartumhemorrhages = binding.inputFirstpregPostpartumhemorrages.text.toString(),
                        preginfections = binding.inputFirstpregPreginfections.text.toString(),
                        highBPpregn = binding.inputFirstpregHighbppregnacies.text.toString(),
                        pregseizures = pregseizuresText,
                        othermedhist = binding.textinputeditPersonalmedicalother.text.toString(),
                        alcoholconsump = alcoholconsumpText,
                        smoking = smokingText,
                        drugs = drugsText,
                        drugtypes = binding.inputDropdownDrugsYes.text.toString(),
                    )
                    if (selectedImageURI != null) {
                        val timeStamp = System.currentTimeMillis().toString()
                        val storageRef = FirebaseStorage.getInstance().reference
                        val foldername = "Patient Profile Images"
                        val imageRef = storageRef.child("$foldername/$userId/$timeStamp")
                        imageRef.putFile(selectedImageURI!!)
                            .addOnSuccessListener { _ ->
                                imageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                                    newPatientProfile.imageUrl = imageUrl.toString()

                                    val profilesCollection =
                                        firestore.collection("Patient Profiles")

                                    // Check if a patient profile with the same information already exists
                                    profilesCollection
                                        .whereEqualTo("firstname", newPatientProfile.firstname)
                                        .whereEqualTo("lastName", newPatientProfile.lastname)
                                        .whereEqualTo("dateofbirth", newPatientProfile.dateofbirth)
                                        .get()
                                        .addOnSuccessListener { querySnapshot ->
                                            if (querySnapshot.isEmpty) {
                                                // No duplicate profile found, proceed to save
                                                val newPatientDocumentRef =
                                                    profilesCollection.document()
                                                newPatientDocumentRef
                                                    .set(newPatientProfile)
                                                    .addOnSuccessListener {
                                                        val subcollectionRef =
                                                            newPatientDocumentRef.collection("PatientProfileInformation")
                                                        subcollectionRef
                                                            .add(newPatientProfile)
                                                            .addOnSuccessListener { _ ->
                                                                // Patient profile saved successfully
                                                                    // Clear all input fields when saved:
                                                                binding.textinputeditFirstname.text?.clear()
                                                                binding.textinputeditMiddlename.text?.clear()
                                                                binding.textinputeditLastname.text?.clear()
                                                                binding.textinputeditDob.text?.clear()
                                                                binding.textinputeditMothersvillage.text?.clear()
                                                                binding.textinputeditMotherscontactnumber.text?.clear()
                                                                binding.textinputeditFathersfirstname.text?.clear()
                                                                binding.textinputeditFathermiddlename.text?.clear()
                                                                binding.textinputeditFatherlastname.text?.clear()
                                                                binding.textinputeditFathersvillage.text?.clear()
                                                                binding.textinputeditFatherscontactnumber.text?.clear()
                                                                binding.textinputeditFchwfirstname.text?.clear()
                                                                binding.textinputeditFchwlastname.text?.clear()
                                                                binding.textinputeditFchwcontactnumber.text?.clear()
                                                                binding.inputDropdownLastmenstcycle.text?.clear()
                                                                binding.inputDropdownMotherbirthdefect.text?.clear()
                                                                binding.inputFirstpregNumprevpreg.text?.clear()
                                                                binding.inputFirstpregNumlivchil.text?.clear()
                                                                binding.inputFirstpregLowweight.text?.clear()
                                                                binding.inputFirstpregStillborns.text?.clear()
                                                                binding.inputFirstpregMiscarriages.text?.clear()
                                                                binding.inputFirstpregCsections.text?.clear()
                                                                binding.inputFirstpregPostpartumhemorrages.text?.clear()
                                                                binding.inputFirstpregPreginfections.text?.clear()
                                                                binding.inputFirstpregHighbppregnacies.text?.clear()
                                                                binding.textinputeditPersonalmedicalother.text?.clear()
                                                                binding.inputDropdownDrugsYes.text?.clear()
                                                                binding.inputDropdownLastmenstcycle.setText("Unknown", false)
                                                                binding.inputDropdownMotherbirthdefect.setText("Unknown", false)
                                                                binding.inputDropdownFirstpreg.setText("Unknown", false)
                                                                binding.inputDropdownAlcoholconsump.setText("Unknown", false)
                                                                binding.inputDropdownSmoking.setText("Unknown", false)
                                                                binding.inputDropdownDrugs.setText("Unknown", false)
                                                                binding.inputDropdownDrugs.setAdapter(null) // Clear any suggestions
                                                                binding.inputDropdownLastmenstcycle.setAdapter(null)
                                                                binding.inputDropdownMotherbirthdefect.setAdapter(null)
                                                                selectedImageURI = null
                                                                binding.PatientProfilePicture.setImageResource(R.drawable.baseline_person_24_purple)

                                                                Toast.makeText(
                                                                    requireContext(),
                                                                    "Patient profile saved successfully",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                            .addOnFailureListener { e ->
                                                                Toast.makeText(
                                                                    requireContext(),
                                                                    "Failed to save patient profile: ${e.message}",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                    }
                                                    .addOnFailureListener { e ->
                                                        Toast.makeText(
                                                            requireContext(),
                                                            "Failed to save patient profile: ${e.message}",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }


                                            } else {
                                                // A duplicate patient profile exists
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Patient profile already exists",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(
                                                requireContext(),
                                                "Query failed: ${e.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                            }
                    } else {
                        // Continue with saving the patient profile data without an image
                    }
                }
            }
        }

        // Defines Errors For Text Inputs:
              // First Name Error:
        binding.textinputeditFirstname.doOnTextChanged { text, _, _, _ ->
            if(text!!.length > 30) { binding.textinputlayoutFirstname.error = "Error: Too Many Characters"
        } else if(text.length < 30){
            binding.textinputlayoutFirstname.error = null }
        }

        // Conditional Visibility Logic
            // Menstrual Cycle Question
        val conditionalInputLayoutMenst = binding.textinputlayoutLastmenstcycleYes
        val initialInputMenst = binding.inputDropdownLastmenstcycle
        val conditionalInputMenst = binding.inputDropdownLastmenstcycleYes
        val conditionalTextMenst = binding.conditionaltextLastmenstcycle

        initialInputMenst.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val initialInputText = s.toString()
                    if (initialInputText.equals("yes", ignoreCase = true)) {
                        conditionalInputLayoutMenst.visibility = View.VISIBLE
                        conditionalTextMenst.visibility = View.VISIBLE
                        conditionalInputMenst.visibility = View.VISIBLE
                    } else{
                        conditionalInputLayoutMenst.visibility = View.GONE
                        conditionalTextMenst.visibility = View.GONE
                    }
                }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Mother Birth Defect Question
        val conditionalInputLayoutMothBirthDef = binding.textinputlayoutMotherbirthdefectYes
        val initialInputMothBirthDef = binding.inputDropdownMotherbirthdefect
        val conditionalInputMothBirthDef = binding.inputDropdownMotherbirthdefectYes
        val conditionalTextMothBirthDef = binding.conditionaltextMotherbirthdefect

        initialInputMothBirthDef.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val initialInputText = s.toString()
                if (initialInputText.equals("yes", ignoreCase = true)) {
                    conditionalInputLayoutMothBirthDef.visibility = View.VISIBLE
                    conditionalTextMothBirthDef.visibility = View.VISIBLE
                    conditionalInputMothBirthDef.visibility = View.VISIBLE
                } else {
                    conditionalInputLayoutMothBirthDef.visibility = View.GONE
                    conditionalTextMothBirthDef.visibility = View.GONE
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })


        // First Pregnancy Questions
        val initialInputFirstPreg = binding.inputDropdownFirstpreg
        val conditionalInputLayoutFirstPreg = binding.textinputlayoutFirstpregNumprevpreg
        val conditionalInputFirstPregNumPrevPreg = binding.inputFirstpregNumprevpreg
        val conditionalInputLayoutNumLivChild = binding.textinputlayoutFirstpregNumlivchild
        val conditionalInputFirstPregNumLivChild = binding.inputFirstpregNumlivchil
        val conditionalInputLayoutLowWeight = binding.textinputlayoutFirstpregLowweight
        val conditionalInputFirstPregLowWeight = binding.inputFirstpregLowweight
        val conditionalInputLayoutStillborns = binding.textinputlayoutFirstpregStillborns
        val conditionalInputFirstPregStillborns = binding.inputFirstpregStillborns
        val conditionalInputLayoutMiscarriages = binding.textinputlayoutFirstpregMiscarriages
        val conditionalInputFirstPregMiscarriages = binding.inputFirstpregMiscarriages
        val conditionalInputLayoutCSections = binding.textinputlayoutFirstpregCsections
        val conditionalInputFirstPregCSections = binding.inputFirstpregCsections
        val conditionalInputLayoutPostpartumHemorrhages = binding.textinputlayoutFirstpregPostpartumhemorrhages
        val conditionalInputFirstPregPostpartumHemorrhages = binding.inputFirstpregPostpartumhemorrages
        val conditionalInputLayoutPregInfections = binding.textinputlayoutFirstpregPreginfections
        val conditionalInputFirstPregPregInfections = binding.inputFirstpregPreginfections
        val conditionalInputLayoutHighBPPregnancies = binding.textinputlayoutFirstpregHighbppregnancies
        val conditionalInputFirstPregHighBPPregnancies = binding.inputFirstpregHighbppregnacies
        val conditionalInputLayoutPregSeizures = binding.textinputlayoutPregseizures
        val conditionalInputFirstPregSeizures = binding.inputDropdownSeizures
        val conditionalInputEditPregSeizures = binding.inputquestionPregseizures

        initialInputFirstPreg.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val initialInputText = s.toString()
                if (initialInputText.equals("no", ignoreCase = true)) {
                    conditionalInputLayoutFirstPreg.visibility = View.VISIBLE
                    conditionalInputFirstPregNumPrevPreg.visibility = View.VISIBLE
                    conditionalInputLayoutNumLivChild.visibility = View.VISIBLE
                    conditionalInputFirstPregNumLivChild.visibility = View.VISIBLE
                    conditionalInputLayoutLowWeight.visibility = View.VISIBLE
                    conditionalInputFirstPregLowWeight.visibility = View.VISIBLE
                    conditionalInputLayoutStillborns.visibility = View.VISIBLE
                    conditionalInputFirstPregStillborns.visibility = View.VISIBLE
                    conditionalInputLayoutMiscarriages.visibility = View.VISIBLE
                    conditionalInputFirstPregMiscarriages.visibility = View.VISIBLE
                    conditionalInputLayoutCSections.visibility = View.VISIBLE
                    conditionalInputFirstPregCSections.visibility = View.VISIBLE
                    conditionalInputLayoutPostpartumHemorrhages.visibility = View.VISIBLE
                    conditionalInputFirstPregPostpartumHemorrhages.visibility = View.VISIBLE
                    conditionalInputLayoutPregInfections.visibility = View.VISIBLE
                    conditionalInputFirstPregPregInfections.visibility = View.VISIBLE
                    conditionalInputLayoutHighBPPregnancies.visibility = View.VISIBLE
                    conditionalInputFirstPregHighBPPregnancies.visibility = View.VISIBLE
                    conditionalInputFirstPregSeizures.visibility = View.VISIBLE
                    conditionalInputLayoutPregSeizures.visibility = View.VISIBLE
                    conditionalInputEditPregSeizures.visibility = View.VISIBLE

                } else{
                    conditionalInputLayoutFirstPreg.visibility = View.GONE
                    conditionalInputLayoutNumLivChild.visibility = View.GONE
                    conditionalInputLayoutLowWeight.visibility = View.GONE
                    conditionalInputLayoutStillborns.visibility = View.GONE
                    conditionalInputLayoutMiscarriages.visibility = View.GONE
                    conditionalInputLayoutCSections.visibility = View.GONE
                    conditionalInputLayoutPostpartumHemorrhages.visibility = View.GONE
                    conditionalInputLayoutPregInfections.visibility = View.GONE
                    conditionalInputLayoutHighBPPregnancies.visibility = View.GONE
                    conditionalInputFirstPregSeizures.visibility = View.GONE
                    conditionalInputLayoutPregSeizures.visibility = View.GONE
                    conditionalInputEditPregSeizures.visibility = View.GONE
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })


        // Recreational Drugs Question
        val conditionalInputLayoutDrugs = binding.textinputlayoutDrugsYes
        val initialInputDrugs = binding.inputDropdownDrugs
        val conditionalInputDrugs = binding.inputDropdownDrugsYes
        val conditionalTextDrugs = binding.conditionaltextDrugs

        initialInputDrugs.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val initialInputText = s.toString()
                if (initialInputText.equals("yes", ignoreCase = true)) {
                    conditionalInputLayoutDrugs.visibility = View.VISIBLE
                    conditionalTextDrugs.visibility = View.VISIBLE
                    conditionalInputDrugs.visibility = View.VISIBLE
                } else{
                    conditionalInputLayoutDrugs.visibility = View.GONE
                    conditionalTextDrugs.visibility = View.GONE
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Calendar View: Preventing Manual User Input of Dates
        binding.textinputeditDob.isFocusable = false
        binding.textinputlayoutDob.isClickable = true
        binding.inputDropdownLastmenstcycleYes.isFocusable = false
        binding.textinputlayoutLastmenstcycle.isClickable = true

        // Calendar View Listening For Selecting Date Input
        binding.textinputeditDob.setOnClickListener {
            showDatePickerDialog()
        }
        binding.inputDropdownLastmenstcycleYes.setOnClickListener {
            showDatePickerDialog()
        }

        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Calendar View
    private fun showDatePickerDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(),
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val selectedDate = String.format(Locale.US, "%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
                binding.textinputeditDob.setText(selectedDate)
            }, year, month, day)

        datePickerDialog.datePicker.maxDate = calendar.timeInMillis // Set max date as today
        datePickerDialog.show()
    }

    // Drop Down Menus
    override fun onResume() {
        super.onResume()
        // Last Menstrual Cycle
        val lastmenstcycle = resources.getStringArray(R.array.newpatient_input_lastmenstcycle)
        val arrayAdapter = ArrayAdapter(requireContext(),R.layout.newpatientinputs_dropdownmenus, lastmenstcycle)
        binding.inputDropdownLastmenstcycle.setAdapter(arrayAdapter)

        // Mother Birth Defects
        val motherbirthdefects = resources.getStringArray(R.array.newpatient_input_motherbirthdefect)
        val motherBirthDefectsArrayAdapter = ArrayAdapter(requireContext(), R.layout.newpatientinputs_dropdownmenus, motherbirthdefects)
        binding.inputDropdownMotherbirthdefect.setAdapter(motherBirthDefectsArrayAdapter)

        // First Pregnancy
        val firstpregnancy = resources.getStringArray(R.array.newpatient_input_firstpreg)
        val firstpregArrayAdapter = ArrayAdapter(requireContext(), R.layout.newpatientinputs_dropdownmenus, firstpregnancy)
        binding.inputDropdownFirstpreg.setAdapter(firstpregArrayAdapter)

        // Pregnancy Seizures
        val pregseizures = resources.getStringArray(R.array.newpatient_input_pregseizures)
        val pregseizuresArrayAdapter = ArrayAdapter(requireContext(),R.layout.newpatientinputs_dropdownmenus, pregseizures)
        binding.inputDropdownSeizures.setAdapter(pregseizuresArrayAdapter)

        // Alcohol Consumption
        val alcoholconsump = resources.getStringArray(R.array.newpatient_input_alcoholconsump)
        val alcoholconsumpArrayAdapter = ArrayAdapter(requireContext(),R.layout.newpatientinputs_dropdownmenus, alcoholconsump)
        binding.inputDropdownAlcoholconsump.setAdapter(alcoholconsumpArrayAdapter)

        // Smoking Habits
        val smoking = resources.getStringArray(R.array.newpatient_input_smoking)
        val smokingArrayAdapter = ArrayAdapter(requireContext(),R.layout.newpatientinputs_dropdownmenus, smoking)
        binding.inputDropdownSmoking.setAdapter(smokingArrayAdapter)

        // Recreational Drug Habits
        val drugs = resources.getStringArray(R.array.newpatient_input_drugs)
        val drugsArrayAdapter = ArrayAdapter(requireContext(),R.layout.newpatientinputs_dropdownmenus, drugs)
        binding.inputDropdownDrugs.setAdapter(drugsArrayAdapter)
    }

    // Select Image for Patient Profile:
    private fun selectImage(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePicker.launch(intent)
    }

}

