package com.example.calcal.subFrag

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calcal.R
import com.example.calcal.adapter.CourseListAdapter
import com.example.calcal.adapter.LocationSearchAdapter
import com.example.calcal.databinding.FragmentSearchLocationBinding
import com.example.calcal.modelDTO.LocationDTO
import com.example.calcal.modelDTO.Result
import com.example.calcal.modelDTO.ReverseGeocodingResponseDTO
import com.example.calcal.retrofit.RequestFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchLocationFragment:Fragment() {
    private lateinit var binding : FragmentSearchLocationBinding
    private lateinit var locationSearchAdapter: LocationSearchAdapter
    private lateinit var courseListAdapter: CourseListAdapter
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient //자동으로 gps값을 받아온다.
    private lateinit var locationCallback: LocationCallback //gps응답 값을 가져온다.
    private lateinit var selectLocation:List<LocationDTO>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchLocationBinding.inflate(inflater,container,false)
        val view = binding.root
        selectLocation = emptyList()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        setLocationList()

       /* val layoutManager = GridLayoutManager(requireContext(), 1)
        binding.selectedLocation.layoutManager = layoutManager*/

        binding.departure.text = selectLocation[0].name + selectLocation[0].location

        binding.arrival.text = selectLocation[1].name + selectLocation[1].location





        return view
    }


    private fun setLocationList(){
        initMyLocation(){
            selectLocation = it
        }
        if (selectLocation.isEmpty()) {
            // locationList가 비어 있다면 출발지와 목적지를 나타내는 두 데이터를 추가합니다.
            Log.d("$$","location size 는 없습니다.")
            selectLocation = listOf(
                LocationDTO("출발지 내용", "[출발지]"),
                LocationDTO("목적지 내용", "[목적지]")
            )
        } else if (selectLocation.size == 1) {
            // locationList에 항목이 하나만 있다면 목적지를 나타내는 데이터를 추가합니다.
            Log.d("$$","location size 는 1입니다.")
            selectLocation =  listOf(
                selectLocation[0],LocationDTO("목적지 내용", "[목적지]")
            )
        } else {
            // 그 외의 경우는 주어진 locationList를 그대로 사용합니다.
            Log.d("$$","location size 는 ${selectLocation.size}입니다.")

        }




    }
    private fun initMyLocation(callback: (List<LocationDTO>) -> Unit) {
        var locationList = mutableListOf<LocationDTO>()

        Log.d("$$","setLocationList 에서 내 위치 가져오기")

        val locationPermissionRequest =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    // 권한이 허용되면 위치 정보 가져오기
                    fusedLocationProviderClient.lastLocation
                        .addOnSuccessListener { location: Location? ->
                            location?.let {
                                getAddressName(it) { address ->
                                    locationList[0].location = address
                                    callback(locationList)
                                }
                            } ?: run {
                                // 마지막으로 알려진 위치가 없는 경우 처리
                                Log.d("$$", "마지막으로 알려진 위치가 없습니다.")
                                locationList[0].location = ""
                                callback(locationList)
                            }
                        }
                } else {
                    // 권한이 거부된 경우 적절히 처리
                    Log.d("$$", "위치 권한이 거부되었습니다.")
                    locationList[0].location = ""
                    callback(locationList)
                    // 사용자에게 위치 권한이 필요한 이유를 설명하는 메시지를 표시하는 것이 좋습니다.
                }
            }

        // 내위치 정보 가져오기
            // 위치 권한이 허용되어 있는 경우
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let { // it 이 latitude와 longitude를 가지고 있음
                        getAddressName(it) { address ->
                            locationList[0].location = address
                            callback(locationList)
                        }
                    } ?: run {
                        // 마지막으로 알려진 위치가 없는 경우에 대한 처리
                        // 예: 위치 권한이 거부되어 있을 때
                        Log.d("$$", "마지막으로 알려진 위치가 없습니다.")
                        // 적절한 에러 처리 또는 기본 위치 설정 등을 수행할 수 있습니다.
                        // 이 예제에서는 빈 주소로 설정합니다.
                        locationList[0].location = ""
                        callback(locationList)
                    }
                }

        }else{
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }



        



    }

    private fun getAddressName(location: Location?, callback: (String) -> Unit ) {
        Log.d("$$","getAddressName 주소 정보 요청 location : $location")
        val apiKeyId = "clurvbfncz"
        val apiKey = "WuVnFkJnFdIt7L03dhCZw7iCyNCeLGtNh3UsrhrI"
        val service = RequestFactory.create2()
        val coords = location?.longitude.toString()+","+location?.latitude.toString()
        Log.d("$$","coords >>> $coords")
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
                }else{
                    Log.e("$$","주소 검색 결과 응답 실패!!!")
                }
            }

            override fun onFailure(call: Call<ReverseGeocodingResponseDTO>, t: Throwable) {
                Log.d("$$","요청 실패 onFailure")
            }
        })
    }


}