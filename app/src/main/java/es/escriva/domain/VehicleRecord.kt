package es.escriva.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(foreignKeys = [ForeignKey(entity = Token::class, parentColumns = ["id"], childColumns = ["tokenId"]),
    ForeignKey(entity = Day::class, parentColumns = ["fecha"], childColumns = ["dia"])])
data class VehicleRecord (

    @PrimaryKey(autoGenerate = true)
    val id: Int,

    val enterDateTime: LocalDateTime,

    val exitDateTime: LocalDateTime,

    @ColumnInfo(index = true)
    val tokenId: Int,

    @ColumnInfo(index = true)
    val dia: LocalDate

)