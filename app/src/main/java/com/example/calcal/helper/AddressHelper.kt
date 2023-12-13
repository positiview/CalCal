package com.example.calcal.helper

import android.util.Log
import com.example.calcal.modelDTO.Result
import com.example.calcal.modelDTO.ReverseGeocodingResponseDTO
import com.example.calcal.retrofit.RequestFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddressHelper {

    companion object {

        fun getAddressName(coords: String, callback: (List<Result>) -> Unit) {
            Log.d("$$", "getAddressName 주소 정보 요청")
            val apiKeyId = "clurvbfncz"
            val apiKey = "WuVnFkJnFdIt7L03dhCZw7iCyNCeLGtNh3UsrhrI"
            val service = RequestFactory.create2()
            val request = "coordsToaddr"
            val sourcecrs = "epsg:4326"
            val output = "json"
            val orders = "legalcode,admcode"

            Log.d("$$", "coords >>> $coords")
            val callLocationList: Call<ReverseGeocodingResponseDTO> =
                service.reverseGeocode(request, coords, sourcecrs, output, orders, apiKeyId, apiKey)

            callLocationList.enqueue(object : Callback<ReverseGeocodingResponseDTO> {
                override fun onResponse(
                    call: Call<ReverseGeocodingResponseDTO>,
                    response: Response<ReverseGeocodingResponseDTO>
                ) {
                    Log.d("$$", "주소 검색 결과 응답 : $response")
                    if (response.isSuccessful) {
                        val respAddress: ReverseGeocodingResponseDTO? = response.body()

                        if (respAddress != null && respAddress.results.isNotEmpty()) {
                            val resultList: List<Result> = respAddress.results
                            Log.d("$$", "내 위치 주소 resultList >> $resultList")

                            callback(resultList)

                        } else {
                            println("에러: ${response.code()} - ${response.message()}")
                        }
                    } else {
                        Log.e("$$", "주소 검색 결과 응답 실패!!!")
                    }
                }

                override fun onFailure(call: Call<ReverseGeocodingResponseDTO>, t: Throwable) {
                    Log.d("$$", "요청 실패 onFailure")

                }
            })
        }
    }
}