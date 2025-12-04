package users

class Admin(
    username: String,
    password: String
) : User(username, password, isAdmin = true)
