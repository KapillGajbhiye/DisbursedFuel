package com.its.generatefuelrequest.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.its.generatefuelrequest.R
import com.its.generatefuelrequest.model.LoginRequest
import com.its.generatefuelrequest.model.ResponseData
import com.its.generatefuelrequest.network.AppPreferences
import com.its.generatefuelrequest.network.RetrofitClient
import com.its.generatefuelrequest.repositories.Repository
import com.its.generatefuelrequest.view.dialogs.CustomProgressBar
import com.its.generatefuelrequest.viewModel.MyViewModel
import com.its.generatefuelrequest.viewModel.ViewModelFactoryProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.its.generatefuelrequest.model.LoginResponse

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: MaterialButton

    private lateinit var loginViewModel: MyViewModel
    private lateinit var appPreferences: AppPreferences
    private lateinit var customProgressBar: CustomProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        setupViewModel()
        checkUserLogin()

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            Log.d("data", "onCreate: $email,$password")
            if (validateInputs(email, password)) {
                userLogin(email, password)
            } else {
                Toast.makeText(this, "Please enter valid credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initViews() {
        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.loginButton)
        appPreferences = AppPreferences(this)

        customProgressBar = CustomProgressBar(this).apply {
            window!!.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(false)
        }
    }

    private fun setupViewModel() {
        val apiService = RetrofitClient.createApiService()
        val loginData = Repository(apiService)
        loginViewModel = ViewModelProvider(this, ViewModelFactoryProvider(loginData))[MyViewModel::class.java]
    }

    private fun checkUserLogin() {
        if (appPreferences.checkUserLogin()) {
            navigateToHomeScreen()
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        return email.isNotEmpty() && password.isNotEmpty()
    }

    private fun userLogin(userName: String, password: String) {
        val loginRequest = LoginRequest(userName, password)
        Log.d("data", "userLogin: $loginRequest")
        loginViewModel.loginResponse(loginRequest, customProgressBar)
        observeLoginResponse()
    }

    private fun observeLoginResponse() {
        loginViewModel.loginResponse.observe(this) { loginResponse ->
            loginResponse?.let {
                handleLoginResponse(it)
            } ?: run {
                showErrorToast("An error occurred during login")
            }
        }
    }

    private fun handleLoginResponse(loginResponse: LoginResponse) {
        if (loginResponse.success) {
            Toast.makeText(this, loginResponse.message, Toast.LENGTH_SHORT).show()
            Log.d("login", "handleLoginResponse: ${loginResponse.message}")
            saveUserData(loginResponse.Data)
            navigateToHomeScreen()
        } else {
            showErrorToast(loginResponse.message)
        }
    }

    private fun saveUserData(data: ResponseData) {
        val responseData = ResponseData(
            fuelPumpId = data.fuelPumpId ?: 0,
            fuelPumpCompanyName = data.fuelPumpCompanyName ?: "",
            profileImageUrl = data.profileImageUrl ?: "",
            mobileNumber = data.mobileNumber ?: "",
            authToken = data.authToken ?: ""
        )
        appPreferences.createSession(responseData)
    }

    private fun navigateToHomeScreen() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showErrorToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}
