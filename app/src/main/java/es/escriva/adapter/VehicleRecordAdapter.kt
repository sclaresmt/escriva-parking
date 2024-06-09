package es.escriva.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.escriva.R
import es.escriva.domain.VehicleRecord

class VehicleRecordAdapter(private val vehicleRecords: List<VehicleRecord>) :
    RecyclerView.Adapter<VehicleRecordAdapter.VehicleRecordViewHolder>() {

    class VehicleRecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Aquí puedes vincular tus vistas, por ejemplo:
        // val vehicleNameTextView: TextView = itemView.findViewById(R.id.vehicle_name_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleRecordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.vehicle_record_item, parent, false)
        return VehicleRecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: VehicleRecordViewHolder, position: Int) {
        val vehicleRecord = vehicleRecords[position]
        // Aquí puedes asignar los valores de vehicleRecord a tus vistas, por ejemplo:
        // holder.vehicleNameTextView.text = vehicleRecord.name
    }

    override fun getItemCount() = vehicleRecords.size
}