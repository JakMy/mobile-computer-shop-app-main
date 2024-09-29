package com.example.myshopapp.data

import Product
import java.io.Serializable

data class Case(
    var form_factor: String? = null,
    var color: String? = null,
    var power_supply: String? = null,
    var max_size_of_graphic_card: Int? = null,
    var max_size_of_cooling: Int? = null
) : Product(), Serializable
