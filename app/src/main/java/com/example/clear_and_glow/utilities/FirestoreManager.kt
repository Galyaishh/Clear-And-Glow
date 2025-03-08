package com.example.clear_and_glow.utilities

import com.example.clear_and_glow.R
import com.example.clear_and_glow.interfaces.FirestoreCallback
import com.example.clear_and_glow.interfaces.ProductCategoryListCallback
import com.example.clear_and_glow.interfaces.ProductListCallback
import com.example.clear_and_glow.interfaces.RoutinesCallback
import com.example.clear_and_glow.interfaces.UserCallback
import com.example.clear_and_glow.models.Product
import com.example.clear_and_glow.models.ProductCategory
import com.example.clear_and_glow.models.Routine
import com.example.clear_and_glow.models.User
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

    fun saveProductToGlobal(product: Product, callback: FirestoreCallback) {
        val productRef = db.collection(Constants.DB.GLOBAL_PRODUCT_REF).document()
        val productWithId = product.copy(id = productRef.id)

        productRef.set(productWithId)
            .addOnSuccessListener { callback.onSuccess() }
            .addOnFailureListener { e -> callback.onFailure(e.message ?: "Failed to save product") }
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
            categoryRef.whereEqualTo("name", categoryName).get().addOnSuccessListener { ducuments ->
                if (ducuments.isEmpty) {
                    val newCategory = ProductCategory(categoryName, R.drawable.unavailable_photo)
                    categoryRef.add(newCategory)
                }
            }
                .addOnFailureListener { e -> println("Error updating categories: ${e.message}") }
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
            val newProductRef = userProductsRef.document()
            batch.set(newProductRef, product)
        }
        batch.commit()
            .addOnSuccessListener { callback.onSuccess() }
            .addOnFailureListener { e ->
                callback.onFailure(
                    e.message ?: "Failed to add products"
                )
            }

//        fun getUserProducts(userId: String, callback: ProductListCallback) {
//            db.collection(Constants.DB.USER_REF).document(userId).collection("products").get()
//                .addOnSuccessListener { documents ->
//                    val productList = mutableListOf<Product>()
//                    for (document in documents) {
//                        val product = document.toObject(Product::class.java).copy(id = document.id)
//                        productList.add(product)
//                    }
//                    callback.onSuccess(productList)
//                }
//                .addOnFailureListener { e ->
//                    callback.onFailure(e.message ?: "Error loading products")
//                }
//        }


        fun saveRoutine(userId: String, routine: Routine, callback: FirestoreCallback) {
            db.collection(Constants.DB.USER_REF).document(userId)
                .collection(Constants.DB.ROUTINE_REF)
                .document(routine.id).set(routine)
                .addOnSuccessListener { callback.onSuccess() }
                .addOnFailureListener { e ->
                    callback.onFailure(
                        e.message ?: "Failed to save routine"
                    )
                }
        }

//        fun getUserRoutines(userId: String, callback: RoutinesCallback) {
//            db.collection(Constants.DB.USER_REF).document(userId)
//                .collection(Constants.DB.ROUTINE_REF).get()
//                .addOnSuccessListener { documents ->
//                    val routinesList = documents.mapNotNull { it.toObject(Routine::class.java) }
//                    callback.onSuccess(routinesList)
//                }
//                .addOnFailureListener { e ->
//                    callback.onFailure(
//                        e.message ?: "Failed to load routines"
//                    )
//                }
//        }


    }


    fun addSampleProductsToGlobal() {
        val sampleProducts = listOf(
            Product(
                id = "",
                picture = "https://example.com/cerave-cleanser.jpg",
                name = "CeraVe Hydrating Facial Cleanser",
                brand = "CeraVe",
                skinType = "Normal to Dry",
                category = "Cleanser",
                ingredients = listOf("Ceramides", "Hyaluronic Acid", "Glycerin"),
                shelfLifeMonths = 12,
                barcode = "3606000537477"
            ),
            Product(
                id = "",
                picture = "https://example.com/la-roche-posay-spf50.jpg",
                name = "La Roche-Posay Anthelios Melt-in Milk Sunscreen SPF 50",
                brand = "La Roche-Posay",
                skinType = "All",
                category = "SPF",
                ingredients = listOf("Avobenzone", "Homosalate", "Octisalate", "Octocrylene"),
                shelfLifeMonths = 6,
                barcode = "3337872419522"
            ),
            Product(
                id = "",
                picture = "https://example.com/the-ordinary-niacinamide.jpg",
                name = "The Ordinary Niacinamide 10% + Zinc 1%",
                brand = "The Ordinary",
                skinType = "Oily, Acne-Prone",
                category = "Serum",
                ingredients = listOf("Niacinamide", "Zinc PCA", "Tamarindus Indica Seed Gum"),
                shelfLifeMonths = 12,
                barcode = "769915190156"
            ),
            Product(
                id = "",
                picture = "https://example.com/estee-lauder-advanced-night-repair.jpg",
                name = "Estée Lauder Advanced Night Repair Serum",
                brand = "Estée Lauder",
                skinType = "All",
                category = "Serum",
                ingredients = listOf("Bifida Ferment Lysate", "Tripeptide-32", "Sodium Hyaluronate"),
                shelfLifeMonths = 18,
                barcode = "027131953728"
            ),
            Product(
                id = "",
                picture = "https://example.com/neutrogena-hydro-boost.jpg",
                name = "Neutrogena Hydro Boost Water Gel",
                brand = "Neutrogena",
                skinType = "Normal to Oily",
                category = "Moisturizer",
                ingredients = listOf("Hyaluronic Acid", "Dimethicone", "Glycerin"),
                shelfLifeMonths = 12,
                barcode = "070501110167"
            ),
            Product(
                id = "",
                picture = "https://example.com/kiehl-avocado-eye-cream.jpg",
                name = "Kiehl's Creamy Eye Treatment with Avocado",
                brand = "Kiehl's",
                skinType = "All",
                category = "Eye Cream",
                ingredients = listOf("Avocado Oil", "Shea Butter", "Beta-Carotene"),
                shelfLifeMonths = 12,
                barcode = "3605975020880"
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

    fun getUserProducts(userId: String, callback: ProductListCallback) {
        db.collection(Constants.DB.USER_REF).document(userId).collection("products").get()
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

    fun getUserRoutines(userId: String, callback: RoutinesCallback) {
        db.collection(Constants.DB.USER_REF).document(userId)
            .collection(Constants.DB.ROUTINE_REF).get()
            .addOnSuccessListener { documents ->
                val routinesList = documents.mapNotNull { it.toObject(Routine::class.java) }
                callback.onSuccess(routinesList)
            }
            .addOnFailureListener { e ->
                callback.onFailure(
                    e.message ?: "Failed to load routines"
                )
            }
    }





}


