package es.escriva.repository

import androidx.room.Transaction
import es.escriva.dao.DayDao
import es.escriva.dao.VehicleRecordDao
import es.escriva.domain.Day
import es.escriva.domain.Token
import es.escriva.domain.VehicleRecord
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

class DayAndVehiclesRepository(private val dayDao: DayDao, private val vehicleRecordDao: VehicleRecordDao) {

    @Transaction
    suspend fun enterAction(token: Token) {
        var day = dayDao.findFirstActiveDay()
        if (day == null) {
            day = Day(date = LocalDate.now())
            val dayId = dayDao.insert(day)
            day = dayDao.findById(dayId)
        }
        val vehicleRecord = VehicleRecord(enterTime = LocalTime.now(), tokenId = token.id, dayId = day?.id!!)
        vehicleRecordDao.insert(vehicleRecord)
        day.vehiclesCount++
        dayDao.update(day)
    }

    @Transaction
    suspend fun exitAction(token: Token) {
        val vehicleRecord =
            vehicleRecordDao.findFirstActiveVehicleRecordByTokenId(token.id)
        val exitTime = LocalTime.now()
        vehicleRecord.exitTime = LocalTime.now()
        vehicleRecord.active = false
        vehicleRecord.amount = calculateAmount(vehicleRecord.enterTime, exitTime)
        vehicleRecordDao.update(vehicleRecord)

        var day = dayDao.findById(vehicleRecord.dayId)
        if (day != null) {
            day.dayAmount += vehicleRecord.amount
            dayDao.update(day)
        }
    }

    @Transaction
    suspend fun closeDay(day: Day) {
        vehicleRecordDao.findActiveByDay(day.id).forEach {
            if (it.active) {
                it.active = false
                vehicleRecordDao.update(it)
            }
        }
        day.active = false
        dayDao.update(day)
    }

    suspend fun findActiveVehicleRecordByTokenId(tokenId: Long): VehicleRecord? {
        return vehicleRecordDao.findActiveByTokenId(tokenId)
    }

    suspend fun getVehicleRecordsForDay(dayId: Long): List<VehicleRecord> {
        return vehicleRecordDao.findByDay(dayId)
    }

    suspend fun newActiveDay(): Day {
        val day = Day(date = LocalDate.now())
        dayDao.insert(day)
        return day
    }

    suspend fun getActiveDay(): Day? {
        return dayDao.findFirstActiveDay()
    }

    suspend fun getPreviousRegisteredDay(day: Day): Day? {
        return dayDao.findFirsPreviousRegisteredDay(day.date)
    }

    private fun calculateAmount(start: LocalTime, end: LocalTime): BigDecimal {
        val parkingMinutes = BigDecimal(ChronoUnit.MINUTES.between(start, end))
        val rawAmount = parkingMinutes.multiply(BigDecimal("0.03")).add(BigDecimal("1"))
        return rawAmount.setScale(1, RoundingMode.HALF_UP)
    }

}