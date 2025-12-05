import java.time.LocalDate
import core.TicketMachine
import models.Station
import models.TicketType
import models.SpecialOffer
import users.Admin
import users.User
import users.LoginManager

fun main() {

    val machine = TicketMachine("Central")

    // Hard-coded stations
    listOf(
        Station("London", 12.50, 20.00),
        Station("Bristol", 8.00, 14.00),
        Station("Oxford", 6.50, 11.00)
    ).forEach(machine::addStation)

    // Hard-coded users
    val admin = Admin("admin", "adminpass")
    val guest = User("guest", "guestpass", isAdmin = false)
    val loginManager = LoginManager(listOf(admin, guest))

    mainMenu(machine, loginManager)
}


fun mainMenu(machine: TicketMachine, login: LoginManager) {

    while (true) {
        println(
            """
            ====== Ticket Machine ======
            1) User Mode
            2) Admin Login
            3) Exit
            """.trimIndent()
        )
        print("Choose option: ")

        when (readLine()?.trim()) {
            "1" -> userMenu(machine)
            "2" -> adminLogin(machine, login)
            "3" -> return
            else -> println("Invalid option. Try again.")
        }
    }
}

fun adminLogin(machine: TicketMachine, login: LoginManager) {
    val user = login.login()
    when {
        user == null -> println("Login failed.")
        !user.isAdmin -> println("Access denied. Not an admin.")
        else -> adminMenu(machine, user as Admin)
    }
}

fun userMenu(machine: TicketMachine) {

    while (true) {
        println(
            """
            --- User Menu ---
            1) Search Ticket
            2) Insert Money
            3) Buy Ticket
            4) Refund
            5) Back
            """.trimIndent()
        )
        print("Choose: ")

        when (readLine()?.trim()) {

            "1" -> {
                print("Enter destination: ")
                machine.searchStation(readLine()?.trim() ?: "")
                    ?.let { println("${it.name} | Single £${it.singlePrice} | Return £${it.returnPrice}") }
                    ?: println("Station not found.")
            }

            "2" -> {
                print("Enter amount: ")
                readLine()?.toDoubleOrNull()
                    ?.let(machine::insertMoney)
                    ?: println("Invalid amount.")
            }

            "3" -> {
                print("Destination: ")
                val dest = readLine()?.trim() ?: ""

                print("1) Single 2) Return: ")
                val type = when (readLine()?.trim()) {
                    "1" -> TicketType.SINGLE
                    "2" -> TicketType.RETURN
                    else -> {
                        println("Invalid type."); null
                    }
                }

                if (type != null) machine.buyTicket(dest, type)
            }

            "4" -> machine.refund()
            "5" -> return
            else -> println("Invalid option.")
        }
    }
}

fun adminMenu(machine: TicketMachine, admin: Admin) {

    while (true) {
        println(
            """
            --- Admin Menu (Logged in as ${admin.username}) ---
            1) View stations
            2) Add station
            3) Edit station
            4) Change all prices
            5) View takings
            6) Offer management
            7) Logout
            """.trimIndent()
        )
        print("Choose: ")

        when (readLine()?.trim()) {
            "1" -> machine.viewAllStations()
            "2" -> machine.addStationInteractive()
            "3" -> editStationMenu(machine)
            "4" -> updatePrices(machine)
            "5" -> println(
                """
                Total takings: £${machine.getTotalTakings()}
                Inserted credit: £${machine.getInsertedAmount()}
                """.trimIndent()
            )
            "6" -> offerMenu(machine)
            "7" -> return
            else -> println("Invalid option.")
        }
    }
}

fun editStationMenu(machine: TicketMachine) {
    print("Station name: ")
    val name = readLine()?.trim() ?: return

    print("New single price (blank skip): ")
    val single = readLine()?.toDoubleOrNull()

    print("New return price (blank skip): ")
    val ret = readLine()?.toDoubleOrNull()

    machine.editStation(name, single, ret)
}

fun updatePrices(machine: TicketMachine) {
    print("Enter factor (1.1 = +10%, 0.8 = -20%): ")
    readLine()?.toDoubleOrNull()
        ?.let(machine::changeAllPrices)
        ?: println("Invalid factor.")
}

fun offerMenu(machine: TicketMachine) {

    while (true) {
        println(
            """
            --- Offer Management ---
            1) View offers
            2) Add offer
            3) Delete offers
            4) Back
            """.trimIndent()
        )
        print("Choose: ")

        when (readLine()?.trim()) {
            "1" -> machine.viewOffers()
            "2" -> addOfferMenu(machine)
            "3" -> {
                print("Station name: ")
                machine.deleteOffersForStation(readLine()?.trim() ?: "")
            }
            "4" -> return
            else -> println("Invalid option.")
        }
    }
}

fun addOfferMenu(machine: TicketMachine) {

    print("Station name: ")
    val name = readLine()?.trim() ?: ""

    print("Start (YYYY-MM-DD): ")
    val start = readLine()?.trim()

    print("End (YYYY-MM-DD): ")
    val end = readLine()?.trim()

    print("Discount (0.8 = 20% off): ")
    val factor = readLine()?.toDoubleOrNull()

    try {
        if (start != null && end != null && factor != null) {
            machine.addOffer(
                SpecialOffer(
                    name,
                    LocalDate.parse(start),
                    LocalDate.parse(end),
                    factor
                )
            )
        } else println("Invalid input.")
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}
