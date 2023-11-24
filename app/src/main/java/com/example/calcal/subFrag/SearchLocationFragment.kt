package com.example.calcal.subFrag

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.calcal.R
import com.example.calcal.adapter.CourseListAdapter
import com.example.calcal.adapter.LocationSearchAdapter
import com.example.calcal.databinding.FragmentSearchLocationBinding
import com.example.calcal.modelDTO.Address
import com.example.calcal.modelDTO.LocationDTO
import com.example.calcal.modelDTO.NaverGeocodingResponseDTO
import com.example.calcal.modelDTO.Result
import com.example.calcal.modelDTO.ReverseGeocodingResponseDTO
import com.example.calcal.retrofit.RequestFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchLocationFragment:Fragment() {
    private lateinit var binding : FragmentSearchLocationBinding
    private lateinit var locationSearchAdapter: LocationSearchAdapter
    private lateinit var courseListAdapter: CourseListAdapter
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient //자동으로 gps값을 받아온다.
    lateinit var locationCallback: LocationCallback //gps응답 값을 가져온다.
    lateinit var selectLocation:List<LocationDTO>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchLocationBinding.inflate(inflater,container,false)
        val view = binding.root



        setLocationList(){
            selectLocation = it
        }

        locationSearchAdapter = LocationSearchAdapter {

            val bundle = Bundle().apply{
                putString("address",it.location)
            }
            findNavController().navigate(R.id.action_searchLocationFragment_to_searchAddressFragment,bundle)
        }

        locationSearchAdapter.setLocationList(selectLocation)

        // 어댑터 설정하기
        binding.selectedLocation.adapter = locationSearchAdapter
//        courseRecyclerView.adapter = courseListAdapter

        return view
    }

    private fun setLocationList(callback : (List<LocationDTO>) -> Unit) {
        var locationList = listOf<LocationDTO>()
        val initialLocation = LocationDTO("1", "[현재 위치] ")
        locationList = locationList.plus(initialLocation)


        Log.d("$$","setLocationList 에서 내 위치 가져오기")

        // 내위치 정보 가져오기
       /* locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                Log.d("$$", "locationresult 값 : $locationResult")
                for ((i, location) in locationResult.locations.withIndex()) {
                    Log.d("$$", "${location.latitude}, ${location.longitude}")
                    getAddressName(location) {
                        locationList[0].location=it
                    }
                }
            }
        }*/


        callback(locationList)
    }

    private fun getAddressName(location: Location?, callback: (String) -> Unit ) {
        Log.d("$$","getAddressName 주소 정보 요청 location : $location")
        val apiKeyId = "clurvbfncz"
        val apiKey = "WuVnFkJnFdIt7L03dhCZw7iCyNCeLGtNh3UsrhrI"
        val service = RequestFactory.create2()
        val coords = location?.longitude.toString()+","+location?.latitude.toString()
        val callLocationList: Call<ReverseGeocodingResponseDTO> = service.reverseGeocode(coords, apiKeyId, apiKey)

        callLocationList.enqueue(object : Callback<ReverseGeocodingResponseDTO>{
            override fun onResponse(
                call: Call<ReverseGeocodingResponseDTO>,
                response: Response<ReverseGeocodingResponseDTO>
            ) {
                Log.d("$$","주소 검색 결과 읍답 : $response")
                if(response.isSuccessful){
                    val respAddress: ReverseGeocodingResponseDTO? = response.body()

                    if(respAddress != null && respAddress.results.isNotEmpty()){
                        val resultList: List<Result> = respAddress.results
                        var actualAddress = "${resultList[0].region.area1.name} ${resultList[0].region.area2.name} ${resultList[0].region.area3.name} ${resultList[0].region.area4.name}".trim()
                        Log.d("$$" , "actualAddress = $actualAddress")
                        callback(actualAddress)

                    }else {
                        println("에러: ${response.code()} - ${response.message()}")
                    }
                }
            }

            override fun onFailure(call: Call<ReverseGeocodingResponseDTO>, t: Throwable) {
                Log.d("$$","요청 실패 onFailure")
            }
        })
    }


}