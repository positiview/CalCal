package com.example.calcal.subFrag

import android.location.Location
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import com.example.calcal.modelDTO.FeatureCollection
import com.example.calcal.modelDTO.TMapRouteRequest
import com.example.calcal.repository.CourseRepository
import com.example.calcal.repository.CourseRepositoryImpl
import com.example.calcal.retrofit.RequestFactory
import com.example.calcal.util.Resource
import com.example.calcal.viewModel.CourseViewModel
import com.example.calcal.viewModelFactory.CourseViewModelFactory
import com.naver.maps.geometry.LatLng
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

        mapFragment.getMapAsync(this)


        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)



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

        }

        btn_back = binding.btnBack
        btn_back.setOnClickListener {
            NavHostFragment.findNavController(this).navigateUp()
        }





        return binding.root
    }



    private fun getRoute(start: LatLng, end: LatLng, waypointList: List<LatLng>) {
        getTmapInfo(start,end)
       /* val pathData = mNaverMap.findPathData(start, end)
            ?: return // 경로 데이터를 가져오지 못한 경우 종료*/

        // 출발지와 도착지에 마커 추가
        Marker().apply {
            position = start
            map = mNaverMap
            icon = MarkerIcons.BLUE
        }

        Marker().apply {
            position = end
            map = mNaverMap
            icon = MarkerIcons.RED
        }

        waypointList.forEach {
            Marker().apply {
                position = it
                map = mNaverMap
                icon = MarkerIcons.GREEN
            }
        }

        // 도보 경로를 지도에 표시
            val pathOverlay = PathOverlay()
            val list : MutableList<LatLng> = mutableListOf()
            list.add(start)
            list.add(end)

            list.addAll(waypointList)

            pathOverlay.coords = list
            pathOverlay.map = mNaverMap

        // 출발지와 도착지가 모두 표시될 수 있도록 지도의 카메라 이동
//        val cameraUpdate = CameraUpdate.fitBounds(pathData.bounds, 100)
//        mNaverMap.moveCamera(cameraUpdate)
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
        // 위치 추적 모드, 현위치 오버레이와 카메라 좌표가 사용자의 위치를 따라 움직입니다. API나 제스터를 사용시 위치추적모드 해제
//        mNaverMap.locationTrackingMode = LocationTrackingMode.Follow // <-- 에뮬에서는 안되는듯

        var initialPosition = LatLng(35.1798159, 129.0750222) // 부산 시청

        // 위치추적모드가 활성화 될때 이벤트 처리
        /*mNaverMap.addOnLocationChangeListener(object : NaverMap.OnLocationChangeListener {
            override fun onLocationChange(location: Location) {
                initialPosition = LatLng(location.latitude, location.longitude)
                Log.d("$$","onLocationChange 발동!")
                // 내 위치를 설정한 후에 리스너를 제거
                mNaverMap.removeOnLocationChangeListener(this)
            }
        })*/

        val cameraPosition = CameraPosition(initialPosition, 17.0)
        mNaverMap.moveCamera(CameraUpdate.toCameraPosition(cameraPosition))
        mNaverMap.maxZoom = 18.0
        mNaverMap.minZoom = 5.0




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

    private fun getTmapInfo(start: LatLng, end: LatLng) {
        Log.d("$$","start  :  $start  end  :  $end")
        val service = RequestFactory.create4()
        val appKey = "f7ToVSBulf2Aj1yZM7FiS8lTT8xjKkyFJ5HEpmi1"
        val data = DataDTO(start.longitude,start.latitude,end.longitude,end.latitude,"출발지","도착지")
        val version = "1"
        val format = "json"
//        val request = TMapRouteRequest(start.longitude,start.latitude,0,4,end.longitude,end.latitude,"출발지","도착지",0,"WGS84GEO")
        val callTmapInfo : Call<FeatureCollection> = service.pedestrianRoute(version,data,appKey)






        callTmapInfo.enqueue(object : Callback<FeatureCollection> {
            override fun onResponse(call: Call<FeatureCollection>, response: Response<FeatureCollection>) {
                Log.d("$$", "response값은.... $response")
                Log.d("$$", "dataBody값은.... ${response.body()}")
                if(response.isSuccessful){
                    Log.d("$$","성공?")
                    val dataBody = response.body()

                }else{
                    Log.d("$$","에러: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<FeatureCollection>, t: Throwable) {
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