package com.example.myshopapp

import Product
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myshopapp.adapter.CheckoutProductAdapter
import com.example.myshopapp.data.Order
import com.example.myshopapp.`object`.Cart
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CheckoutActivity : AppCompatActivity() {

    private lateinit var recipientNameEditText: EditText
    private lateinit var deliveryAddressEditText: EditText
    private lateinit var countryEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var postalCodeEditText: EditText
    private lateinit var paymentMethodSpinner: Spinner
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var totalPriceTextView: TextView
    private lateinit var confirmOrderButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var cartAdapter: CheckoutProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        recipientNameEditText = findViewById(R.id.recipientName)
        deliveryAddressEditText = findViewById(R.id.deliveryAddress)
        countryEditText = findViewById(R.id.country)
        cityEditText = findViewById(R.id.city)
        postalCodeEditText = findViewById(R.id.postalCode)
        paymentMethodSpinner = findViewById(R.id.paymentMethodSpinner)
        productsRecyclerView = findViewById(R.id.productsRecyclerView)
        totalPriceTextView = findViewById(R.id.totalPriceTextView)
        confirmOrderButton = findViewById(R.id.confirmOrderButton)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        setupRecyclerView()
        setupPaymentMethodSpinner()
        populateCartItems()

        confirmOrderButton.setOnClickListener {
            saveOrderToDatabase()
        }

        // Obsługa produktu, jeśli pochodzi z przycisku Buy Now
        val buyNowProduct = intent.getSerializableExtra("buyNowProduct") as? Product
        buyNowProduct?.let {
            Cart.addProduct(it, 1)
            populateCartItems()
        }
    }

    private fun setupRecyclerView() {
        productsRecyclerView.layoutManager = LinearLayoutManager(this)
        cartAdapter = CheckoutProductAdapter(Cart.getCartItems())
        productsRecyclerView.adapter = cartAdapter
    }

    private fun setupPaymentMethodSpinner() {
        val paymentMethods = arrayOf("Credit Card", "PayPal", "Bank Transfer")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, paymentMethods)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        paymentMethodSpinner.adapter = adapter
    }

    private fun populateCartItems() {
        val cartItems = Cart.getCartItems()
        cartAdapter.submitList(cartItems)
        val totalPrice = Cart.getTotalPrice()
        totalPriceTextView.text = String.format("Total Price: %.2f zł", totalPrice)
    }

    private fun saveOrderToDatabase() {
        val recipientName = recipientNameEditText.text.toString().trim()
        val deliveryAddress = deliveryAddressEditText.text.toString().trim()
        val country = countryEditText.text.toString().trim()
        val city = countryEditText.text.toString().trim()
        val postalCode = postalCodeEditText.text.toString().trim()
        val paymentMethod = paymentMethodSpinner.selectedItem?.toString() ?: ""

        if (recipientName.isEmpty() || deliveryAddress.isEmpty() || country.isEmpty() || city.isEmpty() || postalCode.isEmpty() || paymentMethod.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val order = Order(
            recipientName = recipientName,
            deliveryAddress = deliveryAddress,
            country = country,
            city = city,
            postalCode = postalCode,
            paymentMethod = paymentMethod,
            cartItems = Cart.getCartItems(),
            totalPrice = Cart.getTotalPrice()
        )

        val userId = auth.currentUser?.uid ?: ""
        database.child("orders").child(userId).push().setValue(order)
            .addOnSuccessListener {
                Cart.clearCart()
                val intent = Intent(this, OrderSummaryActivity::class.java).apply {
                    putExtra("order", order)
                }
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to place order. Please try again.", Toast.LENGTH_SHORT).show()
            }
    }
}
