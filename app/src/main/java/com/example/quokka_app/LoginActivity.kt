package com.example.quokka_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.example.quokka_app.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressBar = binding.loginProgressBar
        auth = FirebaseAuth.getInstance()

        binding.buttonLogin.setOnClickListener {
            val email = binding.textinputEmail.text.toString()
            val password = binding.textinputPassword.text.toString()
            progressBar.visibility = View.VISIBLE
            if (email.isEmpty()) {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show()
            } else if (password.isEmpty()) {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this, "Login Successful",Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}




