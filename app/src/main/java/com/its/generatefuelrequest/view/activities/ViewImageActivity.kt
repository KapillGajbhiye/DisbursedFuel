package com.its.generatefuelrequest.view.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.its.generatefuelrequest.R
import com.its.generatefuelrequest.globalClasses.SetToolBar
import com.its.generatefuelrequest.view.adapters.ImageViewAdapter

class ViewImageActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var imageViewAdapter: ImageViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        SetToolBar.setToolbar(this, toolbar, "Fuel Slip Images", R.drawable.home_button_custom)
        recyclerView = findViewById(R.id.viewImageRecyclerView)

        val imagesList = intent.getStringArrayListExtra("imagesList")

        if (!imagesList.isNullOrEmpty()) {
            try {
                imageViewAdapter = ImageViewAdapter(this, imagesList)
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = imageViewAdapter
            } catch (e: Exception) {
                Toast.makeText(this, "Error displaying images", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Toast.makeText(this, "No images to display", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
