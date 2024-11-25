package com.example.physioconsult.login.SignUp

// Example usage:
data class RegisterCredentials(
    var name: String = "",
    var surname: String = "",
    var email: String = "",
    var password: String = "",
    var repeatPassword: String = "",
    var remember: Boolean = false
) {

    // Checking if credentials are not empty
    fun isNotEmpty(): Boolean {
        return name.isNotEmpty() && surname.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && repeatPassword.isNotEmpty()
    }
}