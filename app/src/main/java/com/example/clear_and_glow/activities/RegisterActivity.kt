package com.example.clear_and_glow.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clear_and_glow.databinding.ActivityRegisterBinding
import com.example.clear_and_glow.interfaces.FirestoreCallback
import com.example.clear_and_glow.models.User
import com.example.clear_and_glow.utilities.AuthManager
import com.example.clear_and_glow.utilities.FirestoreManager
import com.example.clear_and_glow.utilities.capitalizeFirstLetter
import com.google.firebase.auth.FirebaseUser
import java.util.Calendar

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var authManager: AuthManager
    private lateinit var firestoreManager: FirestoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authManager = AuthManager.getInstance(this)
        firestoreManager = FirestoreManager.getInstance()

        initViews()
    }

    private fun initViews() {
        binding.registerBTNSubmit.setOnClickListener { registerUser() }
        binding.registerBTNBack.setOnClickListener { finish() }
    }

    private fun registerUser() {
        val firstName = binding.registerETFirstName.editText?.text.toString().trim().capitalizeFirstLetter()
        val lastName = binding.registerETLastName.editText?.text.toString().trim().capitalizeFirstLetter()
        val email = binding.registerETEmail.editText?.text.toString().trim()
        val password = binding.registerETPassword.editText?.text.toString().trim()

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        authManager.createUser(email, password, object : AuthManager.AuthCallback {
            override fun onSuccess(user: FirebaseUser?) {
                user?.uid?.let { userId ->
                    val newUser = User.Builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .email(email)
                        .uid(userId)
                        .build()

                    firestoreManager.saveUser(userId, newUser, object :
                        FirestoreCallback {
                        override fun onSuccess() {
                            Toast.makeText(this@RegisterActivity, "Registration successful!", Toast.LENGTH_SHORT).show()
                            navigateToMain()
                        }

                        override fun onFailure(errorMessage: String) {
                            Toast.makeText(this@RegisterActivity, "Failed to save user data", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }

            override fun onFailure(errorMessage: String) {
                Toast.makeText(this@RegisterActivity, "Registration failed: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
