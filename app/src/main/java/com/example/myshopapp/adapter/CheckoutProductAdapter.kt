package com.example.myshopapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myshopapp.R
import com.example.myshopapp.data.CartItem

class CheckoutProductAdapter(private var cartItems: List<CartItem>) : RecyclerView.Adapter<CheckoutProductAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_checkout_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cartItem = cartItems[position]
        holder.bind(cartItem)
    }

    override fun getItemCount(): Int = cartItems.size

    fun submitList(newCartItems: List<CartItem>) {
        cartItems = newCartItems
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productNameTextView: TextView = itemView.findViewById(R.id.productNameTextView)
        private val productPriceTextView: TextView = itemView.findViewById(R.id.productPriceTextView)

        fun bind(cartItem: CartItem) {
            productNameTextView.text = cartItem.name
            productPriceTextView.text = String.format("%.2f zł", cartItem.price)
        }
    }
}
