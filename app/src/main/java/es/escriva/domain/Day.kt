package es.escriva.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDate

@Entity
data class Day (

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    var date: LocalDate,

    var vehiclesCount: Int = 0,

    var dayAmount: BigDecimal = BigDecimal.ZERO,

    var active: Boolean = true

) : Serializable