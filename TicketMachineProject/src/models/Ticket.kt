package models

data class Ticket(
    val origin: String,
    val destination: Station,
    val type: TicketType,
    val price: Double
) {
    fun formatted(): String {
        val priceStr = "%.2f".format(price)

        return """
            ***
            [$origin]
            to
            [${destination.name}]
            Price: £$priceStr [${type}]
            ***
        """.trimIndent()
    }
}
