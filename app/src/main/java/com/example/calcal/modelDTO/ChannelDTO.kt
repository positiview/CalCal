package com.example.calcal.modelDTO

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
    val title: String,
    val link: String?,
    val category: String,
    val description: String,
    val telephone: String?,
    val address: String,
    val roadAddress: String,
    val mapx: String,
    val mapy: String
)