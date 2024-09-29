package com.example.myshopapp.adapter

import Product
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myshopapp.`object`.Cart
import com.example.myshopapp.R
import com.example.myshopapp.data.CartItem

class CartAdapter(private val updateTotalPriceCallback: () -> Unit) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(
    CartDiffCallback()
) {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productImage: ImageView = itemView.findViewById(R.id.productImage)
        private val productName: TextView = itemView.findViewById(R.id.productName)
        private val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        private val productQuantity: TextView = itemView.findViewById(R.id.productQuantity)
        private val removeOneFromCartButton: Button = itemView.findViewById(R.id.removeOneFromCartButton)
        private val removeAllFromCartButton: Button = itemView.findViewById(R.id.removeAllFromCartButton)

        fun bind(cartItem: CartItem) {
            productName.text = cartItem.name
            val finalPrice = cartItem.price - cartItem.sale
            productPrice.text = String.format("Price: %.2f zł", finalPrice)
            productQuantity.text = "Quantity: ${cartItem.quantity}"

            val imageUrl = cartItem.imageUrls?.firstOrNull()
            if (imageUrl != null) {
                Glide.with(itemView.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .into(productImage)
            } else {
                productImage.setImageResource(R.drawable.placeholder_image)
            }

            removeOneFromCartButton.setOnClickListener {
                if (Cart.removeProduct(cartItem.toProduct(), 1)) {
                    notifyItemRemoved(bindingAdapterPosition)
                } else {
                    notifyItemChanged(bindingAdapterPosition)
                }
                updateTotalPriceCallback()
            }

            removeAllFromCartButton.setOnClickListener {
                Cart.clearProduct(cartItem.toProduct())
                notifyItemRemoved(bindingAdapterPosition)
                updateTotalPriceCallback()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}

private fun CartItem.toProduct(): Product {
    return Product(
        id = this.productId,
        name = this.name,
        price = this.price,
        sale = this.sale,
        image_urls = this.imageUrls
    )
}
