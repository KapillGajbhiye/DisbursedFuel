package com.its.generatefuelrequest.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import com.its.generatefuelrequest.model.FuelPumpNameRequest
import com.its.generatefuelrequest.model.FuelSlipDataResponse
import com.its.generatefuelrequest.model.LoginRequest
import com.its.generatefuelrequest.model.LoginResponse
import com.its.generatefuelrequest.model.UpsertResponse
import com.its.generatefuelrequest.repositories.Repository
import com.its.generatefuelrequest.view.dialogs.CustomProgressBar
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class MyViewModel(private val repository: Repository) : ViewModel() {

    private val _loginResponse = MutableLiveData<LoginResponse?>()
    val loginResponse: LiveData<LoginResponse?> get() = _loginResponse

    fun loginResponse(loginRequest: LoginRequest,customProgressBar: CustomProgressBar) {
        viewModelScope.launch {
            try {
                customProgressBar.show()
                val response = repository.fetchUserLogin(loginRequest)
                if(response.success){
                    customProgressBar.dismiss()
                }
                _loginResponse.postValue(response)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("error", "login: $e")
            }finally {
                customProgressBar.dismiss()
            }
        }
    }

    private val _fuelSlipDataResponse = MutableLiveData<FuelSlipDataResponse?>()
    val fuelSlipDataResponse: LiveData<FuelSlipDataResponse?> get() = _fuelSlipDataResponse

    fun fetchFuelSlipData(fuelPump: FuelPumpNameRequest, authToken: String,customProgressBar: CustomProgressBar) {
        viewModelScope.launch {
            try {
                customProgressBar.show()
                val response = repository.fetchFuelSlipData(fuelPump, authToken)
                Log.d("slipData", "fetchFuelSlipData: $response")
                if(response.success){
                    customProgressBar.dismiss()
                }
                _fuelSlipDataResponse.postValue(response)

            } catch (e: Exception) {
                Log.d("error", "fetchFuelSlipData: ${e.message}")
                e.printStackTrace()
            }finally {
                customProgressBar.dismiss()
            }
        }
    }

    private val _upsertFuelSlipData = MutableLiveData<UpsertResponse>()
    val upsertFuelSlipDataResponse: LiveData<UpsertResponse> = _upsertFuelSlipData

    fun upsertFuelSlip(
        authToken: String,
        requestBody: RequestBody,
        imagesList: List<MultipartBody.Part>,
        customProgressBar: CustomProgressBar
    ) {
        viewModelScope.launch {
            customProgressBar.show()
            try {
                val response = repository.upsertFuelSlipData(authToken, requestBody, imagesList)
                Log.d("upsertFuelSlip", "response: $response")
                _upsertFuelSlipData.postValue(response)
            } catch (e: Exception) {
                Log.e("upsertFuelSlip", "Exception: ${e.message}", e)
            } finally {
                customProgressBar.dismiss()
            }
        }
    }

}