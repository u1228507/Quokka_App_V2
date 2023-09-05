package com.example.quokka_app.ui.newpatientprofile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.example.quokka_app.R
import com.example.quokka_app.databinding.ActivityMainBinding
import com.example.quokka_app.databinding.FragmentNewpatientprofilesBinding

class NewPatientProfileFragment : Fragment(R.layout.fragment_newpatientprofiles) {
    private var _binding: FragmentNewpatientprofilesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewpatientprofilesBinding.inflate(inflater, container, false)
        val root: View = binding.root

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
    }

}
