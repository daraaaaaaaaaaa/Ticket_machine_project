package app
import java.time.LocalDate
import core.TicketMachine
import models.Station
import models.TicketType
import models.SpecialOffer
import users.Admin
import users.User
import users.LoginManager

import core.TicketMachine
import models.Station
import models.TicketType
import models.SpecialOffer
import users.Admin
import users.User
import users.LoginManager

fun main() {
    val machine = TicketMachine("Central")

    // Sample stations
    machine.addStation(Station("London", 12.50, 20.00))
    machine.addStation(Station("Bristol", 8.00, 14.00))
    machine.addStation(Station("Oxford", 6.50, 11.00))

    // Users (Group C requirement)
    val adminUser = Admin("admin", "adminpass")
    val normalUser = User("guest", "guestpass", false)
    val users = listOf(adminUser, normalUser)

    val loginManager = LoginManager(users)

    mainMenu(machine, loginManager)
}

fun mainMenu(machine: TicketMachine, loginManager: LoginManager) {
    while (true) {
        println("\n====== Ticket Machine ======")
        println("1) User Mode")
        println("2) Admin Login")
        println("3) Exit")
        print("Choose: ")

        when (readLine()?.trim()) {
            "1" -> userMenu(machine)

            "2" -> {
                val user = loginManager.login()
                if (user == null) {
                    println("Login failed.")
                } else if (!user.isAdmin) {
                    println("Access denied. Not an admin.")
                } else {
                    adminMenuWithLogin(machine, loginManager, user as Admin)
                }
            }

            "3" -> {
                println("Goodbye.")
                return
            }

            else -> println("Invalid choice.")
        }
    }
}

fun userMenu(machine: TicketMachine) {
    while (true) {
        println("\n--- User Menu ---")
        println("1) Search ticket")
        println("2) Insert money")
        println("3) Buy ticket")
        println("4) Refund")
        println("5) Back")
        print("Choose: ")

        when (readLine()?.trim()) {

            "1" -> {
                print("Enter destination: ")
                val dest = readLine()?.trim() ?: ""
                val st = machine.searchStation(dest)
                if (st == null) {
                    println("Destination not found.")
                } else {
                    println("${st.name} | Single: £${"%.2f".format(st.singlePrice)} | Return: £${"%.2f".format(st.returnPrice)}")
                }
            }

            "2" -> {
                print("Enter amount to insert: ")
                val amt = readLine()?.toDoubleOrNull()
                if (amt == null) println("Invalid amount.")
                else machine.insertMoney(amt)
            }

            "3" -> {
                print("Enter destination: ")
                val dest = readLine()?.trim() ?: ""

                print("Select ticket type (1=Single, 2=Return): ")
                val t = when (readLine()?.trim()) {
                    "1" -> TicketType.SINGLE
                    "2" -> TicketType.RETURN
                    else -> {
                        println("Invalid type.")
                        null
                    }
                }

                if (t != null) {
                    val ok = machine.buyTicket(dest, t)
                    if (!ok) println("Buy failed.")
                }
            }

            "4" -> machine.refund()

            "5" -> return

            else -> println("Invalid choice.")
        }
    }
}

fun adminMenuWithLogin(machine: TicketMachine, loginManager: LoginManager, admin: Admin) {
    while (true) {
        println("\n--- Admin Menu (Logged in as ${admin.username}) ---")
        println("1) View stations")
        println("2) Add station")
        println("3) Edit station")
        println("4) Change all prices by factor")
        println("5) View takings & inserted credit")
        println("6) Offer management")
        println("7) Logout")
        print("Choose: ")

        when (readLine()?.trim()) {

            "1" -> machine.viewAllStations()

            "2" -> machine.addStationInteractive()

            "3" -> {
                print("Enter station name to edit: ")
                val name = readLine()?.trim() ?: ""

                print("Enter new single price (blank to skip): ")
                val sStr = readLine()?.trim()
                val single = sStr?.toDoubleOrNull()

                print("Enter new return price (blank to skip): ")
                val rStr = readLine()?.trim()
                val ret = rStr?.toDoubleOrNull()

                machine.editStation(name, single, ret)
            }

            "4" -> {
                print("Enter factor (e.g., 1.1 or 0.9): ")
                val factor = readLine()?.toDoubleOrNull()
                if (factor == null) println("Invalid factor.")
                else machine.changeAllPrices(factor)
            }

            "5" -> {
                println("Total takings: £${"%.2f".format(machine.getTotalTakings())}")
                println("Inserted credit: £${"%.2f".format(machine.getInsertedAmount())}")
            }

            "6" -> offerManagementMenu(machine)

            "7" -> {
                println("Logging out.")
                return
            }

            else -> println("Invalid choice.")
        }
    }
}

fun offerManagementMenu(machine: TicketMachine) {
    while (true) {
        println("\n--- Offer Management ---")
        println("1) View offers")
        println("2) Add offer")
        println("3) Delete offers for station")
        println("4) Back")
        print("Choose: ")

        when (readLine()?.trim()) {

            "1" -> machine.viewOffers()

            "2" -> {
                print("Enter station: ")
                val stName = readLine()?.trim() ?: ""

                print("Start date (YYYY-MM-DD): ")
                val s = readLine()?.trim()

                print("End date (YYYY-MM-DD): ")
                val e = readLine()?.trim()

                print("Discount factor (e.g., 0.8): ")
                val f = readLine()?.toDoubleOrNull()

                if (s == null || e == null || f == null) {
                    println("Invalid input.")
                } else {
                    try {
                        val start = LocalDate.parse(s)
                        val end = LocalDate.parse(e)

                        if (end.isBefore(start)) {
                            println("End date must be after start date.")
                        } else {
                            machine.addOffer(
                                SpecialOffer(stName, start, end, f)
                            )
                        }

                    } catch (ex: Exception) {
                        println("Date error: ${ex.message}")
                    }
                }
            }

            "3" -> {
                print("Enter station name: ")
                val st = readLine()?.trim() ?: ""
                machine.deleteOffersForStation(st)
            }

            "4" -> return

            else -> println("Invalid choice.")
        }
    }
}
