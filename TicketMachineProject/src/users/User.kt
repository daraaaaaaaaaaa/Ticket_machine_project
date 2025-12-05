package users

open class User(
    val username: String,
    private val password: String,
    val isAdmin: Boolean = false
) {
    fun authenticate(pass: String): Boolean = password == pass
}
