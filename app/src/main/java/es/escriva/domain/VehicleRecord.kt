package es.escriva.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Entity(foreignKeys = [ForeignKey(entity = Token::class, parentColumns = ["id"], childColumns = ["tokenId"]),
    ForeignKey(entity = Day::class, parentColumns = ["fecha"], childColumns = ["dia"])])
data class VehicleRecord(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val enterTime: LocalDateTime,

    var exitTime: LocalDateTime? = null,

    var amount: Double = 0.0,

    var active: Boolean = true,

    @ColumnInfo(index = true)
    val tokenId: Long,

    @ColumnInfo(index = true)
    val dayId: Long

)