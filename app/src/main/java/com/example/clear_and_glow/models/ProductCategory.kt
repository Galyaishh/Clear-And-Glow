package com.example.clear_and_glow.models

import com.example.clear_and_glow.R

data class ProductCategory(
    val name: String = "",
    val iconResId: Int = 0
) {
    constructor() : this("", 0)

    companion object {
        private val categoryIcons = mapOf(
            "Cleanser" to R.drawable.unavailable_photo,
            "Serum" to R.drawable.unavailable_photo,
            "SPF" to R.drawable.unavailable_photo,
            "Moisturizer" to R.drawable.unavailable_photo,
            "Eye Cream" to R.drawable.unavailable_photo
        )

        fun getCategory(name: String): ProductCategory {
            return ProductCategory(name, categoryIcons[name] ?: R.drawable.unavailable_photo)
        }

        fun getAllCategories(): List<ProductCategory> {
            return categoryIcons.map { (name, icon) -> ProductCategory(name, icon) }
        }
    }
}