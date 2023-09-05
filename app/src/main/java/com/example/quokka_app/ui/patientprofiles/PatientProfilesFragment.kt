package com.example.quokka_app.ui.patientprofiles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.quokka_app.R
import com.example.quokka_app.databinding.FragmentPatientprofilesBinding

class PatientProfilesFragment: Fragment() {
    private var _binding: FragmentPatientprofilesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val PatientProfilesViewModel = ViewModelProvider(this).get(PatientProfilesViewModel::class.java)
        _binding = FragmentPatientprofilesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textPatientprofiles
        PatientProfilesViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}