package com.example.clear_and_glow.utilities

import android.content.Context
import android.content.Intent
import com.example.clear_and_glow.R
import com.example.clear_and_glow.interfaces.FirestoreCallback
import com.example.clear_and_glow.interfaces.UserCallback
import com.example.clear_and_glow.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.example.clear_and_glow.utilities.capitalizeFirstLetter

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
                            val user = firebaseAuth.currentUser
                            if (user != null) {
                                val firestoreManager = FirestoreManager.getInstance()
                                firestoreManager.getUser(
                                    user.uid,
                                    object : UserCallback {
                                        override fun onSuccess(user: User) {
                                            // User exists in Firestore
                                            callback.onSuccess(firebaseAuth.currentUser)
                                        }

                                        override fun onFailure(errorMessage: String) {
                                            //User does not exist in database
                                            val newUser = User.Builder()
                                                .firstName(
                                                    user.displayName?.substringBefore(" ")?.capitalizeFirstLetter() ?: ""
                                                )
                                                .lastName(
                                                    user.displayName?.substringAfter(" ")?.capitalizeFirstLetter() ?: ""
                                                )
                                                .email(user.email ?: "")
                                                .uid(user.uid)
                                                .build()

                                            firestoreManager.saveUser(
                                                user.uid,
                                                newUser,
                                                object : FirestoreCallback {
                                                    override fun onSuccess() {
                                                        callback.onSuccess(user)
                                                    }

                                                    override fun onFailure(errorMessage: String) {
                                                        callback.onFailure("Failed to save user: $errorMessage")
                                                    }
                                                })
                                        }
                                    })
                            } else {
                                callback.onFailure("Google Sign-In failed: User is null")
                            }
                        } else {
                            callback.onFailure(
                                authTask.exception?.message ?: "Google sign-in failed"
                            )
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

    fun getUser(): FirebaseUser? {
        return firebaseAuth.currentUser
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
