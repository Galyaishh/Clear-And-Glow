package com.example.clear_and_glow.interfaces

import com.example.clear_and_glow.models.Routine

interface RoutinesCallback {
    fun onSuccess(routines: List<Routine>)
    fun onFailure(errorMessage: String)
}