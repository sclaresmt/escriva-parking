package es.escriva.repository

import VehicleRecordDao
import androidx.room.Transaction
import es.escriva.dao.DayDao
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
        val vehicleRecord = VehicleRecord(enterTime = LocalDateTime.now(), tokenId = token.id, dayId = day.id)
        vehicleRecordDao.insert(vehicleRecord)
    }

    @Transaction
    fun exitAction(token: Token) {
        val vehicleRecord =
            vehicleRecordDao.findFirstActiveVehicleRecordByTokenId(token.id)
        val exitTime = LocalDateTime.now()
        vehicleRecord.exitTime = LocalDateTime.now()
        vehicleRecord.active = false
        vehicleRecord.amount = calculateAmount(vehicleRecord.enterTime, exitTime)
        vehicleRecordDao.update(vehicleRecord)
    }

    private fun calculateAmount(start: LocalDateTime, end: LocalDateTime): Double {
        val parkingMinutes = ChronoUnit.MINUTES.between(start, end)
        return parkingMinutes * 0.03
    }

}