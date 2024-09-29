package com.example.myshopapp.data

import android.os.Parcel
import android.os.Parcelable

data class CartItem(
    var productId: String = "",
    var name: String = "",
    var price: Double = 0.0,
    var sale: Double = 0.0,
    var quantity: Int = 0,
    var imageUrls: List<String>? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.createStringArrayList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(productId)
        parcel.writeString(name)
        parcel.writeDouble(price)
        parcel.writeDouble(sale)
        parcel.writeInt(quantity)
        parcel.writeStringList(imageUrls)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CartItem> {
        override fun createFromParcel(parcel: Parcel): CartItem {
            return CartItem(parcel)
        }

        override fun newArray(size: Int): Array<CartItem?> {
            return arrayOfNulls(size)
        }
    }
}
