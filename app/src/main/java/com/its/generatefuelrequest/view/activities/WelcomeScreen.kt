package com.its.generatefuelrequest.view.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.its.generatefuelrequest.R
import com.its.generatefuelrequest.databinding.ActivityWelcomeScreenBinding

class WelcomeScreen : AppCompatActivity() {

    private var progressBarStatus = 0
    private val handler = Handler()
    private lateinit var binding: ActivityWelcomeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = getColor(R.color.blue_logo)

        progress()

        val intent = Intent(this, LoginActivity::class.java)

        binding.anim.setAnimation(R.raw.animation)
        binding.anim.playAnimation()

        Handler().postDelayed({
            startActivity(intent)
            finish()
        }, 2000)
    }

    private fun progress() {
        Thread {
            while (progressBarStatus < 100) {
                progressBarStatus += 1
                handler.post {
                    binding.progressBar.progress = progressBarStatus
                }
                try {
                    Thread.sleep(30)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }
}
