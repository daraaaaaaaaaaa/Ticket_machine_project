package core
import models.Station
import models.Ticket
import models.TicketType
import models.SpecialOffer
import java.time.LocalDate

class TicketMachine(private val origin: String = "ORIGIN") {

    private val stations = mutableListOf<Station>()
    private val offers = mutableListOf<SpecialOffer>()  // Optional group C feature

    private var insertedAmount: Double = 0.0
    private var totalTakings: Double = 0.0


    // ----------------------------
    //   Initialization Helpers
    // ----------------------------
    fun addStation(st: Station) = stations.add(st)

    fun getStations(): List<Station> = stations.toList()

    fun getInsertedAmount(): Double = insertedAmount

    fun getTotalTakings(): Double = totalTakings


    // ----------------------------
    //   USER FEATURES
    // ----------------------------
    fun searchStation(name: String): Station? {
        return stations.find { it.name.equals(name.trim(), ignoreCase = true) }
    }


    fun insertMoney(amount: Double) {
        if (amount <= 0.0) {
            println("Please insert a positive amount.")
            return
        }
        insertedAmount += amount
        println("Inserted: £${"%.2f".format(insertedAmount)}")
    }


    private fun bestOfferForStation(station: Station): SpecialOffer? {
        val today = LocalDate.now()
        return offers
            .filter { it.stationName.equals(station.name, true) && it.isActive(today) }
            .minByOrNull { it.discountFactor }
    }


    fun calculatePrice(station: Station, type: TicketType): Double {
        var price = station.getPrice(type)
        val offer = bestOfferForStation(station)

        if (offer != null) {
            price *= offer.discountFactor
        }
        return price
    }


    fun buyTicket(destinationName: String, type: TicketType): Boolean {
        val station = searchStation(destinationName)
        if (station == null) {
            println("Destination '$destinationName' not found.")
            return false
        }

        val price = calculatePrice(station, type)

        if (insertedAmount < price) {
            println("Insufficient funds. Price: £${"%.2f".format(price)}, Inserted: £${"%.2f".format(insertedAmount)}")
            return false
        }

        val ticket = Ticket(origin, station, type, price)
        println(ticket.formatted())

        insertedAmount -= price
        station.increaseSales(1)
        totalTakings += price

        println("Purchase successful. Remaining credit: £${"%.2f".format(insertedAmount)}")
        return true
    }


    fun refund(): Double {
        val refundAmount = insertedAmount
        insertedAmount = 0.0

        println("Refunded £${"%.2f".format(refundAmount)}")
        return refundAmount
    }


    // ----------------------------
    //   ADMIN FEATURES
    // ----------------------------
    fun viewAllStations() {
        if (stations.isEmpty()) {
            println("No stations available.")
            return
        }

        println("\nStations:")
        stations.forEach { s ->
            println("${s.name} | Single: £${"%.2f".format(s.singlePrice)} | Return: £${"%.2f".format(s.returnPrice)} | Sales: ${s.salesCount}")
        }
    }


    fun addStationInteractive() {
        print("Enter station name: ")
        val name = readLine()?.trim() ?: return

        print("Enter single price: ")
        val single = readLine()?.toDoubleOrNull()
        if (single == null) {
            println("Invalid single price")
            return
        }

        print("Enter return price: ")
        val ret = readLine()?.toDoubleOrNull()
        if (ret == null) {
            println("Invalid return price")
            return
        }

        addStation(Station(name, single, ret))
        println("Station added: $name")
    }


    fun editStation(name: String, newSingle: Double?, newReturn: Double?) {
        val st = searchStation(name)
        if (st == null) {
            println("Station not found.")
            return
        }

        if (newSingle != null) st.singlePrice = newSingle
        if (newReturn != null) st.returnPrice = newReturn

        println("Station updated: ${st.name}")
    }


    fun changeAllPrices(factor: Double) {
        if (factor <= 0.0) {
            println("Factor must be positive.")
            return
        }

        stations.forEach {
            it.singlePrice *= factor
            it.returnPrice *= factor
        }

        println("All prices updated by factor $factor")
    }


    // ----------------------------
    //   SPECIAL OFFERS (GROUP C)
    // ----------------------------
    fun addOffer(offer: SpecialOffer) {
        offers.add(offer)
        println("Added offer for ${offer.stationName}")
    }


    fun deleteOffersForStation(stationName: String) {
        val removed = offers.removeIf { it.stationName.equals(stationName, true) }

        if (removed)
            println("Offers removed for $stationName")
        else
            println("No offers found for $stationName")
    }


    fun viewOffers() {
        if (offers.isEmpty()) {
            println("No special offers.")
            return
        }

        println("\nSpecial Offers:")
        offers.forEach {
            println("${it.stationName} | ${it.startDate} to ${it.endDate} | Factor: ${it.discountFactor}")
        }
    }
}
