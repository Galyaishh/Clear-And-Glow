package com.example.clear_and_glow.interfaces

import com.example.clear_and_glow.models.ProductCategory

interface ProductCategoryListCallback {
    fun onSuccess(categories: List<ProductCategory>)
    fun onFailure(errorMessage: String)
}