package com.example.clear_and_glow.utilities

import android.util.Log
import com.example.clear_and_glow.R
import com.example.clear_and_glow.interfaces.FirestoreCallback
import com.example.clear_and_glow.interfaces.ProductCategoryListCallback
import com.example.clear_and_glow.interfaces.ProductListCallback
import com.example.clear_and_glow.interfaces.RoutinesCallback
import com.example.clear_and_glow.interfaces.SharedRoutinesCallback
import com.example.clear_and_glow.interfaces.UserCallback
import com.example.clear_and_glow.models.Product
import com.example.clear_and_glow.models.ProductCategory
import com.example.clear_and_glow.models.Routine
import com.example.clear_and_glow.models.SharedRoutine
import com.example.clear_and_glow.models.User
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
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
        db.collection(Constants.DB.USER_REF).document(userId).set(user)
            .addOnSuccessListener { callback.onSuccess() }
            .addOnFailureListener { e -> callback.onFailure(e.message ?: "Failed to save user") }
    }

    fun getUser(userId: String, callback: UserCallback) {
        db.collection(Constants.DB.USER_REF).document(userId).get()
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

    fun saveUserProfilePic(userId: String, imageUrl: String, callback: FirestoreCallback) {
        db.collection(Constants.DB.USER_REF).document(userId).update("profilePicUrl", imageUrl)
            .addOnSuccessListener {
                callback.onSuccess()
            }.addOnFailureListener { e ->
                callback.onFailure(e.message ?: "Failed to save user profile picture")
            }
    }

    fun getUserProfilePic(userId: String, callback: (String?) -> Unit) {
        db.collection(Constants.DB.USER_REF).document(userId).get()
            .addOnSuccessListener { document ->
                val imageUrl = document.getString("profilePicUrl")
                callback(imageUrl)
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun updateUserProfilePic(userId: String, imageUrl: String, callback: FirestoreCallback) {
        db.collection(Constants.DB.USER_REF).document(userId).update("profilePicUrl", imageUrl)
            .addOnSuccessListener {
                callback.onSuccess()
            }.addOnFailureListener { e ->
                callback.onFailure(e.message ?: "Failed to save user profile picture")
            }
    }

    fun saveProductToGlobal(product: Product, callback: FirestoreCallback) {
        val productRef = db.collection(Constants.DB.GLOBAL_PRODUCT_REF).document()
        val productWithId = product.copy(id = productRef.id)

        productRef.set(productWithId)
            .addOnSuccessListener { callback.onSuccess() }
            .addOnFailureListener { e -> callback.onFailure(e.message ?: "Failed to save product") }
    }


    fun listenForGlobalProductsUpdates(callback: ProductListCallback) {
        db.collection(Constants.DB.GLOBAL_PRODUCT_REF)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    callback.onFailure(error.message ?: "Error listening for product updates")
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val productList = snapshots.map { document ->
                        document.toObject(Product::class.java).copy(id = document.id)
                    }
                    callback.onSuccess(productList)
                }
            }
    }


    fun getAllGlobalProducts(callback: ProductListCallback) {
        db.collection(Constants.DB.GLOBAL_PRODUCT_REF).get()
            .addOnSuccessListener { documents ->
                val productList = mutableListOf<Product>()
                val uniqueCategories = mutableSetOf<String>() // Track unique categories
                for (document in documents) {
                    val product = document.toObject(Product::class.java).copy(id = document.id)
                    productList.add(product)
                    uniqueCategories.add(product.category)
                }
                updateCategoriesInFirestore(uniqueCategories)
                callback.onSuccess(productList)
            }
            .addOnFailureListener { e ->
                callback.onFailure(e.message ?: "Error loading products")
            }
    }

    private fun updateCategoriesInFirestore(categories: Set<String>) {
        val categoryRef = db.collection(Constants.DB.CATEGORY_REF)

        categories.forEach { categoryName ->
            categoryRef.whereEqualTo("name", categoryName).get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        val newCategory = ProductCategory.getCategory(categoryName)
                        categoryRef.add(newCategory)
                            .addOnSuccessListener {
                                println("Category added: $categoryName with icon ${newCategory.iconResId}")
                            }
                            .addOnFailureListener { e ->
                                println("Error adding category: ${e.message}")
                            }
                    } else {
                        for (document in documents) {
                            val newIconResId = ProductCategory.getCategory(categoryName).iconResId
                            categoryRef.document(document.id).update("iconResId", newIconResId)
                                .addOnSuccessListener {
                                    println("Icon updated for category: $categoryName")
                                }
                                .addOnFailureListener { e ->
                                    println("Error updating icon: ${e.message}")
                                }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    println("Error checking category existence: ${e.message}")
                }
        }
    }


    fun getAllCategories(callback: ProductCategoryListCallback) {
        db.collection(Constants.DB.CATEGORY_REF).get().addOnSuccessListener { documents ->
            val categoryList = mutableListOf<ProductCategory>()
            for (document in documents) {
                val category = document.toObject(ProductCategory::class.java)
                categoryList.add(category)
            }
            callback.onSuccess(categoryList)
        }.addOnFailureListener { e -> callback.onFailure(e.message ?: "Error loading categories") }
    }

    fun updateProductImage(productId: String, imageUrl: String, callback: FirestoreCallback) {
        db.collection(Constants.DB.GLOBAL_PRODUCT_REF).document(productId)
            .update("picture", imageUrl)
            .addOnSuccessListener { callback.onSuccess() }
            .addOnFailureListener { e -> callback.onFailure(e.message ?: "Failed to update image") }
    }


    fun updateProductDates(userId: String, product: Product): Task<Void> {
        val userProductRef = db.collection(Constants.DB.USER_REF).document(userId)
            .collection(Constants.DB.PRODUCT_REF).document(product.id)

        val userRoutineRef = db.collection(Constants.DB.USER_REF).document(userId)
            .collection(Constants.DB.ROUTINE_REF).document(product.id)

        val updateProductTask = userProductRef.update(
            mapOf(
                "openingDate" to product.openingDate,
            )
        )

        val updateRoutineTask = userRoutineRef.update(
            mapOf(
                "openingDate" to product.openingDate,
            )
        )

        return Tasks.whenAll(updateProductTask, updateRoutineTask)
    }


    fun addProductToUser(userId: String, product: Product, callback: FirestoreCallback) {
        val userProductRef = db.collection(Constants.DB.USER_REF).document(userId)
            .collection(Constants.DB.PRODUCT_REF).document(product.id)

        userProductRef.set(product)
            .addOnSuccessListener { callback.onSuccess() }
            .addOnFailureListener { e ->
                callback.onFailure(
                    e.message ?: "Failed to add product"
                )
            }
    }


    fun addProductsToUser(userId: String, products: List<Product>, callback: FirestoreCallback) {
        val batch = db.batch()
        val userProductsRef = db.collection(Constants.DB.USER_REF).document(userId)
            .collection(Constants.DB.PRODUCT_REF)

        products.forEach { product ->
            val newProductRef = userProductsRef.document(product.id)
            batch.set(newProductRef, product)
        }
        batch.commit()
            .addOnSuccessListener { callback.onSuccess() }
            .addOnFailureListener { e ->
                callback.onFailure(
                    e.message ?: "Failed to add products"
                )
            }
    }

    fun getUserProducts(userId: String, callback: ProductListCallback) {
        db.collection(Constants.DB.USER_REF).document(userId).collection(Constants.DB.PRODUCT_REF).get()
            .addOnSuccessListener { documents ->
                val productList = mutableListOf<Product>()
                for (document in documents) {
                    val product = document.toObject(Product::class.java).copy(id = document.id)
                    productList.add(product)
                }
                callback.onSuccess(productList)
            }
            .addOnFailureListener { e ->
                callback.onFailure(e.message ?: "Error loading products")
            }
    }

    fun saveRoutine(userId: String, routine: Routine, callback: FirestoreCallback) {
        val routineId = routine.id.ifEmpty {
            db.collection(Constants.DB.USER_REF).document(userId)
                .collection(Constants.DB.ROUTINE_REF).document().id
        }

        val routineWithId = routine.copy(id = routineId)

        db.collection(Constants.DB.USER_REF).document(userId)
            .collection(Constants.DB.ROUTINE_REF).document(routineId)
            .set(routineWithId)
            .addOnSuccessListener { callback.onSuccess() }
            .addOnFailureListener { e -> callback.onFailure(e.message ?: "Failed to save routine") }
    }

    fun getUserRoutines(userId: String, callback: RoutinesCallback) {
        db.collection(Constants.DB.USER_REF).document(userId)
            .collection(Constants.DB.ROUTINE_REF).get()
            .addOnSuccessListener { documents ->
                val routinesList =
                    documents.mapNotNull { it.toObject(Routine::class.java).copy(id = it.id) }
                callback.onSuccess(routinesList)
            }
            .addOnFailureListener { e ->
                callback.onFailure(e.message ?: "Failed to load routines")
            }
    }

    fun getAllSharedRoutines(callback: SharedRoutinesCallback) {
        db.collection(Constants.DB.SHARED_ROUTINE_REF).get()
            .addOnSuccessListener { documents ->
                val routinesList = documents.map { it.toObject(SharedRoutine::class.java) }
                callback.onSuccess(routinesList)
            }
            .addOnFailureListener { e ->
                callback.onFailure(e.message ?: "Error loading shared routines")
            }
    }

    fun updateSharedRoutineLikes(sharedRoutine: SharedRoutine, callback: FirestoreCallback) {
        db.collection(Constants.DB.SHARED_ROUTINE_REF).document(sharedRoutine.id)
            .update(
                mapOf(
                    "likeCount" to sharedRoutine.likeCount,
                    "likedBy" to sharedRoutine.likedBy
                )
            )
            .addOnSuccessListener { callback.onSuccess() }
            .addOnFailureListener { e ->
                callback.onFailure(
                    e.message ?: "Failed to update shared routine"
                )
            }
    }

    fun listenForSharedRoutineUpdates(callback: SharedRoutinesCallback) {
        db.collection(Constants.DB.SHARED_ROUTINE_REF)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    callback.onFailure(e.message ?: "Error listening for updates")
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val routinesList = snapshots.map { it.toObject(SharedRoutine::class.java) }
                    callback.onSuccess(routinesList)
                }
            }
    }

    fun saveShareRoutine(sharedRoutine: SharedRoutine, callback: FirestoreCallback) {
        db.collection(Constants.DB.SHARED_ROUTINE_REF).document(sharedRoutine.id).set(sharedRoutine)
            .addOnSuccessListener { callback.onSuccess() }.addOnFailureListener { e ->
                callback.onFailure(
                    e.message ?: "Failed to share routine"
                )
            }
    }

    fun generateDocumentId(collectionPath: String): String {
        return db.collection(collectionPath).document().id
    }



    fun addSampleProductsToGlobal() {
        val sampleProducts = listOf(
            Product(
                id = "",
                picture = "gs://clearglow-ba4bb.firebasestorage.app/p1.jpg",
                name = "Estée Lauder Advanced Night Repair Serum",
                brand = "Estée Lauder",
                skinType = "All",
                category = "Serum",
                ingredients = listOf(
                    "Bifida Ferment Lysate",
                    "Tripeptide-32",
                    "Sodium Hyaluronate"
                ),
                shelfLifeMonths = 18,
                barcode = "027131953728"
            )
        )

        val firestoreManager = FirestoreManager.getInstance()
        for (product in sampleProducts) {
            firestoreManager.saveProductToGlobal(product, object : FirestoreCallback {
                override fun onSuccess() {
                    println("Product added: ${product.name} (${product.category})")
                }

                override fun onFailure(errorMessage: String) {
                    println("Error adding product: ${product.name}, Error: $errorMessage")
                }
            })
        }
    }

}




