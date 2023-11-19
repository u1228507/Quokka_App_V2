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
import android.widget.ProgressBar
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.SetOptions
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber
import com.google.i18n.phonenumbers.NumberParseException

class NewPatientProfileFragment : Fragment(R.layout.fragment_newpatientprofiles) {
    private var _binding: FragmentNewpatientprofilesBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var imagePicker: ActivityResultLauncher<Intent>
    private val calendar = Calendar.getInstance()
    private var selectedImageURI: Uri? = null
    private var isDOBFieldClicked = false
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewpatientprofilesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        progressBar = binding.newpatientprofilesProgressBar

        // Patient Profile Image
        imagePicker = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult())
        { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    selectedImageURI = data.data
                    binding.PatientProfilePicture.setImageURI(selectedImageURI)
                }
            }
        }
        // Select image when the button is clicked
        binding.buttonPatientprofileimage.setOnClickListener {
            selectImage()
        }

     // Phone Number Validation
        // Default regions for validation
        val defaultRegionIndia = "IN"
        val defaultRegionUS = "US"

        binding.textinputeditMotherscontactnumber.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val mothersPhoneNumber = binding.textinputeditMotherscontactnumber.text.toString()
                if (!isPhoneNumberValid(mothersPhoneNumber, defaultRegionIndia) && !isPhoneNumberValid(mothersPhoneNumber, defaultRegionUS)) {
                    binding.textinputeditMotherscontactnumber.error = "Invalid Phone Number"
                } else {
                    binding.textinputeditMotherscontactnumber.error = null
                }
            }
        }

        binding.textinputeditFchwcontactnumber.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val fchwPhoneNumber = binding.textinputeditFchwcontactnumber.text.toString()
                if (!isPhoneNumberValid(fchwPhoneNumber, defaultRegionIndia) && !isPhoneNumberValid(fchwPhoneNumber, defaultRegionUS)) {
                    binding.textinputeditFchwcontactnumber.error = "Invalid Phone Number"
                } else {
                    binding.textinputeditFchwcontactnumber.error = null
                }
            }
        }

        // Save Button Listener
        auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        binding.buttonSave.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val userId = auth.currentUser?.uid
            val firstName = binding.textinputeditFirstname.text.toString()
            val middleName = binding.textinputeditMiddlename.text.toString()
            val lastName = binding.textinputeditLastname.text.toString()
            val dateOfBirth = binding.textinputeditDob.text.toString()
            val mothersVillage = binding.textinputeditMothersvillage.text.toString()
            val mothersPhoneNumber = binding.textinputeditMotherscontactnumber.text.toString()
            val fchwFirstName = binding.textinputeditFchwfirstname.text.toString()
            val fchwLastName = binding.textinputeditFchwlastname.text.toString()
            val fchwPhoneNumber = binding.textinputeditFchwcontactnumber.text.toString()
            val fathersPhoneNumber = binding.textinputeditFatherscontactnumber.text.toString()

            // Validate phone numbers
            var isValid = true
            val isMothersPhoneNumberValid = isPhoneNumberValid(mothersPhoneNumber, defaultRegionIndia) || isPhoneNumberValid(mothersPhoneNumber, defaultRegionUS)
            val isFchwPhoneNumberValid = isPhoneNumberValid(fchwPhoneNumber, defaultRegionIndia) || isPhoneNumberValid(fchwPhoneNumber, defaultRegionUS)
            val isFathersPhoneNumberValid = isPhoneNumberValid(fathersPhoneNumber, defaultRegionIndia) || isPhoneNumberValid(fathersPhoneNumber, defaultRegionUS)

            if (!isMothersPhoneNumberValid) {
                binding.textinputeditMotherscontactnumber.error = "Invalid Phone Number"
                isValid = false
            }
            if (!isFchwPhoneNumberValid) {
                binding.textinputeditFchwcontactnumber.error = "Invalid Phone Number"
                isValid = false
            }
            if (fathersPhoneNumber.isNotEmpty()) {
                if (!isFathersPhoneNumberValid) {
                    binding.textinputeditFatherscontactnumber.error = "Invalid phone number format"
                    isValid = false
                } else {
                    binding.textinputeditFatherscontactnumber.error = null
                }
            }

            if (firstName.isEmpty()) {
                binding.textinputeditFirstname.error = "First Name is required"
                Toast.makeText(requireContext(), "First Name is required", Toast.LENGTH_SHORT).show()
            }
            if (lastName.isEmpty()) {
                binding.textinputeditLastname.error = "Last Name is required"
                Toast.makeText(requireContext(), "Last Name is required", Toast.LENGTH_SHORT).show()
            }
            if (dateOfBirth.isEmpty()) {
                binding.textinputeditDob.error = "Date of Birth is required"
                Toast.makeText(requireContext(), "Date of Birth is required", Toast.LENGTH_SHORT).show()
            }
            if (mothersVillage.isEmpty()) {
                binding.textinputeditMothersvillage.error = "Mother's Village is required"
                Toast.makeText(requireContext(), "Mother's Village is required", Toast.LENGTH_SHORT).show()
            }
            if (mothersPhoneNumber.isEmpty()) {
                binding.textinputeditMotherscontactnumber.error = "Mother's Phone Number is required"
                Toast.makeText(requireContext(), "Mother's Phone Number is required", Toast.LENGTH_SHORT).show()

            }
            if (fchwFirstName.isEmpty()) {
                binding.textinputeditFchwfirstname.error = "FCHW First Name is required"
                Toast.makeText(requireContext(), "FCHW First Name is required", Toast.LENGTH_SHORT).show()

            }
            if (fchwLastName.isEmpty()) {
                binding.textinputeditFchwlastname.error = "FCHW Last Name is required"
                Toast.makeText(requireContext(), "FCHW Last Name is required", Toast.LENGTH_SHORT).show()

            }
            if (fchwPhoneNumber.isEmpty()) {
                binding.textinputeditFchwcontactnumber.error = "FCHW Phone Number is required"
                Toast.makeText(requireContext(), "FCHW Phone Number is required", Toast.LENGTH_SHORT).show()
            }

            if (firstName.isNotEmpty() && lastName.isNotEmpty() && dateOfBirth.isNotEmpty() && mothersVillage.isNotEmpty() && mothersPhoneNumber.isNotEmpty() && fchwFirstName.isNotEmpty() && fchwLastName.isNotEmpty() && fchwPhoneNumber.isNotEmpty() && isValid) {
                val patientId = UUID.randomUUID().toString()
                val patientProfilesCollection = firestore.collection("Patient Profiles")
                val patientDocument = patientProfilesCollection.document(patientId)
                val patientInformationCollection = patientDocument.collection("Patient Profile Information")
                val generalInfoDocument = patientInformationCollection.document("General Info")

                val data = hashMapOf(
                    "patientId" to patientId,
                    "firstName" to firstName,
                    "middleName" to middleName,
                    "lastName" to lastName)
                patientDocument.set(data, SetOptions.merge())

                val lastmenstcycleText = binding.inputDropdownLastmenstcycle.text.toString()
                val motherbirthdefectText = binding.inputDropdownMotherbirthdefect.text.toString()
                val firstpregText = binding.inputDropdownFirstpreg.text.toString()
                val alcoholconsumpText = binding.inputDropdownAlcoholconsump.text.toString()
                val smokingText = binding.inputDropdownSmoking.text.toString()
                val drugsText = binding.inputDropdownDrugs.text.toString()
                val pregseizuresText = binding.inputDropdownSeizures.text.toString()
                var newPatientProfile = NewPatienProfileDataClass(
                    patientId = patientId,
                    firstname = firstName,
                    middlename = middleName,
                    lastname = lastName,
                    dateofbirth = dateOfBirth,
                    mothersvillage = mothersVillage,
                    mothersphonenumber = mothersPhoneNumber,
                    fathersfirstname = binding.textinputeditFathersfirstname.text.toString(),
                    fathersmiddlename = binding.textinputeditFathermiddlename.text.toString(),
                    fatherslastname = binding.textinputeditFatherlastname.text.toString(),
                    fathersvillage = binding.textinputeditFathersvillage.text.toString(),
                    fathersphonenumber = binding.textinputeditFatherscontactnumber.text.toString(),
                    fchwfirstname = fchwFirstName,
                    fchwlastname = fchwLastName,
                    fchwphonenumber = fchwPhoneNumber,
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
                val profilesCollection = firestore.collection("Patient Profiles")

                if (selectedImageURI != null) {
                    val timeStamp = System.currentTimeMillis().toString()
                    val storageRef = FirebaseStorage.getInstance().reference
                    val foldername = "Patient Profile Images"
                    val imageRef = storageRef.child("$foldername/$userId/$timeStamp")
                    imageRef.putFile(selectedImageURI!!)
                        .addOnSuccessListener { _ ->
                            imageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                                newPatientProfile = newPatientProfile.copy(imageUrl = imageUrl.toString())

                                // Check if a patient profile with the same information already exists
                                profilesCollection
                                    .whereEqualTo("firstname", newPatientProfile.firstname)
                                    .whereEqualTo("lastname", newPatientProfile.lastname)
                                    .whereEqualTo("dateofbirth", newPatientProfile.dateofbirth)
                                    .get()
                                    .addOnSuccessListener { querySnapshot ->
                                        if (querySnapshot.isEmpty) {
                                            // No duplicate profile found, proceed to save
                                            generalInfoDocument.set(newPatientProfile).addOnSuccessListener {
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
                                                binding.inputDropdownLastmenstcycle.setText(R.string.newpatient_input_unknown)
                                                binding.inputDropdownMotherbirthdefect.setText(R.string.newpatient_input_unknown)
                                                binding.inputDropdownFirstpreg.setText(R.string.newpatient_input_unknown)
                                                binding.inputDropdownAlcoholconsump.setText(R.string.newpatient_input_unknown)
                                                binding.inputDropdownSmoking.setText(R.string.newpatient_input_unknown)
                                                binding.inputDropdownDrugs.setText(R.string.newpatient_input_unknown)
                                                binding.inputDropdownDrugs.setAdapter(null) // Clear any suggestions
                                                binding.inputDropdownLastmenstcycle.setAdapter(null)
                                                binding.inputDropdownMotherbirthdefect.setAdapter(null)
                                                selectedImageURI = null
                                                binding.PatientProfilePicture.setImageResource(R.drawable.baseline_person_24_purple)
                                                progressBar.visibility = View.GONE

                                                val bundle = Bundle()
                                                bundle.putString("patientId", patientId)
                                                bundle.putString("firstname", firstName)
                                                bundle.putString("lastname", lastName)
                                                bundle.putString("imageUrl", imageUrl.toString())
                                                bundle.putString("dateofbirth", dateOfBirth)
                                                findNavController().navigate(R.id.action_newPatientProfileFragment_to_firstVisitFragment, bundle)

                                                Toast.makeText(requireContext(), "Patient profile saved successfully", Toast.LENGTH_SHORT).show()
                                            }
                                                .addOnFailureListener { e ->
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
                            }
                        }
                } else {
                    generalInfoDocument.set(newPatientProfile).addOnSuccessListener{
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
                        binding.inputDropdownLastmenstcycle.setText(R.string.newpatient_input_unknown)
                        binding.inputDropdownMotherbirthdefect.setText(R.string.newpatient_input_unknown)
                        binding.inputDropdownFirstpreg.setText(R.string.newpatient_input_unknown)
                        binding.inputDropdownAlcoholconsump.setText(R.string.newpatient_input_unknown)
                        binding.inputDropdownSmoking.setText(R.string.newpatient_input_unknown)
                        binding.inputDropdownDrugs.setText(R.string.newpatient_input_unknown)
                        binding.inputDropdownDrugs.setAdapter(null) // Clear any suggestions
                        binding.inputDropdownLastmenstcycle.setAdapter(null)
                        binding.inputDropdownMotherbirthdefect.setAdapter(null)
                        selectedImageURI = null
                        binding.PatientProfilePicture.setImageResource(R.drawable.baseline_person_24_purple)
                        progressBar.visibility = View.GONE

                        val bundle = Bundle()
                        bundle.putString("patientId", patientId)
                        bundle.putString("firstname", firstName)
                        bundle.putString("lastname", lastName)
                        bundle.putString("dateofbirth", dateOfBirth)
                        findNavController().navigate(R.id.action_newPatientProfileFragment_to_firstVisitFragment, bundle)

                        Toast.makeText(requireContext(), "Patient profile saved successfully", Toast.LENGTH_SHORT).show()
                    }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Failed to save patient profile: ${e.message}", Toast.LENGTH_SHORT).show()
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
        // Middle Name Error:
        binding.textinputeditMiddlename.doOnTextChanged { text, _, _, _ ->
            if(text!!.length > 30) { binding.textinputlayoutMiddlename.error = "Error: Too Many Characters"
            } else if(text.length < 30){
                binding.textinputlayoutMiddlename.error = null }
        }
        // Last Name Error:
        binding.textinputeditLastname.doOnTextChanged { text, _, _, _ ->
            if(text!!.length > 30) { binding.textinputlayoutLastname.error = "Error: Too Many Characters"
            } else if(text.length < 30){
                binding.textinputlayoutLastname.error = null }
        }

        // Mothers Village Error:
        binding.textinputeditMothersvillage.doOnTextChanged { text, _, _, _ ->
            if(text!!.length > 50) { binding.textinputlayoutMothervillage.error = "Error: Too Many Characters"
            } else if(text.length < 50){
                binding.textinputlayoutMothervillage.error = null }
        }

        // Fathers First Name:
        binding.textinputeditFathersfirstname.doOnTextChanged { text, _, _, _ ->
            if(text!!.length > 30) { binding.textinputlayoutFatherfirstname.error = "Error: Too Many Characters"
            } else if(text.length < 30){
                binding.textinputlayoutFatherfirstname.error = null }
        }

        // Fathers Middle Name:
        binding.textinputeditFathermiddlename.doOnTextChanged { text, _, _, _ ->
            if(text!!.length > 30) { binding.textinputlayoutFathermiddlename.error = "Error: Too Many Characters"
            } else if(text.length < 30){
                binding.textinputlayoutFathermiddlename.error = null }
        }

        // Fathers Last Name:
        binding.textinputeditFatherlastname.doOnTextChanged { text, _, _, _ ->
            if(text!!.length > 30) { binding.textinputlayoutFatherlastname.error = "Error: Too Many Characters"
            } else if(text.length < 30){
                binding.textinputlayoutFatherlastname.error = null }
        }

        // Fathers Village Name:
        binding.textinputeditFathersvillage.doOnTextChanged { text, _, _, _ ->
            if(text!!.length > 50) { binding.textinputlayoutFathervillage.error = "Error: Too Many Characters"
            } else if(text.length < 50){
                binding.textinputlayoutFathervillage.error = null }
        }

        // FCHW First Name
        binding.textinputeditFchwfirstname.doOnTextChanged { text, _, _, _ ->
            if(text!!.length > 30) { binding.textinputlayoutFchwfirstname.error = "Error: Too Many Characters"
            } else if(text.length < 30){
                binding.textinputlayoutFchwfirstname.error = null }
        }

        // FCHW Last Name
        binding.textinputeditFchwlastname.doOnTextChanged { text, _, _, _ ->
            if(text!!.length > 30) { binding.textinputlayoutFchwlastname.error = "Error: Too Many Characters"
            } else if(text.length < 30){
                binding.textinputlayoutFchwlastname.error = null }
        }

        // Mother Birth Defect
        binding.inputDropdownMotherbirthdefectYes.doOnTextChanged { text, _, _, _ ->
            if(text!!.length > 100) { binding.textinputlayoutMotherbirthdefectYes.error = "Error: Too Many Characters"
            } else if(text.length < 100){
                binding.textinputlayoutMotherbirthdefectYes.error = null }
        }

        // Number of Living Children
        binding.inputFirstpregNumlivchil.doOnTextChanged { text, _, _, _ ->
            if(text!!.length > 2) { binding.textinputlayoutFirstpregNumlivchild.error = "Error: Too Many Characters"
            } else if(text.length < 2){
                binding.textinputlayoutFirstpregNumlivchild.error = null }
        }

        // Number of Previous Pregnancies
        binding.inputFirstpregNumprevpreg.doOnTextChanged { text, _, _, _ ->
            val pastPregnancies = text?.toString()?.toIntOrNull() ?: 0 // Convert input to an integer or use 0 as a default value

            if (text!!.length > 2) {
                binding.textinputlayoutFirstpregNumprevpreg.error = "Error: Too Many Characters"
            } else {
                binding.textinputlayoutFirstpregNumprevpreg.error = null // Clear any previous error
            }

            // Number of Low Birth Weights
            binding.inputFirstpregLowweight.doOnTextChanged { text1, _, _, _ ->
                if (text1!!.isNotBlank()) {
                    val inputValue = text1.toString().toInt()
                    if (inputValue > pastPregnancies) {
                        binding.textinputlayoutFirstpregLowweight.error = "Error: Value cannot be greater than past pregnancies"
                    } else if (text1.length > 2) {
                        binding.textinputlayoutFirstpregLowweight.error = "Error: Too Many Characters"
                    } else {
                        binding.textinputlayoutFirstpregLowweight.error = null
                    }
                } else {
                    binding.textinputlayoutFirstpregLowweight.error = null
                }
            }
            // Number of Still Borns
            binding.inputFirstpregStillborns.doOnTextChanged { text2, _, _, _ ->
                if (text2!!.isNotBlank()) {
                    val inputValue = text2.toString().toInt()
                    if (inputValue > pastPregnancies) {
                        binding.textinputlayoutFirstpregStillborns.error = "Error: Value cannot be greater than past pregnancies"
                    } else if (text2.length > 2) {
                        binding.textinputlayoutFirstpregStillborns.error = "Error: Too Many Characters"
                    } else {
                        binding.textinputlayoutFirstpregStillborns.error = null
                    }
                } else {
                    binding.textinputlayoutFirstpregLowweight.error = null
                }
            }
            // Number of Low Birth Weights
            binding.inputFirstpregLowweight.doOnTextChanged { text3, _, _, _ ->
                if (text3!!.isNotBlank()) {
                    val inputValue = text3.toString().toInt()
                    if (inputValue > pastPregnancies) {
                        binding.textinputlayoutFirstpregLowweight.error = "Error: Value cannot be greater than past pregnancies"
                    } else if (text3.length > 2) {
                        binding.textinputlayoutFirstpregLowweight.error = "Error: Too Many Characters"
                    } else {
                        binding.textinputlayoutFirstpregLowweight.error = null
                    }
                } else {
                    binding.textinputlayoutFirstpregLowweight.error = null
                }
            }
            // Number of Miscarriages
            binding.inputFirstpregMiscarriages.doOnTextChanged { text4, _, _, _ ->
                if (text4!!.isNotBlank()) {
                    val inputValue = text4.toString().toInt()
                    if (inputValue > pastPregnancies) {
                        binding.textinputlayoutFirstpregMiscarriages.error = "Error: Value cannot be greater than past pregnancies"
                    } else if (text4.length > 2) {
                        binding.textinputlayoutFirstpregMiscarriages.error = "Error: Too Many Characters"
                    } else {
                        binding.textinputlayoutFirstpregMiscarriages.error = null
                    }
                } else {
                    binding.textinputlayoutFirstpregMiscarriages.error = null
                }
            }
            // Number of C-Sections
            binding.inputFirstpregCsections.doOnTextChanged { text5, _, _, _ ->
                if (text5!!.isNotBlank()) {
                    val inputValue = text5.toString().toInt()
                    if (inputValue > pastPregnancies) {
                        binding.textinputlayoutFirstpregCsections.error = "Error: Value cannot be greater than past pregnancies"
                    } else if (text5.length > 2) {
                        binding.textinputlayoutFirstpregCsections.error = "Error: Too Many Characters"
                    } else {
                        binding.textinputlayoutFirstpregCsections.error = null
                    }
                } else {
                    binding.textinputlayoutFirstpregCsections.error = null
                }
            }
            // Number of Postpartum Hemorrhages
            binding.inputFirstpregPostpartumhemorrages.doOnTextChanged { text6, _, _, _ ->
                if (text6!!.isNotBlank()) {
                    val inputValue = text6.toString().toInt()
                    if (inputValue > pastPregnancies) {
                        binding.textinputlayoutFirstpregPostpartumhemorrhages.error = "Error: Value cannot be greater than past pregnancies"
                    } else if (text6.length > 2) {
                        binding.textinputlayoutFirstpregPostpartumhemorrhages.error = "Error: Too Many Characters"
                    } else {
                        binding.textinputlayoutFirstpregPostpartumhemorrhages.error = null
                    }
                } else {
                    binding.textinputlayoutFirstpregPostpartumhemorrhages.error = null
                }
            }
            // Number of Pregnancy Infections
            binding.inputFirstpregPreginfections.doOnTextChanged { text7, _, _, _ ->
                if (text7!!.isNotBlank()) {
                    val inputValue = text7.toString().toInt()
                    if (inputValue > pastPregnancies) {
                        binding.textinputlayoutFirstpregPreginfections.error = "Error: Value cannot be greater than past pregnancies"
                    } else if (text7.length > 2) {
                        binding.textinputlayoutFirstpregPreginfections.error = "Error: Too Many Characters"
                    } else {
                        binding.textinputlayoutFirstpregPreginfections.error = null
                    }
                } else {
                    binding.textinputlayoutFirstpregPreginfections.error = null
                }
            }
            // High BP Blood Pressure Pregnancies
            binding.inputFirstpregHighbppregnacies.doOnTextChanged { text8, _, _, _ ->
                if (text8!!.isNotBlank()) {
                    val inputValue = text8.toString().toInt()
                    if (inputValue > pastPregnancies) {
                        binding.textinputlayoutFirstpregHighbppregnancies.error = "Error: Value cannot be greater than past pregnancies"
                    } else if (text8.length > 2) {
                        binding.textinputlayoutFirstpregHighbppregnancies.error = "Error: Too Many Characters"
                    } else {
                        binding.textinputlayoutFirstpregHighbppregnancies.error = null
                    }
                } else {
                    binding.textinputlayoutFirstpregHighbppregnancies.error = null
                }
            }
        }

        // Other Personal Medical History
        binding.textinputeditPersonalmedicalother.doOnTextChanged { text, _, _, _ ->
            if(text!!.length > 200) { binding.textinputlayoutPersonalmedicalother.error = "Error: Too Many Characters"
            } else if(text.length < 200){
                binding.textinputlayoutPersonalmedicalother.error = null }
        }

        // Type of Drugs Errors
        binding.inputDropdownDrugsYes.doOnTextChanged { text, _, _, _ ->
            if(text!!.length > 100) { binding.textinputlayoutDrugsYes.error = "Error: Too Many Characters"
            } else if(text.length < 100){
                binding.textinputlayoutDrugsYes.error = null }
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

        binding.inputDropdownLastmenstcycleYes
        binding.inputDropdownLastmenstcycleYes.isFocusable = false
        binding.textinputlayoutLastmenstcycle.isClickable = true

        // Calendar View Listening For Selecting Date Input
        binding.textinputeditDob.setOnClickListener {
            isDOBFieldClicked = true
            showDatePickerDialog()
        }
        binding.inputDropdownLastmenstcycleYes.setOnClickListener {
            isDOBFieldClicked = false
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

                if (isDOBFieldClicked) {
                    binding.textinputeditDob.setText(selectedDate)
                } else {
                    binding.inputDropdownLastmenstcycleYes.setText(selectedDate)
                }
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
    // Phone Validation Function
   private fun isPhoneNumberValid(phoneNumber: String, defaultRegion: String): Boolean {
        val phoneNumberUtil = PhoneNumberUtil.getInstance()
        return try {
            val parsedPhoneNumber: PhoneNumber = phoneNumberUtil.parse(phoneNumber, defaultRegion)
            phoneNumberUtil.isValidNumber(parsedPhoneNumber)
        } catch (e: NumberParseException) {
            false
        }
    }
    }

