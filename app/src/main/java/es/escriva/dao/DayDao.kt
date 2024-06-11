package es.escriva.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import es.escriva.domain.Day

@Dao
interface DayDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(day: Day): Long

    @Query("SELECT * FROM Day WHERE active = 1 ORDER BY date ASC LIMIT 1")
    fun findFirstActiveDay(): Day?

    @Query("SELECT * FROM Day WHERE id = :dayId")
    fun findById(dayId: Long): Day?

    @Update
    fun update(day: Day)

}