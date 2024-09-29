package com.example.myshopapp

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AppInformationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_app_information)

        val exitButton = findViewById<ImageButton>(R.id.exitButton)

        exitButton.setOnClickListener{
            finish()
        }

        val rootLayout = findViewById<LinearLayout>(R.id.root_layout)
        val animDrawable = rootLayout.background as AnimationDrawable

        animDrawable.setEnterFadeDuration(10)
        animDrawable.setExitFadeDuration(5000)
        animDrawable.start()
    }
}