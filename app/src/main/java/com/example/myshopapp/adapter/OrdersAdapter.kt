package com.example.myshopapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myshopapp.R
import com.example.myshopapp.data.Order

class OrdersAdapter(private val orders: List<Order>) :
    RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.productName)
        private val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        private val orderTotalPrice: TextView = itemView.findViewById(R.id.orderTotalPrice)
        private val deliveryAddress: TextView = itemView.findViewById(R.id.deliveryAddress)
        private val paymentMethod: TextView = itemView.findViewById(R.id.paymentMethod)

        fun bind(order: Order) {
            if (order.cartItems.isNotEmpty()) {
                val cartItem = order.cartItems[0]
                productName.text = cartItem.name
                productPrice.text = "${cartItem.price} zł"
            }

            orderTotalPrice.text = "Total: ${order.totalPrice} zł"
            deliveryAddress.text = "Delivery to: ${order.deliveryAddress}, ${order.city}, ${order.postalCode}, ${order.country}"
            paymentMethod.text = "Payment: ${order.paymentMethod}"
        }
    }
}
