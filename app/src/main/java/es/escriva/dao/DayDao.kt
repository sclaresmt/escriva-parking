package es.escriva.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import es.escriva.domain.Day
import java.time.LocalDate

@Dao
interface DayDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(day: Day): Long

    @Query("SELECT * FROM Day WHERE active = 1 ORDER BY date ASC LIMIT 1")
    suspend fun findFirstActiveDay(): Day?

    @Query("SELECT * FROM Day WHERE id = :dayId")
    suspend fun findById(dayId: Long): Day?

    @Update
    suspend fun update(day: Day)

    @Query("SELECT * FROM Day WHERE date < :date ORDER BY date DESC LIMIT 1")
    suspend fun findFirsPreviousRegisteredDay(date: LocalDate): Day?

}