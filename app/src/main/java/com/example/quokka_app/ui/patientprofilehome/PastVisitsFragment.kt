package com.example.quokka_app.ui.patientprofilehome

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.quokka_app.R
import com.example.quokka_app.databinding.FragmentPastVisitsBinding

class PastVisitsFragment : Fragment() {
    private lateinit var binding: FragmentPastVisitsBinding
    private val pastVisitPostpartumFragment: PastVisitPostpartumFragment = PastVisitPostpartumFragment()
    private val pastVisitPrenatalFragment: PastVisitPrenatalFragment = PastVisitPrenatalFragment()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPastVisitsBinding.inflate(inflater, container, false)
        val patientId = arguments?.getString("patientId") ?: ""
        Log.d("PatientID", "Patient ID: $patientId")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val patientId = arguments?.getString("patientId") ?: ""
        Log.d("PatientID", "Patient ID: $patientId")

        val args = Bundle()
        args.putString("patientId", patientId)
        pastVisitPostpartumFragment.arguments = args
        pastVisitPrenatalFragment.arguments = args

        binding.pastvisitsButtonPrenatalvisits.setOnClickListener {
            navController.navigate(R.id.nav_pastprenatalvisits, args)
        }

        binding.pastvisitsButtonPostpartumvisits.setOnClickListener {
            navController.navigate(R.id.nav_pastpostpartumvisits, args)
        }
    }







}



