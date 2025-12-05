package models

data class Station(
    val name: String,
    var singlePrice: Double,
    var returnPrice: Double,
    var salesCount: Int = 0
) {

    fun getPrice(type: TicketType): Double {
        return when (type) {
            TicketType.SINGLE -> singlePrice
            TicketType.RETURN -> returnPrice
        }
    }

    fun increaseSales(count: Int) {
        salesCount += count
    }
}
