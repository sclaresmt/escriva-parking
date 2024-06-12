package es.escriva.repository

import androidx.room.Transaction
import es.escriva.dao.DayDao
import es.escriva.dao.VehicleRecordDao
import es.escriva.domain.Day
import es.escriva.domain.Token
import es.escriva.domain.VehicleRecord
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.math.ceil

class DayAndVehiclesRepository(private val dayDao: DayDao, private val vehicleRecordDao: VehicleRecordDao) {

    @Transaction
    suspend fun enterAction(token: Token) {
        var day = dayDao.findFirstActiveDay()
        if (day == null) {
            day = Day(date = LocalDate.now())
            dayDao.insert(day)
        }
        val vehicleRecord = VehicleRecord(enterTime = LocalTime.now(), tokenId = token.id, dayId = day.id)
        vehicleRecordDao.insert(vehicleRecord)
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

    private fun calculateAmount(start: LocalTime, end: LocalTime): Double {
        val parkingMinutes = ChronoUnit.MINUTES.between(start, end)
        val rawAmount = parkingMinutes * 0.03 + 1
        // Redondear al decimal más cercano
        return ceil(rawAmount * 10) / 10
    }

    suspend fun closeDay(day: Day) {
        day.active = false
        dayDao.update(day)
        vehicleRecordDao.findByDay(day.id).forEach {
            if (!it.active) {
                it.active = false
                vehicleRecordDao.update(it)
            }
        }
    }

}