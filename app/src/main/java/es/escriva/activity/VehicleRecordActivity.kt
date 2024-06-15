package es.escriva.activity

import android.annotation.SuppressLint
import es.escriva.R
import es.escriva.adapter.VehicleRecordAdapter
import es.escriva.domain.Day
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.escriva.config.Constants
import es.escriva.database.AppDatabase
import es.escriva.domain.VehicleRecord
import es.escriva.repository.DayAndVehiclesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

class VehicleRecordActivity : BaseNfcActivity() {

    private lateinit var vehicleRecordRecyclerView: RecyclerView

    private lateinit var dayAndVehiclesRepository: DayAndVehiclesRepository

    private lateinit var day: Day

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_record)

        vehicleRecordRecyclerView = findViewById(R.id.activity_vehicle_records)
        vehicleRecordRecyclerView.layoutManager = LinearLayoutManager(this)

        day = intent.getSerializableExtra("day") as Day

        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getDatabase(this@VehicleRecordActivity).also {
                dayAndVehiclesRepository =
                    DayAndVehiclesRepository(it.dayDao(), it.vehicleRecordDao())
                val vehicleRecords = dayAndVehiclesRepository.getVehicleRecordsForDay(day.id)
                vehicleRecordRecyclerView.adapter = VehicleRecordAdapter(vehicleRecords)
                updateDataAndRefreshView(vehicleRecords, day)
            }
        }

        val previousDayButton = findViewById<TextView>(R.id.previous_day_button)
        previousDayButton.setOnClickListener {
            loadPreviousDayData()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateDataAndRefreshView(newVehicleRecords: List<VehicleRecord>, newDay: Day) {

        // Actualizar los datos
        val adapter = vehicleRecordRecyclerView.adapter as? VehicleRecordAdapter
        if (adapter != null) {
            adapter.vehicleRecords = newVehicleRecords
            adapter.notifyDataSetChanged()
        }

        // Actualizar los TextView
        val totalAmount = newDay.dayAmount
        val totalAmountTextView = findViewById<TextView>(R.id.day_amount)
        totalAmountTextView.text = totalAmount.toString().replace(".", ",")

        val dayDate = newDay.date
        val dayDateTextView = findViewById<TextView>(R.id.day_date)
        dayDateTextView.text = String.format("%s: ", dayDate.format(Constants().dateFormatter))
    }

    private fun loadPreviousDayData() {
        CoroutineScope(Dispatchers.IO).launch {
            val previousRegisteredDay = dayAndVehiclesRepository.getPreviousRegisteredDay(day)
            if (previousRegisteredDay != null) {
                day = previousRegisteredDay
                val vehicleRecords = dayAndVehiclesRepository.getVehicleRecordsForDay(previousRegisteredDay.id)
                withContext(Dispatchers.Main) {
                    updateDataAndRefreshView(vehicleRecords, previousRegisteredDay)
                }
            }
        }
    }

}