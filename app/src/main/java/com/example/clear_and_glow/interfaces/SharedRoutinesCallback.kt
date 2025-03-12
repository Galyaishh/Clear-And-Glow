package com.example.clear_and_glow.interfaces

import com.example.clear_and_glow.models.SharedRoutine

interface SharedRoutinesCallback {
    fun onSuccess(sharedRoutines: List<SharedRoutine>)
    fun onFailure(errorMessage: String)
}