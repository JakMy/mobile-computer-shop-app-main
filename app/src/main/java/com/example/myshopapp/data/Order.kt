package com.example.myshopapp.data

import android.os.Parcel
import android.os.Parcelable

data class Order(
    val recipientName: String = "",
    val deliveryAddress: String = "",
    val country: String = "",
    val city: String = "",
    val postalCode: String = "",
    val paymentMethod: String = "",
    val cartItems: List<CartItem> = emptyList(),
    val totalPrice: Double = 0.0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createTypedArrayList(CartItem.CREATOR) ?: emptyList(),
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(recipientName)
        parcel.writeString(deliveryAddress)
        parcel.writeString(country)
        parcel.writeString(city)
        parcel.writeString(postalCode)
        parcel.writeString(paymentMethod)
        parcel.writeTypedList(cartItems)
        parcel.writeDouble(totalPrice)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Order> {
        override fun createFromParcel(parcel: Parcel): Order {
            return Order(parcel)
        }

        override fun newArray(size: Int): Array<Order?> {
            return arrayOfNulls(size)
        }
    }
}
