package com.example.calcal.modelDTO

import android.os.Parcel
import android.os.Parcelable

data class AddressDTO(
    val roadAddress: String,
    val longName: String,
    val x: String,
    val y: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(roadAddress)
        parcel.writeString(longName)
        parcel.writeString(x)
        parcel.writeString(y)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AddressDTO> {
        override fun createFromParcel(parcel: Parcel): AddressDTO {
            return AddressDTO(parcel)
        }

        override fun newArray(size: Int): Array<AddressDTO?> {
            return arrayOfNulls(size)
        }
    }
}

