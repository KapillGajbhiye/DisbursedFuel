package com.its.generatefuelrequest.repositories

import com.google.gson.JsonElement
import com.its.generatefuelrequest.globalClasses.VersionCode
import com.its.generatefuelrequest.model.FuelPumpNameRequest
import com.its.generatefuelrequest.model.FuelSlipDataResponse
import com.its.generatefuelrequest.model.LoginRequest
import com.its.generatefuelrequest.model.LoginResponse
import com.its.generatefuelrequest.model.UpsertResponse
import com.its.generatefuelrequest.network.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class Repository(private val apiService: ApiService) {

    suspend fun fetchUserLogin(
        loginRequest: LoginRequest
    ): LoginResponse {
        return apiService.login(loginRequest, VersionCode.VERSION_CODE)
    }

    suspend fun fetchFuelSlipData(
        request: FuelPumpNameRequest,
        autToken: String,
    ): FuelSlipDataResponse {
        return apiService.fetchSlipData(request,autToken,VersionCode.VERSION_CODE)
    }

    suspend fun upsertFuelSlipData(
        authToken: String,
        requestBody: RequestBody,
        images: List<MultipartBody.Part>
    ): UpsertResponse {
        return apiService.updateFuelDetails1(authToken, VersionCode.VERSION_CODE, requestBody, images)
    }

}