package com.its.generatefuelrequest.model

import com.google.gson.annotations.SerializedName

data class FuelPumpNameRequest(
    @SerializedName("FuelPumpCompanyName")val fuelPumpCompanyName: String,
    @SerializedName("BTIsDisbursed")val bTIsDisbursed: Boolean
)

data class FuelSlipDataResponse(
    @SerializedName("Message") val message: String,
    @SerializedName("Success") val success: Boolean,
    @SerializedName("Data") val data: List<FuelSlip>
)

data class FuelSlip(

    @SerializedName("FuelSlipId") val fuelSlipId: Long,
    @SerializedName("FuelSlipNo") val fuelSlipNo: String,
    @SerializedName("RequestDate") val requestDate: String,
    @SerializedName("VehicleNo") val vehicleNo: String,
    @SerializedName("VehicleType") val vehicleType: String,
    @SerializedName("FuelPumpName") val fuelPumpName: String,
    @SerializedName("FuelType") val fuelType: String,
    @SerializedName("FuelQuantity") val fuelQuantity: String,
    @SerializedName("FuelRate") val fuelRate: Double,
    @SerializedName("RequestedBy") val requestedBy: String,
    @SerializedName("Completed") val completed: Boolean,
    @SerializedName("FuelMode") val fuelMode: String,
    @SerializedName("DriverName") val driverName: String,
    @SerializedName("DriverMobile") val driverNumber: Long,
    @SerializedName("RequestedCompany") val companyName: String
)

data class UpsertResponse(
    @SerializedName("Message") val message: String,
    @SerializedName("Success") val success: Boolean,
    @SerializedName("Data") val data: Any?
)







