package com.example.clear_and_glow.models

data class Ingredient(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val benefits: List<String> = emptyList(),
    var isCollapsed: Boolean = false
) {
    fun toggleCollapse() = apply { this.isCollapsed = !this.isCollapsed }

    class Builder {
        private var id: String = ""
        private var name: String = ""
        private var description: String = ""
        private var benefits: List<String> = emptyList()
        private var isCollapsed: Boolean = false

        fun id(id: String) = apply { this.id = id }
        fun name(name: String) = apply { this.name = name }
        fun description(description: String) = apply { this.description = description }
        fun benefits(benefits: List<String>) = apply { this.benefits = benefits }
        fun isCollapsed(isCollapsed: Boolean) = apply { this.isCollapsed = isCollapsed }

        fun build() = Ingredient(id, name, description, benefits, isCollapsed)
    }
}
