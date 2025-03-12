package com.example.clear_and_glow.models

import com.example.clear_and_glow.R

data class ProductCategory(
    val name: String = "",
    val iconResId: Int = R.drawable.unavailable_photo
) {
    constructor() : this("", R.drawable.unavailable_photo)

    companion object {
        private val categoryIcons = mapOf(
            "Cleanser" to R.drawable.ic_cleanser,
            "Serum" to R.drawable.ic_serum,
            "SPF" to R.drawable.ic_sunscreen,
            "Moisturizer" to R.drawable.ic_moisturizer,
            "Eye Cream" to R.drawable.ic_eye_cream
        )

        fun getCategory(name: String): ProductCategory {
            return ProductCategory(name, categoryIcons[name] ?: R.drawable.unavailable_photo)
        }

        fun getAllCategories(): List<ProductCategory> {
            return categoryIcons.map { (name, icon) -> ProductCategory(name, icon) }
        }
    }
}