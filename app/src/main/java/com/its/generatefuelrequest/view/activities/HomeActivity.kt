package com.its.generatefuelrequest.view.activities

import android.content.Intent
import android.content.IntentSender
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.its.generatefuelrequest.R
import com.its.generatefuelrequest.network.AppPreferences
import com.its.generatefuelrequest.view.fragments.ConfirmFuelSlip
import com.its.generatefuelrequest.view.fragments.PendingFuelSlip
import com.google.android.material.tabs.TabLayout
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.its.generatefuelrequest.globalClasses.SetToolBar
import com.its.generatefuelrequest.`interface`.TabTitleUpdater
import com.mikhaellopez.circularimageview.CircularImageView
import com.squareup.picasso.Picasso

class HomeActivity : AppCompatActivity(), TabTitleUpdater {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var profileImage: CircularImageView
    private lateinit var appPreferences: AppPreferences
    private lateinit var toolbar: Toolbar
    private lateinit var rootView: ConstraintLayout

    private val UPDATE_CODE = 100
    private var appUpdateManager: AppUpdateManager? = null

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPDATE_CODE) {
            Log.d(
                "Update Result",
                "onActivityResult: Update flow completed with result code: $resultCode"
            )
            if (resultCode != RESULT_OK) {
                Log.e(
                    "Update Error",
                    "onActivityResult: Update failed with result code: $resultCode"
                )
                // Handle update failure
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
                finishAffinity()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        window.statusBarColor = ContextCompat.getColor(this, R.color.blue_logo)
        rootView = findViewById(R.id.rootLayout)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(
                systemBarInsets.left,
                systemBarInsets.top,
                systemBarInsets.right,
                systemBarInsets.bottom
            )

            WindowInsetsCompat.CONSUMED

        }

        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        profileImage = findViewById(R.id.profile)
        appPreferences = AppPreferences(this)
        appUpdateManager = AppUpdateManagerFactory.create(this)
        toolbar = findViewById(R.id.toolbar)

        SetToolBar.setToolbar(
            this,
            toolbar,
            "FUEL PUMP",
            R.drawable.home_button_custom1
        )

        checkForAppUpdate()

        try {
            val adapter = TabPagerAdapter(supportFragmentManager, this)
            viewPager.adapter = adapter
            tabLayout.setupWithViewPager(viewPager)
        } catch (e: Exception) {
            Log.e("ViewPager Error", "Error setting up ViewPager", e)
        }

        appPreferences.getUserData().profileImageUrl.let { profileImageUrl ->
            if (profileImageUrl.isNotEmpty()) {
                try {
                    Picasso.get().load(profileImageUrl).into(profileImage)
                } catch (e: Exception) {
                    Log.e("Picasso Error", "Error loading profile image", e)
                }
            }
        }

        profileImage.setOnClickListener {
            startActivity(Intent(this, Profile::class.java))
        }

        // Handle back press to finish the activity
        onBackPressedDispatcher.addCallback(this) {
            finish()
        }

    }

    private fun checkForAppUpdate() {
        appUpdateManager?.appUpdateInfo?.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                startAppUpdate(appUpdateInfo)
            }
        }?.addOnFailureListener { e ->
            Log.e("AppUpdate", "Failed to retrieve app update info: ${e.message}")
        }
    }

    private fun startAppUpdate(appUpdateInfo: AppUpdateInfo) {
        try {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager?.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    UPDATE_CODE
                )
            }
        } catch (e: IntentSender.SendIntentException) {
            Log.e("AppUpdate", "Failed to start app update: ${e.message}")
        }
    }

    override fun updateTabTitle(position: Int, size: Int) {
        val title = when (position) {
            0 -> "Pending ($size)"
            1 -> "Confirm ($size)"
            else -> "Tab"
        }
        tabLayout.getTabAt(position)?.text = title
    }

}

class TabPagerAdapter(fm: FragmentManager, private val tabTitleUpdater: TabTitleUpdater) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragments = listOf(
        PendingFuelSlip.newInstance(0, tabTitleUpdater),
        ConfirmFuelSlip.newInstance(1, tabTitleUpdater)
    )

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Pending"
            1 -> "Confirm"
            else -> "Tab"
        }
    }
}

