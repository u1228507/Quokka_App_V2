package com.example.quokka_app.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.quokka_app.R
import com.example.quokka_app.databinding.FragmentPatientProfilesHomeBinding
import com.example.quokka_app.databinding.FragmentSettingsBinding


class settingsFragment : Fragment() {
    private var binding: FragmentSettingsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        return binding?.root
    }

}