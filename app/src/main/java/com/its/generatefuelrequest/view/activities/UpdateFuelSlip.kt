package com.its.generatefuelrequest.view.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.its.generatefuelrequest.R
import com.its.generatefuelrequest.globalClasses.SetToolBar
import com.its.generatefuelrequest.network.AppPreferences
import com.its.generatefuelrequest.network.RetrofitClient
import com.its.generatefuelrequest.view.dialogs.CustomProgressBar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.JsonElement
import com.its.generatefuelrequest.globalClasses.VersionCode
import com.its.generatefuelrequest.repositories.Repository
import com.its.generatefuelrequest.viewModel.MyViewModel
import com.its.generatefuelrequest.viewModel.ViewModelFactoryProvider
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class UpdateFuelSlip : AppCompatActivity() {

    private lateinit var vehicleNumber: TextInputEditText
    private lateinit var fuelSlipNo: TextInputEditText
    private lateinit var fuelRequest: TextInputEditText
    private lateinit var fuelDisbursed: TextInputEditText
    private lateinit var fuelRate: TextInputEditText
    private lateinit var amount: TextInputEditText

    private lateinit var captureImage: ImageView
    private lateinit var viewImageIcon: ImageView
    private lateinit var captureImageDriver: ImageView
    private lateinit var viewImageIconDriver: ImageView

    private lateinit var updateBtn: MaterialButton
    private lateinit var appPreferences: AppPreferences
    private lateinit var fuelMode: String
    private var firstImagePath: String = ""
    private var secondImagePath: String = ""
    private var thirdImagePath: String = ""
    private var mFuelSlipID: Long = 0
    private val CAMERA_PERMISSION_CODE = 101
    private val CAMERA_REQUEST_CODE = 102
    private val imagesList = mutableListOf<String>()
    private lateinit var customProgressBar: CustomProgressBar
    private var currentPhotoPath: String? = null
    private lateinit var toolbar: Toolbar
    private var requestedImageType: String? = null
    private lateinit var viewModel: MyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_fuel_slip)

        toolbar = findViewById(R.id.toolbar)
        vehicleNumber = findViewById(R.id.vehicleNo)
        fuelSlipNo = findViewById(R.id.fuelSlipNo)
        fuelRequest = findViewById(R.id.fuelQuantity)
        fuelDisbursed = findViewById(R.id.fuelDisbursed)
        fuelRate = findViewById(R.id.fuelRate)
        amount = findViewById(R.id.amount)

        captureImage = findViewById(R.id.ic_camera1)
        viewImageIcon = findViewById(R.id.viewImage)
        captureImageDriver = findViewById(R.id.captureDriver)
        viewImageIconDriver = findViewById(R.id.viewDriverImage)

        updateBtn = findViewById(R.id.confirm_button)
        appPreferences = AppPreferences(this)
        customProgressBar = CustomProgressBar(this)
        customProgressBar.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        customProgressBar.setCancelable(false)

        val apiService = RetrofitClient.createApiService()
        val repository = Repository(apiService)
        viewModel = ViewModelProvider(this, ViewModelFactoryProvider(repository))[MyViewModel::class.java]

        SetToolBar.setToolbar(
            this,
            toolbar,
            "Fuel Slip",
            R.drawable.home_button_custom
        )

        val mVehicleNo = intent.getStringExtra("VehicleNo")
        vehicleNumber.setText(mVehicleNo)
        val mFuelSlipNo = intent.getStringExtra("FuelSlipNo")
        fuelSlipNo.setText(mFuelSlipNo)
        val mFuelRequest = intent.getStringExtra("FuelQuantity")
        fuelRequest.setText(mFuelRequest)
        val mFuelRate = intent.getDoubleExtra("FuelRate", 0.0)
        Log.d("myData", "onCreate: $mFuelRate")
        val companyName = intent.getStringExtra("companyName")
        Log.d("companyName", "onCreate: $companyName")

        fuelRate.setText(mFuelRate.toString())
        fuelMode = intent.getStringExtra("FuelMode").toString()
        mFuelSlipID = intent.getLongExtra("FuelSlipID", 0)


        fuelDisbursed.addTextChangedListener {
            val enteredValue = it.toString()
            if (enteredValue.isNotEmpty()) {
                try {
                    if (fuelRequest.text.toString() == "Full Tank") {
                        // When fuelRequest is "Full Tank", allow user to enter any value for fuelDisbursed
                        val fuelDisbursedValue = enteredValue.toDouble()
                        val fuelRateValue = fuelRate.text.toString().toDouble()
                        val calculateAmount = fuelDisbursedValue * fuelRateValue
                        val decimalFormat = DecimalFormat("#.##")
                        val formattedAmount = decimalFormat.format(calculateAmount)
                        amount.setText(formattedAmount)
                    } else {

                        val fuelRequestValue = fuelRequest.text.toString().toDouble()
                        val fuelDisbursedValue = enteredValue.toDouble()

                        if (fuelDisbursedValue > fuelRequestValue) {
                            fuelDisbursed.error =
                                "Fuel Disbursed cannot be greater than Fuel Request"
                            amount.setText("")
                        } else {
                            val fuelRateValue = fuelRate.text.toString().toDouble()
                            val calculateAmount = fuelDisbursedValue * fuelRateValue
                            val decimalFormat = DecimalFormat("#.##")
                            val formattedAmount = decimalFormat.format(calculateAmount)
                            amount.setText(formattedAmount)
                        }
                    }
                } catch (e: NumberFormatException) {
                    fuelDisbursed.error = "Please enter a valid number"
                    amount.setText("")
                }
            } else {
                amount.setText("")
            }
        }

        captureImage.setOnClickListener {
            fuelDisbursed.clearFocus()

            when {
                firstImagePath.isEmpty() -> openCamera("slip") // Capture Slip Image First
                secondImagePath.isEmpty() -> openCamera("meter") // Capture Meter Image Second
                else -> Toast.makeText(
                    this,
                    "Fuel Slip and Meter images already captured",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        captureImageDriver.setOnClickListener {
            if (firstImagePath.isEmpty()) {
                Toast.makeText(this, "Capture Fuel Slip Image First", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (secondImagePath.isEmpty()) {
                Toast.makeText(this, "Capture Fuel Meter Image First", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (thirdImagePath.isEmpty()) {
                openCamera("driver") // Capture Driver Image Last
            } else {
                Toast.makeText(this, "Driver Image Already Captured", Toast.LENGTH_SHORT).show()
            }
        }

        viewImageIcon.setOnClickListener {
            if (imagesList.isNotEmpty()) {
                val intent = Intent(this, ViewImageActivity::class.java)
                intent.putStringArrayListExtra("imagesList", ArrayList(imagesList))
                startActivity(intent)
            } else {
                Toast.makeText(this, "No images to view", Toast.LENGTH_SHORT).show()
            }
        }

        viewImageIconDriver.setOnClickListener {
            if (imagesList.isNotEmpty()) {
                val intent = Intent(this, ViewImageActivity::class.java)
                intent.putStringArrayListExtra("imagesList", ArrayList(imagesList))
                startActivity(intent)
            } else {
                Toast.makeText(this, "No images to view", Toast.LENGTH_SHORT).show()
            }
        }

        updateBtn.setOnClickListener {
            val requestedQuantityText = fuelRequest.text.toString()
            val disbursedQuantity = fuelDisbursed.text.toString().toDoubleOrNull()

            if (fuelDisbursed.text?.isEmpty() == true) {
                fuelDisbursed.error = "Please Enter Fuel Disburse"
                return@setOnClickListener
            }

            if (firstImagePath.isBlank()) {
                Toast.makeText(baseContext, "Capture Fuel Slip Image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (secondImagePath.isEmpty()) {
                Toast.makeText(baseContext, "Capture Fuel Meter Image", Toast.LENGTH_SHORT).show()
            }

            if (thirdImagePath.isBlank()) {
                Toast.makeText(baseContext, "Capture Driver Image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (requestedQuantityText == "Full Tank") {
                if (validation()) {
                    if (companyName != null) {
                        saveDetails(companyName)
                    } else {
                        Toast.makeText(
                            baseContext,
                            "Company Name Not Found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        baseContext,
                        "Please enter valid Disbursed Quantity",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                val requestedQuantity = requestedQuantityText.toDoubleOrNull()
                if (disbursedQuantity != null && requestedQuantity != null) {
                    if (disbursedQuantity > requestedQuantity) {
                        Toast.makeText(
                            baseContext,
                            "Disbursed quantity cannot be greater than requested quantity",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }
                }

                if (validation()) {
                    if (companyName != null) {
                        saveDetails(companyName)
                    }
                } else {
                    Toast.makeText(
                        baseContext,
                        "Please enter valid Disbursed Quantity",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        setupObserver()

    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission(imageType: String) {
        requestedImageType = imageType
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_CODE
        )
        Log.d("CameraPermission", "Camera permission requested")
    }

    private fun openCamera(imageType: String) {
        if (!hasCameraPermission()) {
            requestCameraPermission(imageType) // Request permission before opening camera
            return
        }

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val imageFile = createImageFile()
        val photoURI: Uri = FileProvider.getUriForFile(
            this,
            "com.its.intergenerational.file-provider",
            imageFile
        )

        currentPhotoPath = imageFile.absolutePath // Assign after file creation

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        cameraIntent.putExtra("imageType", imageType)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("CameraPermission", "Camera permission granted")
                requestedImageType?.let { openCamera(it) } // Open camera with stored imageType
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val capturedPath = currentPhotoPath ?: return // Create a local copy

            when {
                firstImagePath.isEmpty() -> {
                    firstImagePath = capturedPath
                    imagesList.add(firstImagePath)
                    viewImageIcon.visibility = View.VISIBLE
                    Log.d("ImagePath", "Fuel Slip Image Path: $firstImagePath")
                }

                secondImagePath.isEmpty() -> {
                    secondImagePath = capturedPath
                    imagesList.add(secondImagePath)
                    viewImageIcon.visibility = View.VISIBLE
                    Log.d("ImagePath", "Fuel Meter Image Path: $secondImagePath")
                }

                thirdImagePath.isEmpty() -> {
                    thirdImagePath = capturedPath
                    imagesList.add(thirdImagePath)
                    viewImageIconDriver.visibility = View.VISIBLE
                    Log.d("ImagePath", "Driver Image Path: $thirdImagePath")
                }
            }
        } else {
            Toast.makeText(this, "Image capture failed", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    private fun saveDetails(companyName: String) {
        Log.d("saveDetails", "saveDetails called")

        try {
            val imageFiles: MutableList<MultipartBody.Part> = mutableListOf()

            if (firstImagePath.isNotEmpty()) {
                //val firstImageFile = File(firstImagePath)
                val firstImageFile = File(firstImagePath)
                val firstImageRequestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), firstImageFile)
                imageFiles.add(
                    MultipartBody.Part.createFormData(
                        "imageFuelSlipName",
                        firstImageFile.name,
                        firstImageRequestFile
                    )
                )
            }

            if (secondImagePath.isNotEmpty()) {
                //val secondImageFile = File(secondImagePath)
                val secondImageFile = File(secondImagePath)
                val secondImageRequestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), secondImageFile)
                imageFiles.add(
                    MultipartBody.Part.createFormData(
                        "imageFuelMeterName",
                        secondImageFile.name,
                        secondImageRequestFile
                    )
                )
            }

            if (thirdImagePath.isNotEmpty()) {
                //val thirdImageFile = File(thirdImagePath)
                val thirdImageFile = File(thirdImagePath)
                val thirdImageRequestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), thirdImageFile)
                imageFiles.add(
                    MultipartBody.Part.createFormData(
                        "FuelVehicleDrvImage",
                        thirdImageFile.name,
                        thirdImageRequestFile
                    )
                )
            }

            val jsonObject = JSONObject().apply {
                put("FuelPumpID", appPreferences.getUserData().fuelPumpId)
                put("FuelSlipID", mFuelSlipID)
                put("DisburseFuelQTY", fuelDisbursed.text.toString())
                put("DispurseFuelRate", fuelRate.text.toString())
                put("CompanyName", companyName)

                if (firstImagePath.isNotEmpty()) put("FuelSlipName", File(firstImagePath).name)
                if (secondImagePath.isNotEmpty()) put("FuelMeterName", File(secondImagePath).name)
                if (thirdImagePath.isNotEmpty()) put(
                    "VehicleDriverImage",
                    File(thirdImagePath).name
                )
                put("FuelMode", fuelMode)
            }

            // ✅ Now call ViewModel
            viewModel.upsertFuelSlip(
                authToken = appPreferences.getUserData().authToken ?: "",
                RequestBody.create(MediaType.parse("multipart/form-data"), jsonObject.toString()),
                imagesList = imageFiles,
                customProgressBar = customProgressBar
            )

        } catch (e: Exception) {
            Log.e("saveDetails", "Exception: ${e.message}", e)
            customProgressBar.dismiss()
            Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupObserver() {
        viewModel.upsertFuelSlipDataResponse.observe(this) { response ->
            if (response != null && response.success) {
                Toast.makeText(this, "Fuel Details Updated Successfully!", Toast.LENGTH_SHORT).show()
                val intent = Intent(applicationContext, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Failed to update fuel details.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun validation(): Boolean {
        if (fuelDisbursed.length() == 0) {
            fuelDisbursed.error = "Please enter Quantity of Fuel Disbursed"
            return false
        }

        if (fuelRate.length() == 0) {
            fuelRate.error = "Invalid Fuel Rate"
            return false
        }

        if (amount.length() == 0) {
            amount.error = "Fuel Disbursed Quantity cannot be greater than request"
            return false
        }
        return true
    }

}


