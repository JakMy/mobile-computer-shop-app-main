package com.example.myshopapp.adapter

import Product
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myshopapp.`object`.Cart
import com.example.myshopapp.CheckoutActivity
import com.example.myshopapp.ProductDetailActivity
import com.example.myshopapp.R

class FavoritesAdapter(private val favoritesList: List<Product>) :
    RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>() {

    class FavoritesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val productBrand: TextView = itemView.findViewById(R.id.productBrand)
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val addToCartButton: Button = itemView.findViewById(R.id.addToCartButton)
        val buyNowButton: Button = itemView.findViewById(R.id.buyNowButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return FavoritesViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        val product = favoritesList[position]
        holder.productName.text = product.name
        holder.productPrice.text = "${product.price - product.sale} zł"
        holder.productBrand.text = product.brand

        val imageUrl = product.image_urls?.firstOrNull()
        if (imageUrl != null) {
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)  // Placeholder podczas ładowania
                .into(holder.productImage)
        } else {
            holder.productImage.setImageResource(R.drawable.placeholder_image)
        }

        holder.addToCartButton.setOnClickListener {
            Cart.addProduct(product, 1)
            Toast.makeText(holder.itemView.context, "${product.name} added to cart", Toast.LENGTH_SHORT).show()
        }

        holder.buyNowButton.setOnClickListener {
            Cart.addProduct(product, 1)
            val intent = Intent(holder.itemView.context, CheckoutActivity::class.java)
            holder.itemView.context.startActivity(intent)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ProductDetailActivity::class.java)
            intent.putExtra("product", product)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = favoritesList.size
}
