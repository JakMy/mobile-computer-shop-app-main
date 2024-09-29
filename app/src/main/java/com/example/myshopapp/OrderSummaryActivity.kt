package com.example.myshopapp

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.myshopapp.data.Order

class OrderSummaryActivity : AppCompatActivity() {

    private lateinit var orderSummaryTextView: TextView
    private lateinit var totalPriceTextView: TextView
    private lateinit var backToHomeButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_summary)

        val rootLayout = findViewById<LinearLayout>(R.id.root_layout3)
        val animDrawable = rootLayout.background as AnimationDrawable

        animDrawable.setEnterFadeDuration(10)
        animDrawable.setExitFadeDuration(5000)
        animDrawable.start()

        orderSummaryTextView = findViewById(R.id.orderSummaryTextView)
        totalPriceTextView = findViewById(R.id.totalPriceTextView)
        backToHomeButton = findViewById(R.id.backToHomeButton)

        val order = intent.getParcelableExtra<Order>("order")

        if (order != null) {
            orderSummaryTextView.text = "Your order has been placed successfully. You can view the details of your order in the order history."
            totalPriceTextView.text = String.format("Total Price: %.2f zł", order.totalPrice)
        }

        backToHomeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
