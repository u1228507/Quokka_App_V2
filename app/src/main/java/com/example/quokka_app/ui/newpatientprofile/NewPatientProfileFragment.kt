package com.example.quokka_app.ui.newpatientprofile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.example.quokka_app.R
import com.example.quokka_app.databinding.FragmentNewpatientprofilesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NewPatientProfileFragment : Fragment(R.layout.fragment_newpatientprofiles) {
    private var _binding: FragmentNewpatientprofilesBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewpatientprofilesBinding.inflate(inflater, container, false)
        val root: View = binding.root

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

                // Boolean Inputs:
                val lastmenstcycleText = binding.inputDropdownLastmenstcycle.text.toString()
                val isLastMenstCycleYes = lastmenstcycleText.equals("yes", ignoreCase = true)
                val motherbirthdefectText = binding.inputDropdownMotherbirthdefectYes.text.toString()
                val isMotherBirthDefectYes = motherbirthdefectText.equals("yes",ignoreCase = true)
                val firstpregText = binding.inputDropdownFirstpreg.text.toString()
                val isFirstPregYes = firstpregText.equals("yes",ignoreCase = true)
                val alcoholconsumpText = binding.inputDropdownAlcoholconsump.text.toString()
                val isAlcoholConsumpYes = alcoholconsumpText.equals("yes",ignoreCase = true)
                val smokingText = binding.inputDropdownSmoking.text.toString()
                val isSmokingYes = smokingText.equals("yes",ignoreCase = true)
                val drugsText = binding.inputDropdownDrugs.text.toString()
                val isDrugsYes = drugsText.equals("yes",ignoreCase = true)

                val newPatientProfile = NewPatienProfileDataClass(
                    First_Name = firstName,
                    Middle_Name = binding.textinputeditMiddlename.text.toString(),
                    Last_Name = lastName,
                    Date_Of_Birth = dateOfBirth,
                    lastmenstcycle = isLastMenstCycleYes,
                    lastmenstcycledate = binding.inputDropdownLastmenstcycleYes.text.toString(),
                    motherbirthdefect = isMotherBirthDefectYes,
                    motherbirthdefecttype = binding.inputDropdownMotherbirthdefectYes.text.toString(),
                    firstpregnancy = isFirstPregYes,
                    numpregn = binding.inputFirstpregNumprevpreg.text.toString(),
                    livingchildren = binding.inputFirstpregNumlivchil.text.toString(),
                    lowbirthweight = binding.inputFirstpregLowweight.text.toString(),
                    stillborns = binding.inputFirstpregStillborns.text.toString(),
                    miscarriages = binding.inputFirstpregMiscarriages.text.toString(),
                    csections = binding.inputFirstpregCsections.text.toString(),
                    postpartumhemorrhages = binding.inputFirstpregPostpartumhemorrages.text.toString(),
                    preginfections = binding.inputFirstpregPreginfections.text.toString(),
                    highBPpregn = binding.inputFirstpregHighbppregnacies.text.toString(),
                    othermedhist = binding.textinputeditPersonalmedicalother.text.toString(),
                    alcoholconsump = isAlcoholConsumpYes,
                    drinksperweek = binding.inputDropdownAlcoholconsumpYes.text.toString(),
                    smoking = isSmokingYes,
                    smokesperweek = binding.inputDropdownSmokingYes.text.toString(),
                    drugs = isDrugsYes,
                    drugtypes = binding.inputDropdownDrugsYes.text.toString(),
                    exercise = binding.inputExercise.text.toString()
                )
                    val profilesCollection = firestore.collection("Patient Profiles")

                // Check if a patient profile with the same information already exists
                profilesCollection
                    .whereEqualTo("firstname", newPatientProfile.First_Name)
                    .whereEqualTo("lastName", newPatientProfile.Last_Name)
                    .whereEqualTo("dateofbirth",newPatientProfile.Date_Of_Birth)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (querySnapshot.isEmpty) {
                            // No duplicate profile found, proceed to save
                            val newPatientDocumentRef = profilesCollection.document()
                            newPatientDocumentRef
                                .set(newPatientProfile)
                                .addOnSuccessListener {
                                    val subcollectionRef = newPatientDocumentRef.collection("PatientProfileInformation")
                                    subcollectionRef
                                        .add(newPatientProfile)
                                        .addOnSuccessListener { documentReference ->
                                            // Patient profile saved successfully
                                            val patientProfileId = documentReference.id
                                            binding.textinputeditFirstname.text?.clear()
                                            binding.textinputeditLastname.text?.clear()
                                            binding.textinputeditDob.text?.clear()
                                            Toast.makeText(requireContext(), "Patient profile saved successfully", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener { e ->
                                            // Handle the failure of subcollection add operation
                                            Toast.makeText(requireContext(), "Failed to save patient profile: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                }
                                .addOnFailureListener { e ->
                                    // Handle the failure of main document set operation
                                    Toast.makeText(requireContext(), "Failed to save patient profile: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            // A duplicate patient profile exists
                            Toast.makeText(requireContext(), "Patient profile already exists", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Query failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Display an error message if any of the required fields are empty
                    Toast.makeText(requireContext(), "Please fill out First Name, Last Name, and Date of Birth", Toast.LENGTH_SHORT).show()
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

        initialInputMothBirthDef.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val initialInputText = s.toString()
                if (initialInputText.equals("yes", ignoreCase = true)) {
                    conditionalInputLayoutMothBirthDef.visibility = View.VISIBLE
                    conditionalTextMothBirthDef.visibility = View.VISIBLE
                    conditionalInputMothBirthDef.visibility = View.VISIBLE
                } else{
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
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Alcohol Consumption Question
        val conditionalInputLayoutAlcConsump = binding.textinputlayoutAlcoholconsumpYes
        val initialInputAlcConsump = binding.inputDropdownAlcoholconsump
        val conditionalInputAlcConsump = binding.inputDropdownAlcoholconsumpYes
        val conditionalTextAlcConsump = binding.conditionaltextAlcoholconsump

        initialInputAlcConsump.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val initialInputText = s.toString()
                if (initialInputText.equals("yes", ignoreCase = true)) {
                    conditionalInputLayoutAlcConsump.visibility = View.VISIBLE
                    conditionalTextAlcConsump.visibility = View.VISIBLE
                    conditionalInputAlcConsump.visibility = View.VISIBLE
                } else{
                    conditionalInputLayoutAlcConsump.visibility = View.GONE
                    conditionalTextAlcConsump.visibility = View.GONE
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Smoking Question
        val conditionalInputLayoutSmoking = binding.textinputlayoutSmokingYes
        val initialInputSmoking = binding.inputDropdownSmoking
        val conditionalInputSmoking = binding.inputDropdownSmokingYes
        val conditionalTextSmoking = binding.conditionaltextSmoking

        initialInputSmoking.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val initialInputText = s.toString()
                if (initialInputText.equals("yes", ignoreCase = true)) {
                    conditionalInputLayoutSmoking.visibility = View.VISIBLE
                    conditionalTextSmoking.visibility = View.VISIBLE
                    conditionalInputSmoking.visibility = View.VISIBLE
                } else{
                    conditionalInputLayoutSmoking.visibility = View.GONE
                    conditionalTextSmoking.visibility = View.GONE
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

        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

}
