package es.escriva.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import es.escriva.domain.Token

@Dao
interface TokenDao {

    @Query("SELECT * FROM Token WHERE id = :tokenId")
    suspend fun findById(tokenId: Long): Token?

    @Upsert
    suspend fun upsert(token: Token): Long

}