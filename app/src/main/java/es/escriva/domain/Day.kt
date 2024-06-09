package es.escriva.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class Day (

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    var date: LocalDate,

    var vehiclesCount: Int = 0,

    var dayAmount: Double = 0.0,

    var active: Boolean = true

)