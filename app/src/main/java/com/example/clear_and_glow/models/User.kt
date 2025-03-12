package com.example.clear_and_glow.models

data class User(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val uid: String = "",
    val profilePicUrl: String = "",

    ) {
    class Builder {
        private var firstName: String = ""
        private var lastName: String = ""
        private var email: String = ""
        private var uid: String = ""
        private var profilePicUrl: String = ""

        fun firstName(firstName: String) = apply { this.firstName = firstName }
        fun lastName(lastName: String) = apply { this.lastName = lastName }
        fun email(email: String) = apply { this.email = email }
        fun uid(uid: String) = apply { this.uid = uid }
        fun profilePicUrl(profilePicUrl: String) = apply { this.profilePicUrl = profilePicUrl }

        fun build() = User(firstName, lastName, email, uid, profilePicUrl)
    }


    fun getFullName(): String {
        return "$firstName $lastName"
    }
}
