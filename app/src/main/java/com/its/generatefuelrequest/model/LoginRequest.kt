package com.its.generatefuelrequest.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("FuelPumpCompanyName") val fuelPumpCompanyName: String,
    @SerializedName("Password") val password: String
)

data class LoginResponse(
    @SerializedName("Message") val message: String,
    @SerializedName("Success") val success: Boolean,
    @SerializedName("Data") val Data: ResponseData
)

data class ResponseData(
    @SerializedName("FuelPumpMasterId") val fuelPumpId: Int,
    @SerializedName("FuelPumpCompanyName") val fuelPumpCompanyName: String,
    @SerializedName("ProfileImage") val profileImageUrl: String,
    @SerializedName("MobileNumber") val mobileNumber: String,
    @SerializedName("AuthToken") val authToken: String,
)