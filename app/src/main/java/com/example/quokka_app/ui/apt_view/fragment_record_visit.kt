package com.example.quokka_app.ui.apt_view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.quokka_app.R
import com.example.quokka_app.databinding.FragmentRecordVisitBinding


class fragment_record_visit : AppCompatActivity() {

    private lateinit var binding: FragmentRecordVisitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentRecordVisitBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val postVisit = post_visit()
        val preVisit = pre_visit()
        val postVisitKid = post_visit_kid()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, preVisit)
            commit()
        }

        binding.btnPostB.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, postVisit)
                addToBackStack(null)
                commit()
            }
        }

        binding.btnPreB.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, preVisit)
                addToBackStack(null)
                commit()
            }
        }

        binding.btnPostBK.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, postVisitKid)
                addToBackStack(null)
                commit()
            }
        }









        }


    }


