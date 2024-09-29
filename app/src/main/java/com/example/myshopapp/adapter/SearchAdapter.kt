package com.example.myshopapp.adapter

import Product
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myshopapp.R

class SearchAdapter(
    private var products: List<Product>,
    private val onProductClick: (Product) -> Unit,
    private val onAddToCartClick: (Product) -> Unit,
    private val onBuyNowClick: (Product) -> Unit
) : RecyclerView.Adapter<SearchAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    fun updateData(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productImage: ImageView = itemView.findViewById(R.id.productImage)
        private val productName: TextView = itemView.findViewById(R.id.productName)
        private val productBrand: TextView = itemView.findViewById(R.id.productBrand)
        private val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        private val addToCartButton: Button = itemView.findViewById(R.id.addToCartButton)
        private val buyNowButton: Button = itemView.findViewById(R.id.buyNowButton)

        fun bind(product: Product) {
            productName.text = product.name
            productBrand.text = product.brand
            productPrice.text = String.format("%.2f zł", product.price - product.sale)

            Glide.with(itemView.context)
                .load(product.image_urls?.firstOrNull())
                .into(productImage)

            itemView.setOnClickListener {
                onProductClick(product)
            }

            addToCartButton.setOnClickListener {
                onAddToCartClick(product)
            }

            buyNowButton.setOnClickListener {
                onBuyNowClick(product)
            }
        }
    }
}
