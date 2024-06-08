package es.escriva.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class Token (

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val dateTimeCreation: LocalDateTime

)