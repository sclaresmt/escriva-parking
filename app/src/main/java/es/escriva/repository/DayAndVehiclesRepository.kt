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

class DayAndVehiclesRepository(private val dayDao: DayDao, private val vehicleRecordDao: VehicleRecordDao) {

    @Transaction
    fun enterAction(token: Token) {
        var day = dayDao.findFirstActiveDay()
        if (day == null) {
            day = Day(date = LocalDate.now())
            dayDao.insert(day)
        }
        val vehicleRecord = VehicleRecord(enterTime = LocalTime.now(), tokenId = token.id, dayId = day.id)
        vehicleRecordDao.insert(vehicleRecord)
    }

    @Transaction
    fun exitAction(token: Token) {
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

    fun getVehicleRecordsForDay(dayId: Long): List<VehicleRecord> {
        return vehicleRecordDao.findByDay(dayId)
    }

    fun newActiveDay(): Day {
        val day = Day(date = LocalDate.now())
        dayDao.insert(day)
        return day
    }

    fun getActiveDay(): Day? {
        return dayDao.findFirstActiveDay()
    }

    private fun calculateAmount(start: LocalTime, end: LocalTime): Double {
        val parkingMinutes = ChronoUnit.MINUTES.between(start, end)
        return parkingMinutes * 0.03 + 1
    }

}