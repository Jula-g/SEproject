package com.example.physioconsult.login.LogIn

/**
 * Data class representing user credentials for login.
 *
 * @property login The user's login identifier. Default is an empty string.
 * @property pwd The user's password. Default is an empty string.
 * @property remember A flag indicating whether the user's login information should be remembered. Default is false.
 */
data class Credentials(
    var login: String = "",
    var pwd: String = "",
    var remember: Boolean = false
) {
    /**
     * Checks if the login and password fields are not empty.
     *
     * @return True if both login and password are not empty, false otherwise.
     */
    fun isNotEmpty(): Boolean {
        return login.isNotEmpty() && pwd.isNotEmpty()
    }
}
