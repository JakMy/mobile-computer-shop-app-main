package com.example.myshopapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myshopapp.R

class ImagePagerAdapter(
    private val imageUrls: List<String>,
    private val onImageClick: (Int) -> Unit
) : RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageUrls[position], position)
    }

    override fun getItemCount(): Int = imageUrls.size

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = view.findViewById(R.id.imageView)

        fun bind(url: String, position: Int) {
            Glide.with(itemView.context)
                .load(url)
                .fitCenter()
                .into(imageView)

            imageView.setOnClickListener {
                onImageClick(position)
            }
        }
    }
}
