package com.example.clear_and_glow.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clear_and_glow.R
import com.example.clear_and_glow.adapters.SharedRoutineAdapter
import com.example.clear_and_glow.databinding.FragmentFeedBinding
import com.example.clear_and_glow.interfaces.FirestoreCallback
import com.example.clear_and_glow.models.SharedRoutine
import com.example.clear_and_glow.utilities.AuthManager
import com.example.clear_and_glow.utilities.FirestoreManager
import com.example.clear_and_glow.utilities.ImageLoader
import com.example.clear_and_glow.utilities.StorageManager
import com.example.clear_and_glow.viewModels.FeedViewModel
import com.google.firebase.auth.FirebaseAuth

class FeedFragment : Fragment() {

    private val feedViewModel: FeedViewModel by viewModels()

    private lateinit var binding: FragmentFeedBinding
    private lateinit var sharedRoutineAdapter: SharedRoutineAdapter
    private lateinit var authManager: AuthManager
    private lateinit var storageManager: StorageManager
    private lateinit var firestoreManager: FirestoreManager
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { uploadProfilePicture(it) }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authManager = AuthManager.getInstance(requireContext())
        storageManager = StorageManager()

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Log.e("FirebaseAuth", "User is null! Not authenticated.")
        } else {
            Log.d("FirebaseAuth", "User is authenticated: ${user.uid}")
        }


        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        feedViewModel.loadSharedRoutines()

    }

    private fun setupRecyclerView() {
        val userId = authManager.getCurrentUserUid() ?: return
        sharedRoutineAdapter =
            SharedRoutineAdapter(mutableListOf(), userId) { sharedRoutine, position ->
                likeSharedRoutine(sharedRoutine, position)
            }

        binding.feedRVRoutines.apply {
            adapter = sharedRoutineAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        updateUi()
        Log.e("FeedFragment", "User ID is null, cannot initialize adapter")
    }


    private fun setupObservers() {
        feedViewModel.sharedRoutines.observe(viewLifecycleOwner) { sharedRoutines ->
            sharedRoutineAdapter.setSharedRoutines(sharedRoutines)
        }
    }


    private fun setupClickListeners() {
        binding.feedBTNCamera.setOnClickListener {
            pickImage.launch("image/*")
        }
    }


//    private fun uploadProfilePicture(imageUri: Uri) {
//        val userId = authManager.getCurrentUserUid() ?: return
//
//        storageManager.uploadProfilePicture(userId, imageUri) { imageUrl ->
//            if (imageUrl != null) {
//                updateUserProfilePicture(imageUrl)
//            } else {
//                Toast.makeText(requireContext(), "Failed to upload image.", Toast.LENGTH_SHORT).show()
//            }
//        }
//        ImageLoader.getInstance().loadImage(userId.profilePicUrl, binding.feedIMGProfile)
//
//    }

    private fun uploadProfilePicture(imageUri: Uri) {
        val userId = authManager.getCurrentUserUid() ?: return
        storageManager.uploadProfilePicture(userId, imageUri) { imageUrl ->
            if (imageUrl != null) {
                firestoreManager.saveUserProfilePic(userId, imageUrl, object : FirestoreCallback {
                    override fun onSuccess() {
                        ImageLoader.getInstance().loadImage(imageUrl, binding.feedIMGProfile)
                        Toast.makeText(requireContext(), "Profile picture updated!", Toast.LENGTH_SHORT).show()
                    }
                    override fun onFailure(errorMessage: String) {
                        Toast.makeText(requireContext(), "Failed to save profile picture.", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(requireContext(), "Failed to upload image.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun updateUserProfilePicture(imageUrl: String) {
        val userId = authManager.getCurrentUserUid() ?: return

        firestoreManager.saveUserProfilePic(userId, imageUrl, object : FirestoreCallback {
            override fun onSuccess() {
                // ✅ Update UI
                ImageLoader.getInstance().loadImage(imageUrl, binding.feedIMGProfile)
                Toast.makeText(requireContext(), "Profile picture updated!", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(errorMessage: String) {
                Toast.makeText(requireContext(), "Failed to save profile picture.", Toast.LENGTH_SHORT).show()
            }
        })
    }


//    fun updateUserProfilePicture(imageUrl: String) {
//        val userId = authManager.getCurrentUserUid() ?: return
//
//        firestoreManager.getUserProfile(userId) { user ->
//            val updatedUser =
//                user?.copy(profilePicUrl = imageUrl) ?: User(id = userId, profilePicUrl = imageUrl)
//            firestoreService.saveUserProfile(updatedUser) { success ->
//                if (success) {
//                    loadUserProfile()
//                    Toast.makeText(requireContext(), "Profile picture updated!", Toast.LENGTH_SHORT)
//                        .show()
//                } else {
//                    Toast.makeText(requireContext(), "Failed to save profile picture.", Toast.LENGTH_SHORT)
//                        .show()
//                }
//            }
//        }
//    }

    private fun updateUi() {
        val userName = authManager.getUserName()
        binding.feedTXTHello.text = getString(R.string.welcome_user, userName)

    }

    private fun likeSharedRoutine(sharedRoutine: SharedRoutine, position: Int) {
        val userId = authManager.getCurrentUserUid() ?: return
        sharedRoutineAdapter.updateLikeStatus(position, sharedRoutine)
        feedViewModel.toggleLike(userId, sharedRoutine, position)
    }
}
