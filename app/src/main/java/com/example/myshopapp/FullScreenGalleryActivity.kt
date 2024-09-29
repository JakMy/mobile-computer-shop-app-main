package com.example.myshopapp

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.myshopapp.adapter.ImagePagerAdapter

class FullScreenGalleryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_gallery)

        val imageUrls = intent.getStringArrayListExtra("image_urls") ?: arrayListOf()
        val initialPosition = intent.getIntExtra("position", 0)

        val viewPager = findViewById<ViewPager2>(R.id.fullScreenViewPager)
        val adapter = ImagePagerAdapter(imageUrls) {}
        viewPager.adapter = adapter
        viewPager.setCurrentItem(initialPosition, false)

        val closeButton = findViewById<ImageView>(R.id.closeButton)
        closeButton.setOnClickListener {
            finish()
        }
    }
}
