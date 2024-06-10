package es.escriva.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import es.escriva.domain.Token

@Dao
interface TokenDao {

    @Query("SELECT * FROM Token WHERE id = :tokenId")
    fun findById(tokenId: Long): Token

    @Upsert
    fun upsert(token: Token): Long

}