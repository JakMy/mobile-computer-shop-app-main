package com.example.myshopapp

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myshopapp.adapter.OrdersAdapter
import com.example.myshopapp.data.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var orderHistoryRecyclerView: RecyclerView
    private lateinit var orderHistoryAdapter: OrdersAdapter
    private lateinit var ordersList: MutableList<Order>
    private lateinit var database: DatabaseReference
    private lateinit var noOrdersTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        val exitButton = findViewById<ImageButton>(R.id.exitButton)

        exitButton.setOnClickListener{
            finish()
        }
        
        orderHistoryRecyclerView = findViewById(R.id.orderHistoryRecyclerView)
        noOrdersTextView = findViewById(R.id.noOrdersTextView)

        orderHistoryRecyclerView.layoutManager = LinearLayoutManager(this)
        ordersList = mutableListOf()
        orderHistoryAdapter = OrdersAdapter(ordersList)
        orderHistoryRecyclerView.adapter = orderHistoryAdapter

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            loadOrderHistory(currentUser.uid)
        }
    }

    private fun loadOrderHistory(userId: String) {
        database = FirebaseDatabase.getInstance().getReference("orders/$userId")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ordersList.clear()
                for (orderSnapshot in snapshot.children) {
                    val order = orderSnapshot.getValue(Order::class.java)
                    order?.let { ordersList.add(it) }
                }
                orderHistoryAdapter.notifyDataSetChanged()

                // Sprawdzenie, czy lista zamówień jest pusta
                if (ordersList.isEmpty()) {
                    orderHistoryRecyclerView.visibility = View.GONE
                    noOrdersTextView.visibility = View.VISIBLE
                } else {
                    orderHistoryRecyclerView.visibility = View.VISIBLE
                    noOrdersTextView.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Obsługa błędu
            }
        })
    }
}
