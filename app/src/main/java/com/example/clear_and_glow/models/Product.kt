package com.example.clear_and_glow.models

import android.util.Log
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class Product(
    val id: String = "",
    val picture: String = "",
    val name: String = "",
    val brand: String = "",
    val category: String = "",
    val skinType: String? = null,
    val ingredients: List<String> = emptyList(),
    var openingDate: String? = null,  // when the user starts using the product
    val shelfLifeMonths: Int = 6, // default: 6 months
    val barcode: String? = null,
    var isCollapsed: Boolean = false
) {
    fun toggleCollapse() = apply { this.isCollapsed = !this.isCollapsed }


    fun calculateExpiryDate(): String? {
        if (openingDate.isNullOrEmpty()) {
            return null // Return null if openingDate is not set
        }

        return try {
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            val localDate = LocalDate.parse(openingDate, formatter)
            val expiryDate = localDate.plusMonths(shelfLifeMonths.toLong())
            expiryDate.format(formatter) // Return formatted expiry date
        } catch (e: Exception) {
            Log.e("DateParsing", "Invalid date format: $openingDate", e)
            null // Return null if parsing fails
        }
    }


    class Builder {
        private var id: String = ""
        private var picture: String = ""
        private var name: String = ""
        private var brand: String = ""
        private var category: String = ""
        private var skinType: String? = null
        private var ingredients: List<String> = emptyList()
        private var openingDate: String? = null
        private var shelfLifeMonths: Int = 6
        private var barcode: String? = null
        private var isCollapsed: Boolean = false

        fun id(id: String) = apply { this.id = id }
        fun picture(picture: String) = apply { this.picture = picture }
        fun name(name: String) = apply { this.name = name }
        fun brand(brand: String) = apply { this.brand = brand }
        fun category(category: String) = apply { this.category = category }
        fun skinType(skinType: String?) = apply { this.skinType = skinType }
        fun ingredients(ingredients: List<String>) = apply { this.ingredients = ingredients }
        fun openingDate(openingDate: String?) = apply { this.openingDate = openingDate }
        fun shelfLifeMonths(shelfLifeMonths: Int) = apply { this.shelfLifeMonths = shelfLifeMonths }
        fun barcode(barcode: String?) = apply { this.barcode = barcode }
        fun isCollapsed(isCollapsed: Boolean) = apply { this.isCollapsed = isCollapsed }

        fun build() = Product(
            id, picture, name, brand, category, skinType, ingredients, openingDate, shelfLifeMonths, barcode, isCollapsed
        )
    }
}
