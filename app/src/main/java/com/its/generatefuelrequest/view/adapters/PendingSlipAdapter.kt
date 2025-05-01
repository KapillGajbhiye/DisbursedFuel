package com.its.generatefuelrequest.view.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.its.generatefuelrequest.R
import com.its.generatefuelrequest.model.FuelSlip
import com.its.generatefuelrequest.view.activities.UpdateFuelSlip
import java.text.SimpleDateFormat
import java.util.*

class PendingSlipAdapter(
    private val context: Context,
    private var data: List<FuelSlip>
) : RecyclerView.Adapter<PendingSlipAdapter.PendingSlipViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingSlipViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.recycler_adapter_view, parent, false)
        return PendingSlipViewHolder(view)
    }

    override fun onBindViewHolder(holder: PendingSlipViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = data.size

    fun updateData(newData: List<FuelSlip>) {
        data = newData
        notifyDataSetChanged()
    }

    inner class PendingSlipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val vehicleNumber: TextView = itemView.findViewById(R.id.vehicleNumber)
        private val vehicleType: TextView = itemView.findViewById(R.id.vehicleType)
        private val fuelType: TextView = itemView.findViewById(R.id.fuelType)
        private val fuelQuantity: TextView = itemView.findViewById(R.id.fuelQuantity)
        private val requestBy: TextView = itemView.findViewById(R.id.requestedBy)
        private val driverName: TextView = itemView.findViewById(R.id.driverName)
        private val driverNumber: TextView = itemView.findViewById(R.id.driverMobile)
        private val dateTime: TextView = itemView.findViewById(R.id.dateTime)
        private val editSlip: ImageButton = itemView.findViewById(R.id.update_ic)
        private val companyName: TextView = itemView.findViewById(R.id.companyName)

        fun bind(item: FuelSlip) {
            vehicleNumber.text = item.vehicleNo
            vehicleType.text = item.vehicleType
            fuelType.text = item.fuelType
            fuelQuantity.text = item.fuelQuantity
            requestBy.text = item.requestedBy
            dateTime.text = formatDate(item.requestDate)
            driverName.text = item.driverName?: "N/A"
            driverNumber.text = item.driverNumber.toString()
            companyName.text = item.companyName ?: "N/A"

            editSlip.setOnClickListener {
                val intent = Intent(context, UpdateFuelSlip::class.java).apply {
                    putExtras(Bundle().apply {
                        putString("VehicleNo", item.vehicleNo)
                        putString("FuelSlipNo", item.fuelSlipNo)
                        putString("FuelQuantity", item.fuelQuantity)
                        putDouble("FuelRate", item.fuelRate ?: 0.0)
                        putLong("FuelSlipID", item.fuelSlipId ?: 0)
                        putString("FuelMode", item.fuelMode)
                        putString("companyName", item.companyName)
                    })
                }
                (context as Activity).startActivity(intent)
            }
        }

        private fun formatDate(inputDate: String?): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
            val outputFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH)

            return try {
                val date = inputDate?.let { inputFormat.parse(it) }
                date?.let { outputFormat.format(it) } ?: "Not Available"
            } catch (e: Exception) {
                e.printStackTrace()
                "Not Available"
            }
        }
    }
}
