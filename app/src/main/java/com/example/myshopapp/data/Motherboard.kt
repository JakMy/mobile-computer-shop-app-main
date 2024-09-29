package com.example.myshopapp.data

import Product
import java.io.Serializable

data class Motherboard(
    var chipset: String? = null,
    var form_factor: String? = null,
    var socket: String? = null,
    var memory_slots: Int? = null,
    var memory_standard: String? = null
) : Product(), Serializable
