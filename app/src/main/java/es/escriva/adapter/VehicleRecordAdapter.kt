package es.escriva.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.escriva.R
import es.escriva.domain.VehicleRecord

class VehicleRecordAdapter(var vehicleRecords: List<VehicleRecord>) :
    RecyclerView.Adapter<VehicleRecordAdapter.VehicleRecordViewHolder>() {

    class VehicleRecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val enterTimeTextView: TextView = itemView.findViewById(R.id.enterTimeTextView)
        val exitTimeTextView: TextView = itemView.findViewById(R.id.exitTimeTextView)
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleRecordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.vehicle_record_item, parent, false)
        return VehicleRecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: VehicleRecordViewHolder, position: Int) {
        val vehicleRecord = vehicleRecords[position]
        holder.enterTimeTextView.text = vehicleRecord.enterTime.toString()
        holder.exitTimeTextView.text = vehicleRecord.exitTime?.toString() ?: ""
        holder.amountTextView.text = if (vehicleRecord.amount == 0.0) "" else vehicleRecord.amount.toString()
    }

    override fun getItemCount() = vehicleRecords.size
}