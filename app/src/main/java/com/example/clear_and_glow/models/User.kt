package com.example.clear_and_glow.models

data class User(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val dob: String = "",
    val uid: String = ""
) {
    class Builder {
        private var firstName: String = ""
        private var lastName: String = ""
        private var email: String = ""
        private var dob: String = ""
        private var uid: String = ""

        fun firstName(firstName: String) = apply { this.firstName = firstName }
        fun lastName(lastName: String) = apply { this.lastName = lastName }
        fun email(email: String) = apply { this.email = email }
        fun dob(dob: String) = apply { this.dob = dob }
        fun uid(uid: String) = apply { this.uid = uid } // נוסיף UID

        fun build() = User(firstName, lastName, email, dob, uid)
    }
}
