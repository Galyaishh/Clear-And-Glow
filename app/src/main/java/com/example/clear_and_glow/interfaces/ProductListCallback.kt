package com.example.clear_and_glow.interfaces

import com.example.clear_and_glow.models.Product

interface ProductListCallback {
    fun onSuccess(products: List<Product>)
    fun onFailure(errorMessage: String)
}