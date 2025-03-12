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

    private var originalUserProducts: List<Product> = listOf()
    private var originalGlobalProducts: List<Product> = listOf()


    private val _categories = MutableLiveData<List<ProductCategory>>()
    val categories: LiveData<List<ProductCategory>> get() = _categories


    fun loadUserProducts(userId: String) {
        firestoreManager.getUserProducts(userId, object : ProductListCallback {
            override fun onSuccess(products: List<Product>) {
                Log.d("ViewModelData", "User Products Loaded: ${products.size}")
                products.forEach { product ->
                    Log.d("ViewModelData", "Product: ${product.name}, OpeningDate: ${product.openingDate}")
                }
                originalUserProducts = products
                _userProducts.value = products
            }

            override fun onFailure(errorMessage: String) {
                Log.e("ViewModelData", "Failed to load user products: $errorMessage")
                _userProducts.value = emptyList()
            }
        })
    }


    fun loadGlobalProducts() {
        firestoreManager.getAllGlobalProducts(object : ProductListCallback {
            override fun onSuccess(products: List<Product>) {
                originalGlobalProducts = products
                _globalProducts.value = products
            }

            override fun onFailure(errorMessage: String) {
                _globalProducts.value = emptyList()
            }
        })
    }


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
                _globalProducts.value = products
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


    fun filterProductsByCategory(category: ProductCategory?, isGlobal: Boolean) {
        val allProducts = if (isGlobal) originalGlobalProducts else originalUserProducts
        val filteredProducts = if (category == null) {
            allProducts
        } else {
            allProducts.filter { it.category == category.name }
        }

        if (isGlobal) {
            _globalProducts.value = filteredProducts.sortedBy { it.name }
        } else {
            _userProducts.value = filteredProducts.sortedBy { it.name }
        }
    }


    fun searchProducts(query: String, isGlobal: Boolean) {
        val filteredList = if (query.isEmpty()) {
            if (isGlobal) originalGlobalProducts else originalUserProducts
        } else {
            val sourceList = if (isGlobal) originalGlobalProducts else originalUserProducts
            sourceList.filter { product ->
                product.name.contains(query, ignoreCase = true) ||
                        product.brand.contains(query, ignoreCase = true)
            }
        }
        if (isGlobal) {
            _globalProducts.value = filteredList
        } else {
            _userProducts.value = filteredList
        }
    }

}
