package com.example.clear_and_glow.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.clear_and_glow.interfaces.FirestoreCallback
import com.example.clear_and_glow.interfaces.SharedRoutinesCallback
import com.example.clear_and_glow.interfaces.UserCallback
import com.example.clear_and_glow.models.Routine
import com.example.clear_and_glow.models.SharedRoutine
import com.example.clear_and_glow.models.User
import com.example.clear_and_glow.utilities.AuthManager
import com.example.clear_and_glow.utilities.Constants
import com.example.clear_and_glow.utilities.FirestoreManager
import com.google.firebase.auth.oAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class FeedViewModel : ViewModel() {

    private val firestoreManager = FirestoreManager.getInstance()
    private val _sharedRoutines = MutableLiveData<List<SharedRoutine>>()
    val sharedRoutines: LiveData<List<SharedRoutine>> get() = _sharedRoutines




    fun loadSharedRoutines() {
        firestoreManager.listenForSharedRoutineUpdates(object : SharedRoutinesCallback {
            override fun onSuccess(sharedRoutines: List<SharedRoutine>) {
                _sharedRoutines.value = sharedRoutines
            }

            override fun onFailure(errorMessage: String) {
                _sharedRoutines.value = emptyList()
            }
        })
    }


    fun toggleLike(userId: String, sharedRoutine: SharedRoutine, position: Int) {
        val updatedRoutine = sharedRoutine.toggleLike(userId)
        _sharedRoutines.value = _sharedRoutines.value?.toMutableList()?.apply {
            this[position] = updatedRoutine
        }
        firestoreManager.updateSharedRoutineLikes(updatedRoutine, object : FirestoreCallback {
            override fun onSuccess() {
                Log.d("FeedViewModel", "Like updated successfully!")
            }

            override fun onFailure(errorMessage: String) {
                Log.e("FeedViewModel", "Error updating like: $errorMessage")
                _sharedRoutines.value = _sharedRoutines.value?.toMutableList()?.apply {
                    this[position] = sharedRoutine
                }
            }
        })
    }

    fun shareRoutine(
        userId: String,
        timeOfDay: String,
        routine: Routine,
        callback: FirestoreCallback
    ) {
        firestoreManager.getUser(userId, object : UserCallback {
            override fun onSuccess(user: User) {

                val sharedRoutineId = FirebaseFirestore.getInstance()
                    .collection(Constants.DB.SHARED_ROUTINE_REF)
                    .document().id

                val sharedRoutine = SharedRoutine(
                    id = firestoreManager.generateDocumentId(Constants.DB.SHARED_ROUTINE_REF),
                    userId = userId,
                    userName = user.getFullName(),
                    profileImageRes = user.profilePicUrl, // Ensure the User model has this field
                    title = "$timeOfDay Routine",
                    routine = routine
                )
                firestoreManager.saveShareRoutine(sharedRoutine, callback)
            }

            override fun onFailure(errorMessage: String) {
                callback.onFailure(errorMessage)
            }
        })
    }
}


