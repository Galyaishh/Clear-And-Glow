package com.example.clear_and_glow.utilities

import com.example.clear_and_glow.models.User
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreManager private constructor() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    companion object {
        @Volatile
        private var instance: FirestoreManager? = null

        fun getInstance(): FirestoreManager {
            return instance ?: synchronized(this) {
                instance ?: FirestoreManager().also { instance = it }
            }
        }
    }

    fun saveUser(userId: String, user: User, callback: FirestoreCallback) {
        db.collection("users").document(userId).set(user)
            .addOnSuccessListener { callback.onSuccess() }
            .addOnFailureListener { e -> callback.onFailure(e.message ?: "Failed to save user") }
    }

    fun getUser(userId: String, callback: UserCallback) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                if (user != null) {
                    callback.onSuccess(user)
                } else {
                    callback.onFailure("User not found")
                }
            }
            .addOnFailureListener { e -> callback.onFailure(e.message ?: "Error fetching user") }
    }

    interface FirestoreCallback {
        fun onSuccess()
        fun onFailure(errorMessage: String)
    }

    interface UserCallback {
        fun onSuccess(user: User)
        fun onFailure(errorMessage: String)
    }
}
