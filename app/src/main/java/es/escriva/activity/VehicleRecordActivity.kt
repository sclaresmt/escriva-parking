package es.escriva.activity

import es.escriva.R
import es.escriva.adapter.VehicleRecordAdapter
import es.escriva.domain.Day
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.escriva.database.AppDatabase
import es.escriva.repository.DayAndVehiclesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VehicleRecordActivity : AppCompatActivity() {

    private lateinit var vehicleRecordRecyclerView: RecyclerView

    private lateinit var dayAndVehiclesRepository: DayAndVehiclesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_record)

        vehicleRecordRecyclerView = findViewById(R.id.vehicle_record_recycler_view)
        vehicleRecordRecyclerView.layoutManager = LinearLayoutManager(this)

        val day = intent.getSerializableExtra("day") as Day

        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getDatabase(this@VehicleRecordActivity).also {
                dayAndVehiclesRepository = DayAndVehiclesRepository(it.dayDao(), it.vehicleRecordDao())
            }
        }

        val vehicleRecords = dayAndVehiclesRepository.getVehicleRecordsForDay(day.id)
        vehicleRecordRecyclerView.adapter = VehicleRecordAdapter(vehicleRecords)
    }
}