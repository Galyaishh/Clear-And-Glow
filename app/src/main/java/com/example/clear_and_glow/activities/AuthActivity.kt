package com.example.clear_and_glow.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.clear_and_glow.databinding.ActivityAuthBinding
import com.example.clear_and_glow.utilities.AuthManager
import com.google.firebase.auth.FirebaseUser

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authManager = AuthManager.getInstance(this)

        // If user is already logged in, go to MainActivity
        if (authManager.isUserLoggedIn()) {
            navigateToMain()
            return
        }

        initViews()
    }

    private fun initViews() {
        binding.authBTNSignup.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.authBTNGoogle.setOnClickListener {
            googleSignIn()
        }

        binding.authBTNSignin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun googleSignIn() {
        val signInIntent = authManager.getGoogleSignInIntent()
        googleSignInLauncher.launch(signInIntent)
    }

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            authManager.handleGoogleSignInResult(result.data, object : AuthManager.AuthCallback {
                override fun onSuccess(currentUser: FirebaseUser?) {
                    showToast("Google Sign-in successful")
                    navigateToMain()
                }

                override fun onFailure(errorMessage: String) {
                    showToast("Google Sign-in failed: $errorMessage")
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
