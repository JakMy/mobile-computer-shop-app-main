package com.example.myshopapp.data

import Product
import com.google.firebase.database.PropertyName
import java.io.Serializable

data class GraphicCard(
    @get:PropertyName("core_clock") @set:PropertyName("core_clock") var coreClock: String? = null,
    @get:PropertyName("boost_clock") @set:PropertyName("boost_clock") var boostClock: String? = null,
    @get:PropertyName("recommended_wattage") @set:PropertyName("recommended_wattage") var recommendedWattage: String? = null,
    var size: Int? = null
) : Product(), Serializable
