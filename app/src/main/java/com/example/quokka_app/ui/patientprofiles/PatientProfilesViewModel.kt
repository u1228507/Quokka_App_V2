package com.example.quokka_app.ui.patientprofiles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PatientProfilesViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is the Patient Profiles Fragment"
    }
    val text: LiveData<String> = _text
}