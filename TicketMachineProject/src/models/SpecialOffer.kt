package models
import java.time.LocalDate

data class SpecialOffer(
    val stationName: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val discountFactor: Double // e.g., 0.8 -> 20% off
) {
    fun isActive(today: LocalDate = LocalDate.now()): Boolean {
        return (today.isEqual(startDate) || today.isAfter(startDate)) &&
                (today.isEqual(endDate) || today.isBefore(endDate))
    }
}
