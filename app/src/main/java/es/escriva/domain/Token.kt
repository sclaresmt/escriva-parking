package es.escriva.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDateTime

@Entity
data class Token (

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val lastUpdatedDateTime: LocalDateTime

) : Serializable