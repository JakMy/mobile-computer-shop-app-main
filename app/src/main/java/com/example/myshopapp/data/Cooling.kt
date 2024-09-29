package com.example.myshopapp.data

import Product
import java.io.Serializable

data class Cooling(
    var type: String? = null,
    var fans: Int? = null,
    var radiator_size: Int? = null
) : Product(), Serializable
