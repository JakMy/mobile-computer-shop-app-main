package com.example.myshopapp.`object`

import Product
import com.example.myshopapp.data.CartItem

object Cart {
    private val cartItems = mutableListOf<CartItem>()

    fun addProduct(product: Product, quantity: Int = 1) {
        val existingCartItem = cartItems.find { it.productId == product.id }
        if (existingCartItem != null) {
            existingCartItem.quantity += quantity
        } else {
            cartItems.add(CartItem(product.id, product.name, product.price, product.sale, quantity, product.image_urls))
        }
    }

    fun removeProduct(product: Product, quantity: Int = 1): Boolean {
        val existingCartItem = cartItems.find { it.productId == product.id }
        return if (existingCartItem != null) {
            if (existingCartItem.quantity > quantity) {
                existingCartItem.quantity -= quantity
                false
            } else {
                cartItems.remove(existingCartItem)
                true
            }
        } else {
            false
        }
    }

    fun clearProduct(product: Product) {
        val existingCartItem = cartItems.find { it.productId == product.id }
        if (existingCartItem != null) {
            cartItems.remove(existingCartItem)
        }
    }

    fun getCartItems(): List<CartItem> {
        return cartItems
    }

    fun getTotalPrice(): Double {
        return cartItems.sumOf { (it.price - it.sale) * it.quantity }
    }

    fun clearCart() {
        cartItems.clear()
    }
}
