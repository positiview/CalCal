package com.example.calcal.subFrag

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Point
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.calcal.adapter.CourseListAdapter
import com.example.calcal.adapter.LocationSearchAdapter
import com.example.calcal.databinding.FragmentSearchLocationBinding
import com.example.calcal.modelDTO.LocationDTO
import com.example.calcal.modelDTO.Result
import com.example.calcal.modelDTO.ReverseGeocodingResponseDTO
import com.example.calcal.modelDTO.CoordinateDTO
import com.example.calcal.modelDTO.ItemDTO
import com.example.calcal.modelDTO.DeviceSizeDTO
import com.example.calcal.retrofit.RequestFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
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
    private lateinit var departure: CoordinateDTO
    private lateinit var waypoint1: CoordinateDTO
    private lateinit var waypoint2: CoordinateDTO
    private lateinit var waypoint3: CoordinateDTO
    private lateinit var waypoint4: CoordinateDTO
    private lateinit var waypoint5: CoordinateDTO
    private lateinit var arrival: CoordinateDTO
    private var myArea:String = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchLocationBinding.inflate(inflater,container,false)
        val view = binding.root
        var waypointCount = 0
        selectLocation = emptyList()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        setLocationList()
        Log.d("$$"," selectLocation 값 : $selectLocation")

        /* val layoutManager = GridLayoutManager(requireContext(), 1)
         binding.selectedLocation.layoutManager = layoutManager*/
        if(binding.departure.text.isEmpty()){
            binding.btnRoundTrip.visibility = View.GONE
        } else {
            binding.btnRoundTrip.visibility = View.VISIBLE
        }


        binding.apply{
            btnSwitch.setOnClickListener {
                val temp = departure.text
                departure.text = arrival.text
                arrival.text = temp
            }
            btnRoundTrip.setOnClickListener {



                val newWaypoint = when {
                    waypoint1.visibility != View.VISIBLE -> waypoint1Text
                    waypoint2.visibility != View.VISIBLE -> waypoint2Text
                    waypoint3.visibility != View.VISIBLE -> waypoint3Text
                    waypoint4.visibility != View.VISIBLE -> waypoint4Text
                    waypoint5.visibility != View.VISIBLE -> waypoint5Text
                    else -> null
                }

                addWaypoint.performClick()

                if(newWaypoint==null) {
                    Toast.makeText(requireContext(),"경유지는 최대 5개까지 설정 할 수 있어, 목적지를 출발지로 설정했습니다.",Toast.LENGTH_SHORT).show()
                }

                newWaypoint?.text = arrival.text



                arrival.text = departure.text
            }

            updateAddWaypointVisibility(waypointCount)

            addWaypoint.setOnClickListener {
                waypointCount++
                updateAddWaypointVisibility(waypointCount)
                val waypoints = listOf(waypoint1, waypoint2, waypoint3, waypoint4, waypoint5)
                waypoints.firstOrNull { !it.isVisible }?.visibility = View.VISIBLE
                // 웨이포인트 1번 부터 5번까지 리스트중 첫번째로 visible되어 있지 않은 waypoint가 있는지 확인하는 코드.. 확인 도중 있다면 보이게 하고 종료 없으면 null 로 반환후 종료

            }


            waypoint1Cancel.setOnClickListener {
                waypoint1.visibility = View.GONE
                waypoint1Text.hint = "[경유지]"
                waypoint1Text.text = ""
                waypointCount--
                updateAddWaypointVisibility(waypointCount)
            }
            waypoint2Cancel.setOnClickListener {
                waypoint2.visibility = View.GONE
                waypoint2Text.hint = "[경유지]"
                waypoint2Text.text = ""
                waypointCount--
                updateAddWaypointVisibility(waypointCount)
            }
            waypoint3Cancel.setOnClickListener {
                waypoint3.visibility = View.GONE
                waypoint3Text.hint = "[경유지]"
                waypoint3Text.text = ""
                waypointCount--
                updateAddWaypointVisibility(waypointCount)
            }
            waypoint4Cancel.setOnClickListener {
                waypoint4.visibility = View.GONE
                waypoint4Text.hint = "[경유지]"
                waypoint4Text.text = ""
                waypointCount--
                updateAddWaypointVisibility(waypointCount)

            }
            waypoint5Cancel.setOnClickListener {
                waypoint5.visibility = View.GONE
                waypoint5Text.hint = "[경유지]"
                waypoint5Text.text = ""
                waypointCount--
                updateAddWaypointVisibility(waypointCount)
            }


            val waypoints = arrayOf(departure,waypoint1Text, waypoint2Text, waypoint3Text, waypoint4Text, waypoint5Text,arrival)

            waypoints.forEach { waypoint ->
                waypoint.setOnClickListener{
                    openSearchAddressDialog(waypoint)
                }
            }


        }




        return view
    }



    private fun openSearchAddressDialog(textView: TextView) {
        val searchAddressDialog = SearchAddressDialog(myArea)
        searchAddressDialog.setOnItemClickListener(object : SearchAddressDialog.OnItemClickListener {
            override fun onItemClicked(itemDTO: ItemDTO) {
                textView.text = itemDTO.title
            }

            override fun onMyLocationClicked(myLocation: CoordinateDTO) {

            }

        })

        searchAddressDialog.setClickedTextView(textView)

        searchAddressDialog.show(childFragmentManager, SearchAddressDialog.TAG)

    }

    private fun updateAddWaypointVisibility(waypointCount:Int) {
        binding.addWaypoint.visibility = if (waypointCount < 5) View.VISIBLE else View.GONE    }


    private fun setLocationList(){
        var locationList = mutableListOf<LocationDTO>()
        initMyLocation(){
            val name = "[내 위치]"
            val area = it[0].region.area2.name
            myArea = area
            var actualAddress = "${it[0].region.area1.name} $area ${it[0].region.area3.name} ${it[0].region.area4.name}".trim()
            Log.d("$$" , "actualAddress = $actualAddress")
            locationList.add(LocationDTO(actualAddress,name))



            /* if (selectLocation.isEmpty()) {
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
             }*/

            binding.departure.text = locationList[0].name + locationList[0].location


        }
    }
    private fun initMyLocation(callback: (List<Result>) -> Unit) {

        Log.d("$$","setLocationList 에서 내 위치 가져오기")

        val locationPermissionRequest =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    // 권한이 허용되면 위치 정보 가져오기

                    fusedLocationProviderClient.lastLocation
                        .addOnSuccessListener { location: Location? ->
                            location?.let {
                                getAddressName(it) { address ->

                                    callback(address)
                                }
                            } ?: run {
                                // 마지막으로 알려진 위치가 없는 경우 처리
                                Log.d("$$", "마지막으로 알려진 위치가 없습니다.")

//                                callback(locationList)
                            }
                        }
                } else {
                    // 권한이 거부된 경우 적절히 처리
                    Log.d("$$", "위치 권한이 거부되었습니다.")

//                    callback(locationList)
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
                            /*val name = "[내 위치]"
                            var actualAddress = "${address[0].region.area1.name} ${address[0].region.area2.name} ${address[0].region.area3.name} ${address[0].region.area4.name}".trim()
                            Log.d("$$" , "actualAddress = $actualAddress")
                            locationList.add(LocationDTO(actualAddress,name))*/
                            callback(address)
                        }
                    } ?: run {
                        // 마지막으로 알려진 위치가 없는 경우에 대한 처리
                        // 예: 위치 권한이 거부되어 있을 때
                        Log.d("$$", "마지막으로 알려진 위치가 없습니다.")
                        // 적절한 에러 처리 또는 기본 위치 설정 등을 수행할 수 있습니다.
                        // 이 예제에서는 빈 주소로 설정합니다.

//                        callback(locationList)
                    }
                }

        }else{
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }







    }

    private fun getAddressName(location: Location?, callback: (List<Result>) -> Unit ) {
        Log.d("$$","getAddressName 주소 정보 요청 location : $location")
        val apiKeyId = "clurvbfncz"
        val apiKey = "WuVnFkJnFdIt7L03dhCZw7iCyNCeLGtNh3UsrhrI"
        val service = RequestFactory.create2()
        val request = "coordsToaddr"
        val sourcecrs = "epsg:4326"
        val output = "json"
        val orders="legalcode,admcode"
        val coords = location?.longitude.toString()+","+location?.latitude.toString()
        Log.d("$$","coords >>> $coords")
        val callLocationList: Call<ReverseGeocodingResponseDTO> = service.reverseGeocode(request,coords, sourcecrs,output,orders, apiKeyId, apiKey)

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
                        Log.d("$$","resultList >> $resultList")

                        callback(resultList)

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