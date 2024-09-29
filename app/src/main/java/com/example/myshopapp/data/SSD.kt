package com.example.myshopapp.data

import Product
import java.io.Serializable

data class SSD(
    var capacity: String? = null,
    var interfaceType: String? = null,
    var form_factor: String? = null
) : Product(), Serializable
