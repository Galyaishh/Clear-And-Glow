package com.example.clear_and_glow.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.clear_and_glow.interfaces.FirestoreCallback
import com.example.clear_and_glow.interfaces.ProductCategoryListCallback
import com.example.clear_and_glow.interfaces.ProductListCallback
import com.example.clear_and_glow.models.Product
import com.example.clear_and_glow.models.ProductCategory
import com.example.clear_and_glow.utilities.FirestoreManager
import com.example.clear_and_glow.utilities.ImageLoader
import com.example.clear_and_glow.utilities.TimeFormater
import com.google.firebase.firestore.FirebaseFirestore

class ProductsViewModel : ViewModel() {
    private val firestoreManager = FirestoreManager.getInstance()

    private val _userProducts = MutableLiveData<List<Product>>()
    val userProducts: LiveData<List<Product>> get() = _userProducts

    private val _globalProducts = MutableLiveData<List<Product>>()
    val globalProducts: LiveData<List<Product>> get() = _globalProducts

    private val _categories = MutableLiveData<List<ProductCategory>>()
    val categories: LiveData<List<ProductCategory>> get() = _categories

    fun loadUserProducts(userId: String) {
        firestoreManager.getUserProducts(userId, object : ProductListCallback {
            override fun onSuccess(products: List<Product>) {
                _userProducts.value = products
            }

            override fun onFailure(errorMessage: String) {
                _userProducts.value = emptyList()
            }
        })
    }

//    fun loadGlobalProducts() {
//        firestoreManager.listenForGlobalProductsUpdates(object : ProductListCallback {
//            override fun onSuccess(products: List<Product>) {
//                _globalProducts.value = products
//            }
//
//            override fun onFailure(errorMessage: String) {
//                Log.e("ProductsViewModel", "Failed to fetch global products: $errorMessage")
//            }
//        })
//    }


    fun loadGlobalProducts() {
        firestoreManager.getAllGlobalProducts(object : ProductListCallback {
            override fun onSuccess(products: List<Product>) {
                _globalProducts.value = products
            }

            override fun onFailure(errorMessage: String) {
                _globalProducts.value = emptyList()
            }
        })
    }

//    fun fetchAllProductImages() {
//        val db = FirebaseFirestore.getInstance()
//        db.collection("products")
//            .get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    val productId = document.id
//                    val imageUrl = document.getString("picture") ?: ""
//                    ImageLoader.productImages[productId] = imageUrl
//                }
//            }
//            .addOnFailureListener {
//                Log.e("Firestore", "Failed to load product images", it)
//            }
//    }

    fun loadCategories() {
        firestoreManager.getAllCategories(object : ProductCategoryListCallback {
            override fun onSuccess(categories: List<ProductCategory>) {
                _categories.value = categories
            }

            override fun onFailure(errorMessage: String) {
                _categories.value = emptyList()
            }
        })
    }

    fun addProductsToUser(
        userId: String,
        products: List<Product>,
        callback: (Boolean, String?) -> Unit
    ) {
        firestoreManager.addProductsToUser(userId, products, object : FirestoreCallback {
            override fun onSuccess() {
                callback(true, null)
            }

            override fun onFailure(errorMessage: String) {
                callback(false, errorMessage)
            }
        })
    }

    fun refreshProducts() {
        firestoreManager.getAllGlobalProducts(object : ProductListCallback {
            override fun onSuccess(products: List<Product>) {
                _globalProducts.value = products // מעדכן את LiveData עם המוצרים החדשים
            }

            override fun onFailure(errorMessage: String) {
                Log.e("Firestore", "Failed to refresh products: $errorMessage")
            }
        })
    }




    fun updateProductDates(userId: String, product: Product) {
        product.openingDate = TimeFormater().getFormattedDate()
        firestoreManager.updateProductDates(userId, product).addOnSuccessListener {
            _userProducts.value = _userProducts.value?.map {
                if (it.id == product.id) product else it
            }
        }.addOnFailureListener {
            Log.e("Firestore", "Failed to update product date", it)
        }
    }

    fun filterProductsByCategory(category: ProductCategory, isGlobal: Boolean) {
        val products = if (isGlobal) _globalProducts.value else _userProducts.value
        val filteredProducts = if (category.name == "All") {
            products
        } else {
            products?.filter { it.category == category.name } ?: emptyList()
        }

        if (isGlobal) {
            _globalProducts.value = filteredProducts?.sortedBy { it.name }
        } else {
            _userProducts.value = filteredProducts?.sortedBy { it.name }
        }
    }

    fun searchProducts(query: String, isGlobal: Boolean) {
        val products = if (isGlobal) _globalProducts.value else _userProducts.value
        val filteredProducts =
            products?.filter { it.name.contains(query, ignoreCase = true) } ?: emptyList()
        if (isGlobal) {
            _globalProducts.value = filteredProducts
        } else {
            _userProducts.value = filteredProducts
        }
    }
}
