package com.example.quokka_app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.quokka_app.R
import com.example.quokka_app.databinding.FragmentHomeBinding
import com.example.quokka_app.ui.patientprofilehome.ChildPersonalInfoFragment
import com.example.quokka_app.ui.patientprofilehome.PatientProfilesHomeFragment
import androidx.navigation.fragment.findNavController

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val patientprofilesFragment = PatientProfilesHomeFragment()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val patientprofilesButton = binding.buttonTop
        val newpatientprofilesButton = binding.buttonBottom

        patientprofilesButton.setOnClickListener {
            // Use the NavController to navigate to the desired destination
            findNavController().navigate(R.id.action_HomeFragment_to_patientProfilesHomeFragment)
        }

        newpatientprofilesButton.setOnClickListener {
            // Use the NavController to navigate to the desired destination
            findNavController().navigate(R.id.action_HomeFragment_to_newpatientprofiles)
        }






        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}