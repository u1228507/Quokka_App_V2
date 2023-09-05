package com.example.quokka_app.ui.userprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserProfileViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is the User Profile Fragment"
    }
    val text: LiveData<String> = _text
}