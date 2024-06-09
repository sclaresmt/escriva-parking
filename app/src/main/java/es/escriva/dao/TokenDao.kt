package es.escriva.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import es.escriva.domain.Token

@Dao
interface TokenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(token: Token): Long

    @Query("SELECT * FROM Token WHERE id = :tokenId")
    fun findById(tokenId: Long): Token

}