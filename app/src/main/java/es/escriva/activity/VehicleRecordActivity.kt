package es.escriva.activity

import es.escriva.R
import es.escriva.adapter.VehicleRecordAdapter
import es.escriva.database.AppDatabase

package es.escriva

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.escriva.repository.DayAndVehiclesRepository
import java.time.LocalDateTime

class VehicleRecordActivity : AppCompatActivity() {

    private lateinit var vehicleRecordRecyclerView: RecyclerView
    private lateinit var dayAndVehiclesRepository: DayAndVehiclesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_record)

        vehicleRecordRecyclerView = findViewById(R.id.vehicle_record_recycler_view)
        vehicleRecordRecyclerView.layoutManager = LinearLayoutManager(this)

        val day = intent.getSerializableExtra("day") as LocalDateTime

        AppDatabase.getDatabase(this).also {
            dayAndVehiclesRepository = DayAndVehiclesRepository(it.dayDao(), it.vehicleRecordDao())
        }

        val vehicleRecords = dayAndVehiclesRepository.getVehicleRecordsForDay(day)
        vehicleRecordRecyclerView.adapter = VehicleRecordAdapter(vehicleRecords)
    }
}