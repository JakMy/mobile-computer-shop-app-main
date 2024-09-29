package com.example.myshopapp.data

import Product
import java.io.Serializable

data class Processor(
    var cores: Int? = null,
    var threads: Int? = null,
    var base_clock: String? = null,
    var boost_clock: String? = null,
    var socket: String? = null
) : Product(), Serializable
