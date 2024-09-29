package com.example.myshopapp.data

import Product
import java.io.Serializable

data class PowerSupply(
    var wattage: String? = null,
    var modularity: String? = null,
    var efficiency_rating: String? = null
) : Product(), Serializable
