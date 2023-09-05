package com.example.quokka_app.ui.userprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.quokka_app.databinding.FragmentUserprofileBinding

class UserProfileFragment : Fragment() {
    private var _binding: FragmentUserprofileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val UserProfileViewModel = ViewModelProvider(this).get(UserProfileViewModel::class.java)
        _binding = FragmentUserprofileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textUserprofile
        UserProfileViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}