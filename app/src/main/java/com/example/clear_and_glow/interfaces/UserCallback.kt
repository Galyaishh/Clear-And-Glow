package com.example.clear_and_glow.interfaces

import com.example.clear_and_glow.models.User

interface UserCallback {
    fun onSuccess(user: User)
    fun onFailure(errorMessage: String)
}