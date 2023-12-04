package com.example.calcal.subFrag

import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Chronometer
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.calcal.MainActivity
import com.example.calcal.R
import com.example.calcal.databinding.FragmentMapBinding
import com.example.calcal.modelDTO.CoordinateDTO
import com.example.calcal.modelDTO.DataDTO
import com.example.calcal.modelDTO.DirectionResponseDTO
import com.example.calcal.modelDTO.FeatureCollection
import com.example.calcal.modelDTO.TMapRouteRequest
import com.example.calcal.repository.CourseRepository
import com.example.calcal.repository.CourseRepositoryImpl
import com.example.calcal.retrofit.RequestFactory
import com.example.calcal.util.Resource
import com.example.calcal.viewModel.CourseViewModel
import com.example.calcal.viewModelFactory.CourseViewModelFactory
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.UiSettings
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.overlay.PolylineOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import com.naver.maps.map.widget.LocationButtonView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST
import java.lang.Integer.min


class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding : FragmentMapBinding
    private lateinit var locationSource: FusedLocationSource
    private lateinit var mNaverMap: NaverMap
    private lateinit var uiSettings: UiSettings
    private lateinit var btn_back:ImageView

    private val courseRepository: CourseRepository = CourseRepositoryImpl()
    private val courseViewModelFactory = CourseViewModelFactory(courseRepository)
    private val viewModel: CourseViewModel by activityViewModels() { courseViewModelFactory }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = FragmentMapBinding.inflate(inflater,container, false)
        val view = binding.root
        val options = NaverMapOptions()
            .mapType(NaverMap.MapType.Terrain)
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance(options).also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }

        binding.startbutton.setOnClickListener {
            binding.singleLayout.visibility = View.GONE
            binding.stopwatchChronometer.visibility = View.VISIBLE
        }


        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        val myRouteRecord: MutableList<LatLng> = mutableListOf()


        binding.apply {
            toggleCourse.textOff = null
            toggleCourse.textOn = null
            toggleCourse.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked){
                    toggleCourse.setBackgroundResource(R.drawable.ic_minus_shape)
                    courseRecode.visibility = View.VISIBLE
                }else{
                    toggleCourse.setBackgroundResource(R.drawable.ic_plus_shape)
                    toggleCourse.visibility = View.GONE
                }
            }

            selectCourse.setOnClickListener{
                findNavController().navigateUp()
            }



            val onLocationChangeListener = object : NaverMap.OnLocationChangeListener {
                override fun onLocationChange(location: Location) {
                    myRouteRecord.add(LatLng(location.latitude, location.longitude))
                    val polyline = PolylineOverlay()
                    polyline.color = Color.BLUE
                    polyline.width = 10
                    polyline.map = mNaverMap
                    polyline.coords = myRouteRecord
                    // 내 위치를 설정한 후에 리스너를 제거
                }
            }
            btnStart.setOnClickListener {
                 //위치 추적 모드, 현위치 오버레이와 카메라 좌표가 사용자의 위치를 따라 움직입니다. API나 제스터를 사용시 위치추적모드 해제
                mNaverMap.locationTrackingMode = LocationTrackingMode.Follow // <-- 에뮬에서는 안되는듯


                // 위치추적모드가 활성화 될때 이벤트 처리
                mNaverMap.addOnLocationChangeListener(onLocationChangeListener)
                        /*val initialPosition = LatLng(location.latitude, location.longitude)
                        val cameraPosition = CameraPosition(initialPosition, 17.0)
                        mNaverMap.moveCamera(CameraUpdate.toCameraPosition(cameraPosition))
                        mNaverMap.maxZoom = 18.0
                        mNaverMap.minZoom = 5.0
                        Log.d("$$","onLocationChange 발동!")*/
                        // 내 위치를 설정한 후에 리스너를 제거




            }
            // 일시 중지 버튼 (토글 버튼 추천)
            btnPause.setOnClickListener {

            }

            // 종료 버튼 + 저장
            btnStop.setOnClickListener {

            }

        }

        btn_back = binding.btnBack
        btn_back.setOnClickListener {
            NavHostFragment.findNavController(this).navigateUp()
        }





        return binding.root
    }

    // 좌표 리스트로부터 경계를 계산하는 함수
    fun calculateBounds(coords: List<LatLng>): LatLngBounds {
        var minLat = Double.MAX_VALUE
        var maxLat = Double.MIN_VALUE
        var minLng = Double.MAX_VALUE
        var maxLng = Double.MIN_VALUE

        for (coord in coords) {
            minLat = minOf(minLat, coord.latitude)
            maxLat = maxOf(maxLat, coord.latitude)
            minLng = minOf(minLng, coord.longitude)
            maxLng = maxOf(maxLng, coord.longitude)
        }

        return LatLngBounds(LatLng(minLat, minLng), LatLng(maxLat, maxLng))
    }

    private fun getRoute(start: LatLng, end: LatLng, waypointList: List<LatLng>) {
        getMapInfo(start,end, waypointList){
            val startInfo = it.route.traavoidcaronly[0].summary.start.location
            val startLocation =  LatLng(startInfo[1],startInfo[0])
            val goalInfo = it.route.traavoidcaronly[0].summary.goal.location
            val goalLocation = LatLng(goalInfo[1],goalInfo[0])


            binding.expectedTimeView.text = it.route.traavoidcaronly[0].summary.duration.toString()
//            mNaverMap.moveCamera(CameraUpdate.scrollTo(startLocation))
            Marker().apply {
                position = startLocation
                map = mNaverMap
                icon = MarkerIcons.BLUE
            }

            Marker().apply {
                position = goalLocation
                map = mNaverMap
                icon = MarkerIcons.RED
            }

            val waypoints = it.route.traavoidcaronly[0].summary.waypoints
            if(waypoints != null){
                waypoints.forEach { go ->
                    Marker().apply{
                       position = LatLng(go.location[1],go.location[0])
                        map = mNaverMap
                        icon = MarkerIcons.GREEN
                    }
                }
            }


            // 도보 경로를 지도에 표시
            val pathOverlay = PathOverlay()
            val list : MutableList<LatLng> = mutableListOf()
            list.add(startLocation)
            lateinit var pathLatLng: LatLng
            val pathList = it.route.traavoidcaronly[0].path
            for( i in pathList){
                pathLatLng = LatLng(i[1],i[0])
                list.add(pathLatLng)
            }

            list.add(goalLocation)

            val cameraUpdate = CameraUpdate.fitBounds(calculateBounds(list), 100)

            pathOverlay.coords = list
            pathOverlay.map = mNaverMap

            mNaverMap.moveCamera(cameraUpdate)
        }


    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions,
                grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨
                mNaverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onMapReady(naverMap: NaverMap) {
        mNaverMap = naverMap
        mNaverMap.locationSource = locationSource
        uiSettings = naverMap.uiSettings
        uiSettings.isLocationButtonEnabled = false
        val locationButtonView: LocationButtonView = binding.mylocationView
        locationButtonView.map = mNaverMap





        viewModel.getPlaceList.observe(viewLifecycleOwner) {result ->
            binding.textCourse.text = result.courseName
            Log.d("$$","getPlaceList LiveData 사용")
            lateinit var start : LatLng
            lateinit var end :LatLng
            var waypoint1 :LatLng? = null
            var waypoint2 :LatLng? = null
            var waypoint3 :LatLng? = null
            var waypoint4 :LatLng? = null
            var waypoint5 :LatLng? = null

            when (result.placeList.size) {
                in 2..7 -> {
                    start = LatLng(result.placeList[0].latidute, result.placeList[0].longitude)
                        Log.d("$$","start = $start")
                    end = LatLng(result.placeList.last().latidute, result.placeList.last().longitude)

                    for (i in 1 until min(result.placeList.size - 1, 6)) {
                        val waypoint = LatLng(result.placeList[i].latidute, result.placeList[i].longitude)
                        when (i) {
                            1 -> waypoint1 = waypoint
                            2 -> waypoint2 = waypoint
                            3 -> waypoint3 = waypoint
                            4 -> waypoint4 = waypoint
                            5 -> waypoint5 = waypoint
                        }
                    }
                }
                else -> {
                    // 예외 처리 또는 다른 조건에 따른 로직 추가
                    // 예: throw IllegalArgumentException("Unsupported size: ${result.placeList.size}")
                }
            }
            // waypoint가 없을 경우 빈리스트로 초기화
            val waypointList = mutableListOf<LatLng>()

            if (waypoint1!=null) {
                waypointList.add(waypoint1)
            }
            if (waypoint2!=null) {
                waypointList.add(waypoint2)
            }
            if (waypoint3!=null) {
                waypointList.add(waypoint3)
            }
            if (waypoint4!=null) {
                waypointList.add(waypoint4)
            }
            if (waypoint5!=null) {
                waypointList.add(waypoint5)
            }
            getRoute(start,end ,waypointList)
        }


    }

    private fun getMapInfo(start: LatLng, end: LatLng, waypointList: List<LatLng>, result: (DirectionResponseDTO)->Unit) {
        Log.d("$$","start  :  $start  end  :  $end")
        val service = RequestFactory.create2()
//        val appKey = "f7ToVSBulf2Aj1yZM7FiS8lTT8xjKkyFJ5HEpmi1"
        val apiKeyId = "clurvbfncz"
        val apiKey = "WuVnFkJnFdIt7L03dhCZw7iCyNCeLGtNh3UsrhrI"
//        val data = DataDTO(start.longitude,start.latitude,end.longitude,end.latitude,"출발지","도착지")
//        val version = "1"
//        val format = "json"
//        val request = TMapRouteRequest(start.longitude,start.latitude,0,4,end.longitude,end.latitude,"출발지","도착지",0,"WGS84GEO")
        val start = "${start.longitude},${start.latitude},name=출발지"
        val goal = "${end.longitude},${end.latitude},name=목적지"
        val waypointListString = waypointList.joinToString(separator = "|") { "${it.longitude},${it.latitude}" }
        val option = "traavoidcaronly"
        val callMapInfo : Call<DirectionResponseDTO> = service.directions5(start,goal,waypointListString,option, apiKeyId,apiKey)



        callMapInfo.enqueue(object : Callback<DirectionResponseDTO> {
            override fun onResponse(call: Call<DirectionResponseDTO>, response: Response<DirectionResponseDTO>) {
                Log.d("$$", "response값은.... $response")
                Log.d("$$", "dataBody값은.... ${response.body()}")
                if(response.isSuccessful){
                    Log.d("$$","성공?")
                    val dataBody = response.body()
                    if (dataBody != null) {
                        result.invoke(dataBody)
                    }

                }else{
                    Log.d("$$","에러: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DirectionResponseDTO>, t: Throwable) {
                Log.e("$$", "call $call")
                Log.e("$$","Tmap 요청한 응답 실패!!!")
            }
        })
    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.hideBottomNavigation()
    }

    override fun onPause() {
        super.onPause()
        (activity as? MainActivity)?.showBottomNavigation()
    }
}