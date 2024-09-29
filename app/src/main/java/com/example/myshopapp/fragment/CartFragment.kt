package com.example.myshopapp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myshopapp.CheckoutActivity
import com.example.myshopapp.R
import com.example.myshopapp.adapter.CartAdapter
import com.example.myshopapp.`object`.Cart
import java.text.DecimalFormat

class CartFragment : Fragment() {

    private lateinit var cartAdapter: CartAdapter
    private lateinit var checkoutButton: Button
    private lateinit var totalPriceTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        // Inicjalizacja widoków
        totalPriceTextView = view.findViewById(R.id.totalPrice)
        checkoutButton = view.findViewById(R.id.checkoutButton)

        // Ustawienie RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewCart)
        recyclerView.layoutManager = LinearLayoutManager(context)
        cartAdapter = CartAdapter { updateTotalPrice() }
        recyclerView.adapter = cartAdapter

        // Wyświetlenie ceny od razu po załadowaniu widoku
        updateTotalPrice()

        // Ustawienie przycisku Checkout
        checkoutButton.setOnClickListener {
            if (Cart.getCartItems().isEmpty()) {
                Toast.makeText(activity, "The cart cannot be empty to complete the transaction", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(activity, CheckoutActivity::class.java)
                startActivity(intent)
            }
        }

        // Przekazanie listy produktów do adaptera
        cartAdapter.submitList(Cart.getCartItems())

        return view
    }

    private fun updateTotalPrice() {
        val totalPrice = Cart.getTotalPrice()
        val formattedPrice = DecimalFormat("#0.00").format(totalPrice)
        totalPriceTextView.text = "Total Price: $formattedPrice zł"
    }
}
