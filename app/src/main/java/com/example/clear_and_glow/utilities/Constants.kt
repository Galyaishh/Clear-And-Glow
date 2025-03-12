package com.example.clear_and_glow.utilities

object Constants {

    object DB{
        const val USER_REF = "users"
        const val PRODUCT_REF = "products"
        const val ROUTINE_REF = "routines"
        const val SHARED_ROUTINE_REF = "shared_routines"
        const val GLOBAL_PRODUCT_REF = "global_products"
        const val CATEGORY_REF = "categories"
    }

    object Storage{
        const val PROFILE_PICS_FOLDER = "profile_pics"
        const val PRODUCT_PICS_FOLDER = "product_pics"
    }

    object Pathes {
        fun getProfilePicPath(userId: String) = "${Storage.PROFILE_PICS_FOLDER}/$userId.jpg"
        fun getProductPicPath(productId: String) = "${Storage.PRODUCT_PICS_FOLDER}/$productId.jpg"
    }
}