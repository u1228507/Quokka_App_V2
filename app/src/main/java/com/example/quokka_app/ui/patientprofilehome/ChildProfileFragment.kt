package com.example.quokka_app.ui.patientprofilehome


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quokka_app.R
import com.example.quokka_app.databinding.FragmentChildProfileBinding
import com.example.quokka_app.databinding.FragmentMotherProfileBinding


class ChildProfileFragment : Fragment(R.layout.fragment_child_profile) {

    private lateinit var binding: FragmentChildProfileBinding
    private val motherPersonalInfoFragment = ChildPersonalInfoFragment()
    private val pastVisitsFragment: PastVisitsFragment = PastVisitsFragment()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChildProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        val patientId = arguments?.getString("patientId") ?: ""

        val personalInformationButton = binding.patientprofileshomeButtonPersonalinformationChild
        val pastVisitsButton = binding.patientprofileshomeButtonPastvisitsChild

        personalInformationButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.patientprofilehome_fragmentContainer, motherPersonalInfoFragment)
                .addToBackStack(null)
                .commit()
        }

        pastVisitsButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.patientprofilehome_fragmentContainer, pastVisitsFragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}