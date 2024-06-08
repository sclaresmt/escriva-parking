package es.escriva.domain

import androidx.room.Embedded
import androidx.room.Relation

data class RegistroVehiculoWithToken(
    @Embedded val vehicleRecord: VehicleRecord,
    @Relation(
        parentColumn = "tokenId",
        entityColumn = "id"
    )
    val token: Token
)