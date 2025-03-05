package com.example.clear_and_glow.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clear_and_glow.databinding.ActivityLoginBinding
import com.example.clear_and_glow.utilities.AuthManager
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        authManager = AuthManager.getInstance(this)
        initViews()
    }

    private fun initViews() {
        binding.loginBTNSubmit.setOnClickListener { loginUser() }
        binding.loginBTNBack.setOnClickListener { finish() }
    }

    private fun loginUser() {
        val email = binding.loginETEmail.editText?.text?.toString()?.trim()
        val password = binding.loginETPassword.editText?.text?.toString()?.trim()

        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            showToast("Please enter both email and password")
            return
        }

        authManager.loginUser(email, password, object : AuthManager.AuthCallback {
            override fun onSuccess(currentUser: FirebaseUser?) {
                showToast("Login successful")
                navigateToMain()
            }

            override fun onFailure(errorMessage: String) {
                showToast("Login failed: $errorMessage")
            }
        })
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
