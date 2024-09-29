package com.example.myshopapp.data

import Product
import java.io.Serializable

data class Memory(
    var type: String? = null,
    var capacity: String? = null,
    var quantity: Int? = null,
    var speed: String? = null
) : Product(), Serializable
