package com.example.quokka_app.ui.home


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.quokka_app.R
import com.example.quokka_app.databinding.FragmentHomeBinding
import com.example.quokka_app.ui.apt_view.fragment_record_visit


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnRecordVisit.setOnClickListener {
            val intent = Intent(requireActivity(), fragment_record_visit::class.java)
            // Start the record_visit activity
            startActivity(intent)
        }

        return root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
