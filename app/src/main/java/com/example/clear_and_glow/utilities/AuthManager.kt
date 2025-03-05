package com.example.clear_and_glow.utilities

import android.content.Context
import android.content.Intent
import com.example.clear_and_glow.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class AuthManager private constructor(context: Context) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val googleSignInClient: GoogleSignInClient

    companion object {
        @Volatile
        private var instance: AuthManager? = null

        fun getInstance(context: Context): AuthManager {
            return instance ?: synchronized(this) {
                instance ?: AuthManager(context.applicationContext).also { instance = it }
            }
        }
    }

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id)) // Ensure it's correct
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context.applicationContext, gso)
    }

    fun isUserLoggedIn(): Boolean = firebaseAuth.currentUser != null

    fun getCurrentUserUid(): String? = firebaseAuth.currentUser?.uid

    fun loginUser(email: String, password: String, callback: AuthCallback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback.onSuccess(firebaseAuth.currentUser)
                } else {
                    callback.onFailure(task.exception?.message ?: "Login failed")
                }
            }
    }

    // --- GOOGLE SIGN-IN ---
    fun getGoogleSignInIntent(): Intent = googleSignInClient.signInIntent

    fun handleGoogleSignInResult(data: Intent?, callback: AuthCallback) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { idToken ->
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            callback.onSuccess(firebaseAuth.currentUser)
                        } else {
                            callback.onFailure(authTask.exception?.message ?: "Google sign-in failed")
                        }
                    }
            } ?: callback.onFailure("Google Sign-In failed: ID token is null")
        } catch (e: Exception) {
            callback.onFailure("Google Sign-In error: ${e.message}")
        }
    }

    fun createUser(email: String, password: String, callback: AuthCallback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback.onSuccess(firebaseAuth.currentUser)
                } else {
                    callback.onFailure(task.exception?.message ?: "Registration failed")
                }
            }
    }

    fun signOut() {
        firebaseAuth.signOut()
        googleSignInClient.signOut()
    }

    interface AuthCallback {
        fun onSuccess(currentUser: FirebaseUser?)
        fun onFailure(errorMessage: String)
    }
}
