package com.example.clear_and_glow.interfaces


interface FirestoreCallback {
    fun onSuccess()
    fun onFailure(errorMessage: String)
}