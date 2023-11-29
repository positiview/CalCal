package com.example.calcal.subFrag

import android.Manifest
import android.annotation.SuppressLint
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
import com.example.calcal.modelDTO.Coords
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
    private lateinit var location_departure: CoordinateDTO
    private var myLocation: Result? = null
    private var location_waypoint1: CoordinateDTO? = null
    private var location_waypoint2: CoordinateDTO? = null
    private var location_waypoint3: CoordinateDTO? = null
    private var location_waypoint4: CoordinateDTO? = null
    private var location_waypoint5: CoordinateDTO? = null

    private lateinit var location_arrival: CoordinateDTO
    private var myArea:String = ""
    /*private val locationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // 권한이 허용되면 위치 정보 가져오기
                   getMyLocation(){
                       if(it != null){

                           myArea = it.region.area2.name
                           val actualAddress = "${it.region.area1.name} $myArea ${it.region.area3.name} ${it.region.area4.name}".trim()
                           myLocation = it
                           binding.departure.text = "[내 위치] $actualAddress"
                           location_departure = CoordinateDTO(longitude = myLocation!!.region.area4.coords.center.x, latidute = myLocation!!.region.area4.coords.center.y)
                       }else{
                           Toast.makeText(requireContext(),"내 위치를 찾을 수 없습니다.",Toast.LENGTH_SHORT).show()
                       }
                   }


            } else {
                // 권한이 거부된 경우 적절히 처리
                Log.d("$$", "위치 권한이 거부되었습니다.")


                // 사용자에게 위치 권한이 필요한 이유를 설명해야함
            }
        }*/

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



        checkGrantAndGetLocation(){

            if(it != null){

                myArea = it.region.area2.name // 내 지역 데이터 저장

                val actualAddress = "${it.region.area1.name} ${it.region.area2.name} ${it.region.area3.name} ${it.region.area4.name}".trim()

                binding.departure.text = "[내 위치] $actualAddress"
                location_departure = CoordinateDTO(longitude = it.region.area4.coords.center.x, latidute = it.region.area4.coords.center.y)

            }else{
                Toast.makeText(requireContext(),"내 위치를 찾을 수 없습니다.",Toast.LENGTH_SHORT).show()
            }


        }

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
                location_waypoint1 = null
                waypointCount--
                updateAddWaypointVisibility(waypointCount)
            }
            waypoint2Cancel.setOnClickListener {
                waypoint2.visibility = View.GONE
                waypoint2Text.hint = "[경유지]"
                waypoint2Text.text = ""
                location_waypoint2 = null
                waypointCount--
                updateAddWaypointVisibility(waypointCount)
            }
            waypoint3Cancel.setOnClickListener {
                waypoint3.visibility = View.GONE
                waypoint3Text.hint = "[경유지]"
                waypoint3Text.text = ""
                location_waypoint3 = null
                waypointCount--
                updateAddWaypointVisibility(waypointCount)
            }
            waypoint4Cancel.setOnClickListener {
                waypoint4.visibility = View.GONE
                waypoint4Text.hint = "[경유지]"
                waypoint4Text.text = ""
                location_waypoint4 = null
                waypointCount--
                updateAddWaypointVisibility(waypointCount)

            }
            waypoint5Cancel.setOnClickListener {
                waypoint5.visibility = View.GONE
                waypoint5Text.hint = "[경유지]"
                waypoint5Text.text = ""
                location_waypoint5 = null
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
        searchAddressDialog.setWaypointTextView(textView)
        searchAddressDialog.setOnItemClickListener(object : SearchAddressDialog.OnItemClickListener {
            override fun onItemClicked(itemDTO: ItemDTO) {
                Log.d("$$","onItemClicked 설정 textView = $textView , itemDTO = $itemDTO")
                textView.text = itemDTO.title
                val coords = CoordinateDTO(longitude = itemDTO.mapx.toDouble(), latidute = itemDTO.mapy.toDouble())
                coordinateData(textView,coords)

            }

            override fun onMyLocationClicked() {


                getMyLocation {
                    if(it!=null){

                        textView.text = "[내 위치] ${it.region.area1.name} ${it.region.area2.name} ${it.region.area3.name} ${it.region.area4.name}".trim()
                        val coords = CoordinateDTO(it.region.area4.coords.center.y,it.region.area4.coords.center.x)
                        coordinateData(textView,coords)
                    }
                }
            }

        })

        searchAddressDialog.setClickedTextView(textView)

        searchAddressDialog.show(childFragmentManager, SearchAddressDialog.TAG)

    }

    private fun coordinateData(textView: TextView, coordinateDTO: CoordinateDTO) {
        when(textView.id.toString()){
            "departure" ->{
                location_departure = coordinateDTO
            }
            "waypoint1Text"->{
                location_waypoint1 = coordinateDTO
            }
            "waypoint2Text"->{
                location_waypoint2 = coordinateDTO
            }
            "waypoint3Text"->{
                location_waypoint3 = coordinateDTO
            }
            "waypoint4Text"->{
                location_waypoint4 = coordinateDTO
            }
            "waypoint5Text"->{
                location_waypoint5 = coordinateDTO
            }
            "arrival"->{
                location_arrival = coordinateDTO
            }
        }
    }

    private fun updateAddWaypointVisibility(waypointCount:Int) {
        binding.addWaypoint.visibility = if (waypointCount < 5) View.VISIBLE else View.GONE    }



    private fun checkGrantAndGetLocation(callback: (Result?) -> Unit) {
        val locationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // 권한이 허용되면 위치 정보 가져오기
                getMyLocation(){
                    callback(it)
                }


            } else {
                // 권한이 거부된 경우 적절히 처리
                Log.d("$$", "위치 권한이 거부되었습니다.")


                // 사용자에게 위치 권한이 필요한 이유를 설명해야함
            }
        }

        Log.d("$$","setLocationList 에서 내 위치 가져오기")


            // 위치 권한이 허용되어 있는 경우
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getMyLocation(){

                callback(it)
            }

        }else{
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }







    }

    @SuppressLint("MissingPermission")
    private fun getMyLocation(callback: (Result?) -> Unit) {

        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    getAddressName(it) { address ->
                        callback(address.firstOrNull())

                    }
                } ?: run {
                    // 마지막으로 알려진 위치가 없는 경우 처리
                    Log.d("$$", "마지막으로 알려진 위치가 없습니다.")
                    callback(null)

                }
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


