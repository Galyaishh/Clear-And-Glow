package com.example.clear_and_glow.utilities

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class StorageManager(
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {

    private val storageRef: StorageReference = storage.reference

    fun uploadProfilePicture(userId: String, imageUri: Uri, callback: (String?) -> Unit) {
        val profilePicRef = storageRef.child(Constants.Pathes.getProfilePicPath(userId))

        profilePicRef.putFile(imageUri)
            .addOnSuccessListener {
                profilePicRef.downloadUrl.addOnSuccessListener { uri ->
                    callback(uri.toString())
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun uploadProductImage(productId: String, imageUri: Uri, callback: (String?) -> Unit) {
        val productImageRef = storageRef.child(Constants.Pathes.getProductPicPath(productId))

        productImageRef.putFile(imageUri)
            .addOnSuccessListener {
                productImageRef.downloadUrl.addOnSuccessListener { uri ->
                    callback(uri.toString())
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun getProfilePictureUrl(userId: String, callback: (String?) -> Unit) {
        val profilePicRef = storageRef.child(Constants.Pathes.getProfilePicPath(userId))

        profilePicRef.downloadUrl
            .addOnSuccessListener { uri -> callback(uri.toString()) }
            .addOnFailureListener { callback(null) }
    }

    fun getProductImageUrl(productId: String, callback: (String?) -> Unit) {
        val productImageRef = storageRef.child(Constants.Pathes.getProductPicPath(productId))

        productImageRef.downloadUrl
            .addOnSuccessListener { uri -> callback(uri.toString()) }
            .addOnFailureListener { callback(null) }
    }
}
