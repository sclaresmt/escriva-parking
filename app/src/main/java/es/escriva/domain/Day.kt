package es.escriva.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class Day (

    @PrimaryKey
    var date: LocalDate,

    var vehiclesCount: Int

)