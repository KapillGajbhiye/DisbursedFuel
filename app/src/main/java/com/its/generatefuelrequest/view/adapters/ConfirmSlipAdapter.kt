package com.its.generatefuelrequest.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.its.generatefuelrequest.R
import com.its.generatefuelrequest.model.FuelSlip
import java.text.SimpleDateFormat
import java.util.*

class ConfirmSlipAdapter(private val context: Context, private var data: List<FuelSlip>) :
    RecyclerView.Adapter<ConfirmSlipAdapter.ConfirmSlipViewHolder>() {

    fun updateData(newList: List<FuelSlip>) {
        data = newList
        notifyDataSetChanged()
    }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConfirmSlipViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.confirm_recycler_adapter_view, parent, false)
        return ConfirmSlipViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ConfirmSlipViewHolder, position: Int) {
        val item = data[position]

        holder.vehicleNumber.text = item.vehicleNo ?: "Not Available"
        holder.vehicleType.text = item.vehicleType ?: "Not Available"
        holder.fuelType.text = item.fuelType ?: "Not Available"
        holder.fuelQuantity.text = item.fuelQuantity ?: "Not Available"
        holder.requestedBy.text = item.requestedBy ?: "Not Available"
        holder.dateTime.text = formatDate(item.requestDate) ?: "Not Available"
        holder.driverName.text = item.driverName ?: "N/A"
        holder.driverNumber.text = item.driverNumber.toString()
        holder.companyName.text = item.companyName ?: "N/A"
    }

    class ConfirmSlipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val vehicleNumber: TextView = itemView.findViewById(R.id.vehicleNumber)
        val vehicleType: TextView = itemView.findViewById(R.id.vehicleType)
        val fuelType: TextView = itemView.findViewById(R.id.fuelType)
        val fuelQuantity: TextView = itemView.findViewById(R.id.fuelQuantity)
        val requestedBy: TextView = itemView.findViewById(R.id.requestedBy)
        val dateTime: TextView = itemView.findViewById(R.id.dateTime)
        val driverName: TextView = itemView.findViewById(R.id.driverName)
        val driverNumber: TextView = itemView.findViewById(R.id.driverNumber)
        val companyName: TextView = itemView.findViewById(R.id.companyName)
    }

    private fun formatDate(inputDate: String?): String? {
        if (inputDate.isNullOrBlank()) return "Not Available"

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH)

        return try {
            val date = inputFormat.parse(inputDate)
            date?.let { outputFormat.format(it) } ?: "Not Available"
        } catch (e: Exception) {
            e.printStackTrace()
            "Not Available"
        }
    }
}
