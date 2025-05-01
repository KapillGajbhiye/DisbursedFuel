package com.its.generatefuelrequest.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.its.generatefuelrequest.R
import com.its.generatefuelrequest.globalClasses.SetToolBar
import com.its.generatefuelrequest.network.AppPreferences
import com.mikhaellopez.circularimageview.CircularImageView
import com.squareup.picasso.Picasso

class Profile : AppCompatActivity() {

    private lateinit var appPreferences: AppPreferences
    private lateinit var fuelPumpName: TextInputEditText
    private lateinit var fuelPumpId: TextInputEditText
    private lateinit var contactNumber: TextInputEditText
    private lateinit var logoutBtn: MaterialButton
    private lateinit var confirmBtn: MaterialButton
    private lateinit var profileImage: CircularImageView
    private lateinit var versionCode: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        init()
    }

    private fun init() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        appPreferences = AppPreferences(baseContext)
        fuelPumpName = findViewById(R.id.fuelPumpName)
        fuelPumpId = findViewById(R.id.fuelPumpId)
        contactNumber = findViewById(R.id.contactNumber)
        logoutBtn = findViewById(R.id.logOut)
        confirmBtn = findViewById(R.id.updateBtn)
        profileImage = findViewById(R.id.profileImage)
        versionCode = findViewById(R.id.versionCodeId)

        versionCode.text = "Version Code: 26.O"

        SetToolBar.setToolbar(this, toolbar, "Profile", R.drawable.home_button_custom)

        appPreferences.getUserData().let { userData ->
            fuelPumpName.setText(userData.fuelPumpCompanyName ?: "")
            fuelPumpId.setText(userData.fuelPumpId.toString() ?: "")
            contactNumber.setText(userData.mobileNumber ?: "")
        }

        val profileImageUrl = appPreferences.getUserData().profileImageUrl
        if (profileImageUrl.isNotEmpty()) {
            try {
                Picasso.get().load(profileImageUrl).into(profileImage)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        logoutBtn.setOnClickListener {

            android.app.AlertDialog.Builder(this@Profile)
                .setTitle("Logout Confirmation")
                .setMessage("Do you want to logout?")
                .setPositiveButton("Yes") { dialogInterface, i ->
                    appPreferences.clearPreferences()

                    val intent = Intent(baseContext, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()

                }
                .setNegativeButton("No") { dialogInterface, i ->

                }
                .setOnCancelListener {

                }
                .show()
        }
        confirmBtn.setOnClickListener {
            Toast.makeText(this, "In Progress", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}