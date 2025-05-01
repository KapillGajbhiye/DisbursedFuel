package com.its.generatefuelrequest.network

import android.content.Context
import android.content.SharedPreferences
import com.its.generatefuelrequest.model.ResponseData

class AppPreferences(context: Context) {
    private val PREFERENCE_NAME: String = "com.its.intergenerational.model.FuelPump"
    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = preferences.edit()

    fun createSession(details: ResponseData) {
        editor.clear()
        editor.putBoolean("isLogin", true)
        editor.putInt("fuelPump_masterId", details.fuelPumpId)
        editor.putString("fuelPump_companyName", details.fuelPumpCompanyName)
        editor.putString("profileImageUrl", details.profileImageUrl)
        editor.putString("mobileNumber", details.mobileNumber)
        editor.putString("AuthToken", details.authToken)
        editor.apply()
    }

    fun getUserData(): ResponseData {
        val fuelPumpMasterId = preferences.getInt("fuelPump_masterId", -1)
        val fuelPumpCompanyName = preferences.getString("fuelPump_companyName", "") ?: ""
        val profileImageUrl = preferences.getString("profileImageUrl", "") ?: ""
        val mobileNumber = preferences.getString("mobileNumber", "") ?: ""
        val authToken = preferences.getString("AuthToken", "") ?: ""

        return ResponseData(
            fuelPumpMasterId,
            fuelPumpCompanyName,
            profileImageUrl,
            mobileNumber,
            authToken
        )
    }

    fun checkUserLogin(): Boolean {
        return preferences.getBoolean("isLogin", false)
    }

    fun clearPreferences() {
        if (checkUserLogin()) {
            editor.clear().apply()
            editor.putBoolean("isLogin", false)
            editor.apply()
        }
    }

}
