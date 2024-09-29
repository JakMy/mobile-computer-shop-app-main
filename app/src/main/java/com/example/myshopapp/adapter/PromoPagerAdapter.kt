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

class PromoPagerAdapter(
    private var promoProducts: List<Product> = listOf(),
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<PromoPagerAdapter.PromoViewHolder>() {

    inner class PromoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productImage: ImageView = itemView.findViewById(R.id.productImage)
        private val productName: TextView = itemView.findViewById(R.id.productName)
        private val productBrand: TextView = itemView.findViewById(R.id.productBrand)
        private val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        private val productSale: TextView = itemView.findViewById(R.id.productSale)
        private val addToCartButton: Button = itemView.findViewById(R.id.addToCartButton)
        private val buyNowButton: Button = itemView.findViewById(R.id.buyNowButton)

        fun bind(product: Product) {
            productName.text = product.name
            productBrand.text = product.brand
            productPrice.text = "${product.price} zł"
            productSale.text = "-${product.sale} zł"

            val imageUrl = product.image_urls?.firstOrNull()
            if (imageUrl != null) {
                Glide.with(itemView.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .into(productImage)
            } else {
                productImage.setImageResource(R.drawable.placeholder_image)
            }

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ProductDetailActivity::class.java)
                intent.putExtra("product", product)
                itemView.context.startActivity(intent)
            }


            addToCartButton.setOnClickListener {
                Cart.addProduct(product, 1)
                Toast.makeText(itemView.context, "${product.name} added to cart", Toast.LENGTH_SHORT).show()
            }

            buyNowButton.setOnClickListener {
                Cart.addProduct(product, 1)
                val intent = Intent(itemView.context, CheckoutActivity::class.java)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_promo, parent, false)
        return PromoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PromoViewHolder, position: Int) {
        holder.bind(promoProducts[position])
    }

    override fun getItemCount(): Int = promoProducts.size

    fun updateProducts(newPromoProducts: List<Product>) {
        promoProducts = newPromoProducts
        notifyDataSetChanged()
    }
}
