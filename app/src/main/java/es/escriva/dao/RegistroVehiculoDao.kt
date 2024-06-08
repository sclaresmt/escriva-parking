import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import es.escriva.domain.RegistroVehiculoWithToken

@Dao
interface RegistroVehiculoDao {
    @Transaction
    @Query("SELECT * FROM VehicleRecord")
    fun getRegistroVehiculoWithToken(): List<RegistroVehiculoWithToken>

}