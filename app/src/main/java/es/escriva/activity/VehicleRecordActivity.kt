package es.escriva.activity

import es.escriva.R
import es.escriva.adapter.VehicleRecordAdapter
import es.escriva.domain.Day
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.escriva.config.Constants
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

        vehicleRecordRecyclerView = findViewById(R.id.activity_vehicle_records)
        vehicleRecordRecyclerView.layoutManager = LinearLayoutManager(this)

        val day = intent.getSerializableExtra("day") as Day

        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getDatabase(this@VehicleRecordActivity).also {
                dayAndVehiclesRepository = DayAndVehiclesRepository(it.dayDao(), it.vehicleRecordDao())
                val vehicleRecords = dayAndVehiclesRepository.getVehicleRecordsForDay(day.id)
                vehicleRecordRecyclerView.adapter = VehicleRecordAdapter(vehicleRecords)

                val totalAmount = day.dayAmount
                val totalAmountTextView = findViewById<TextView>(R.id.day_amount)
                totalAmountTextView.text = totalAmount.toString()

                val dayDate = day.date
                val dayDateTextView = findViewById<TextView>(R.id.day_date)
                dayDateTextView.text = String.format("%s: ", dayDate.format(Constants().dateFormatter))
            }
        }

    }
}