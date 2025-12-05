package users

class LoginManager(private val users: List<User>) {

    fun login(): User? {
        print("Enter username: ")
        val username = readLine()?.trim() ?: return null

        print("Enter password: ")
        val password = readLine()?.trim() ?: return null

        return users.find { it.username == username && it.authenticate(password) }
    }
}
