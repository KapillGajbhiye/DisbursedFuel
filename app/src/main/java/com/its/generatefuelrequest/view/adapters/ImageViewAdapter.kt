package com.its.generatefuelrequest.view.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.its.generatefuelrequest.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.io.File

class ImageViewAdapter(private val context: Context, private val imageListData: List<String>) :
    RecyclerView.Adapter<ImageViewAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.view_images, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return imageListData.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        try {
            val item = imageListData[position]
            if (item.isNotEmpty()) {
                Picasso.get().load(File(item)).into(holder.imageView, object : Callback {
                    override fun onSuccess() {
                        Log.d("ImageViewAdapter", "Image loaded successfully")
                    }

                    override fun onError(e: Exception?) {
                        Log.e("ImageViewAdapter", "Error loading image", e)
                    }
                })
            } else {
                Log.e("ImageViewAdapter", "Image path is empty at position $position")
            }
        } catch (e: Exception) {
            Log.e("ImageViewAdapter", "Error loading image", e)
        }
    }
}
