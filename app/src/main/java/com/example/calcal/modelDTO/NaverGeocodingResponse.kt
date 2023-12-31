package com.example.calcal.modelDTO

// 주소 검색 요청할때 응답JSON형태를 DTO로 만듬
data class NaverGeocodingResponseDTO(
    val status: String,
    val meta: Meta,
    val addresses: List<Address>,
    val errorMessage: String
)

data class Meta(
    val totalCount: Int,
    val page: Int,
    val count: Int
)

data class Address(
    val roadAddress: String,
    val jibunAddress: String,
    val englishAddress: String,
    val addressElements: List<AddressElement>,
    val x: String,
    val y: String,
    val distance: Double
)

data class AddressElement(
    val types: List<String>,
    val longName: String,
    val shortName: String,
    val code: String
)


