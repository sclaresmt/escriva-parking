package es.escriva.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import es.escriva.domain.VehicleRecord

@Dao
interface VehicleRecordDao {

    @Query("SELECT * FROM VehicleRecord WHERE tokenId = :tokenId AND active == 1 LIMIT 1")
    suspend fun findFirstActiveVehicleRecordByTokenId(tokenId: Long): VehicleRecord

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vehicleRecord: VehicleRecord): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(vehicleRecord: VehicleRecord): Int

    @Query("SELECT * FROM VehicleRecord WHERE dayId = :dayId")
    suspend fun findByDay(dayId: Long): List<VehicleRecord>

    @Query("SELECT * FROM VehicleRecord WHERE tokenId = :tokenId AND active = 1 LIMIT 1")
    suspend fun findActiveByTokenId(tokenId: Long): VehicleRecord?

}