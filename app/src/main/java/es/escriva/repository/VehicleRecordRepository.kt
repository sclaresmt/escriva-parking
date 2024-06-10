package es.escriva.repository

import es.escriva.dao.VehicleRecordDao
import es.escriva.domain.VehicleRecord

class VehicleRecordRepository(private val vehicleRecordDao: VehicleRecordDao) {

    fun insert(vehicleRecord: VehicleRecord): Long {
        return vehicleRecordDao.insert(vehicleRecord)
    }

}