package com.its.generatefuelrequest.network

import com.google.gson.JsonElement
import com.its.generatefuelrequest.model.FuelPumpNameRequest
import com.its.generatefuelrequest.model.FuelSlipDataResponse
import com.its.generatefuelrequest.model.LoginRequest
import com.its.generatefuelrequest.model.LoginResponse
import com.its.generatefuelrequest.model.UpsertResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("/api/login-fuel-pump-master")
    suspend fun login(
        @Body loginRequest: LoginRequest,
        @Header("VersionCode") versionCode: Int
    ): LoginResponse

    @POST("/api/get-fuel-slips-data-new")
    suspend fun fetchSlipData(
        @Body fuelPump: FuelPumpNameRequest,
        @Header("AuthToken") authToken: String,
        @Header("VersionCode") versionCode: Int
    ): FuelSlipDataResponse


    @Multipart
    @POST("/api/update-mobile-fuel-slip-details")
    fun updateFuelDetails(
        @Header("AuthToken") authToken: String?,
        @Header("VersionCode") versionCode: Int,
        @Part("FormData") formData: RequestBody?,
        @Part files: List<MultipartBody.Part?>?
    ): Call<JsonElement>


//    @Multipart
//    @POST("/api/update-mobile-fuel-slip-details")
//    fun updateFuelDetails1(
//        @Header("AuthToken") authToken: String?,
//        @Header("VersionCode") versionCode: Int,
//        @Part("FormData") formData: RequestBody?,
//        @Part files: List<MultipartBody.Part?>?
//    ): UpsertResponse

    @Multipart
    @POST("/api/update-mobile-fuel-slip-details")
    suspend fun updateFuelDetails1(
        @Header("AuthToken") authToken: String,
        @Header("VersionCode") versionCode: Int,
        @Part("FormData") formData: RequestBody,
        @Part files: List<MultipartBody.Part>
    ): UpsertResponse


}
