package com.example.calcal.modelDTO

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// 지역 검색 했을때 응답 값 형태를 DTO로 만듬
data class ChannelDTO(
    val lastBuildDate: String,
    val total: Int,
    val start: Int,
    val display: Int,
    val items: List<ItemDTO>
)

data class ItemDTO(
    var title: String,
    val link: String?,
    val category: String,
    val description: String,
    val telephone: String?,
    val address: String,
    val roadAddress: String,
    val mapx: String,
    val mapy: String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(link)
        parcel.writeString(category)
        parcel.writeString(description)
        parcel.writeString(telephone)
        parcel.writeString(address)
        parcel.writeString(roadAddress)
        parcel.writeString(mapx)
        parcel.writeString(mapy)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ItemDTO> {
        override fun createFromParcel(parcel: Parcel): ItemDTO {
            return ItemDTO(parcel)
        }

        override fun newArray(size: Int): Array<ItemDTO?> {
            return arrayOfNulls(size)
        }
    }
}