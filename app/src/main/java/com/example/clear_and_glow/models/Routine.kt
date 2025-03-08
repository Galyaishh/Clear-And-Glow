package com.example.clear_and_glow.models

data class Routine(
    val id: String = "",
    val name: String = "",
    val timeOfDay: String = "", // Morning / Evening
    val products: List<Product> = emptyList()
) {
    class Builder {
        private var id: String = ""
        private var name: String = ""
        private var timeOfDay: String = ""
        private var products: List<Product> = emptyList()

        fun id(id: String) = apply { this.id = id }
        fun name(name: String) = apply { this.name = name }
        fun timeOfDay(timeOfDay: String) = apply { this.timeOfDay = timeOfDay }
        fun products(products: List<Product>) = apply { this.products = products }

        fun build() = Routine(id, name, timeOfDay, products)

        fun getTimeOfDay(): String {
            return timeOfDay
        }

        fun getProducts(): List<Product> {
            return products
        }
    }
}
