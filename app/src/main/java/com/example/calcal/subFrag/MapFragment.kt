package com.example.calcal.subFrag

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.core.os.postDelayed
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.calcal.MainActivity
import com.example.calcal.R
import com.example.calcal.databinding.FragmentMapBinding
import com.example.calcal.modelDTO.DirectionResponseDTO
import com.example.calcal.modelDTO.RouteAndTimeDTO
import com.example.calcal.repository.CourseRepositoryImpl
import com.example.calcal.repository.MemberRepository
import com.example.calcal.repository.MemberRepositoryImpl
import com.example.calcal.repository.RecordRepository
import com.example.calcal.repository.RecordRepositoryImpl
import com.example.calcal.retrofit.RequestFactory
import com.example.calcal.service.ChronometerService
import com.example.calcal.signlogin.LoginActivity
import com.example.calcal.signlogin.LoginActivity.Companion.PREF_NAME
import com.example.calcal.util.CustomLoading
import com.example.calcal.util.Resource
import com.example.calcal.viewModel.CourseViewModel
import com.example.calcal.viewModel.MemberViewModel
import com.example.calcal.viewModel.RecordViewModel
import com.example.calcal.viewModelFactory.CourseViewModelFactory
import com.example.calcal.viewModelFactory.MemberViewModelFactory
import com.example.calcal.viewModelFactory.RecordViewModelFactory
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraAnimation
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
import com.naver.maps.map.widget.CompassView
import com.naver.maps.map.widget.LocationButtonView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Integer.min
import java.lang.System.currentTimeMillis
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding : FragmentMapBinding
    private lateinit var locationSource: FusedLocationSource
    private lateinit var mNaverMap: NaverMap
    private var locationManager: LocationManager? = null
    private lateinit var uiSettings: UiSettings
    private lateinit var btn_back:ImageView
    private lateinit var lastLocation:LatLng
    private var isCameraMoveExecuted = false
    private val handler = Handler()
    private val touchTimeout = 5000L // 5초
    private var lastTouchTime = 0L
    private var cal :Double = 0.0
    private var goalCal : Double = 0.0
    private var distance = ""
    private var chronometerService: ChronometerService? = null
    //임시몸무게
    private var memberWeight : Int? = 70
    private var memberLength : Int? = null
    private var memberAge : Int? = null
    private var memberGender :String?= null
    private lateinit var courseRepository: CourseRepositoryImpl
    private lateinit var courseViewModelFactory: CourseViewModelFactory
    private lateinit var sharedPreferences: SharedPreferences

    private val courseViewModel: CourseViewModel by activityViewModels() { courseViewModelFactory }

    private val recordRepository: RecordRepository = RecordRepositoryImpl()
    private val recordViewModelFactory = RecordViewModelFactory(recordRepository)
    private val recordViewModel: RecordViewModel by viewModels() { recordViewModelFactory }

    private val memberRepository: MemberRepository = MemberRepositoryImpl()
    private val memberViewModelFactory = MemberViewModelFactory(memberRepository)
    private val memberViewModel: MemberViewModel by viewModels(){memberViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        courseRepository = CourseRepositoryImpl(requireContext())
        courseViewModelFactory = CourseViewModelFactory(courseRepository)
    }
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



        val myRoute: MutableList<LatLng> = mutableListOf()
        var routeAndTimeDTO: MutableList<RouteAndTimeDTO> = mutableListOf()





        binding.apply {
            // 옵션 토글 버튼 수정 필요!! (사용자 편의)
            // 1. 모든 레이아웃 숨기기
            // 2. 예상 경로 숨기기
            btnOption.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked){
                    courseRecode.visibility = View.VISIBLE
                }else{
                    courseRecode.visibility = View.GONE
                }
            }

            selectCourse.setOnClickListener{
                findNavController().navigate(R.id.action_mapFragment_to_searchLocationFragment)
            }



            val onLocationChangeListener = object : NaverMap.OnLocationChangeListener {
                override fun onLocationChange(location: Location) {
                    val elapsedMillis = SystemClock.elapsedRealtime() - chronometer.base
                    Log.d("$$","ChronometerTime : $elapsedMillis")
                    var speed = 0.0
                    val polyline = PolylineOverlay()
                    polyline.color = Color.BLUE
                    polyline.width = 10
                    // 2개의 루트 이상일때 오차 생략하기 계산
                    if(myRoute.size >= 3){
                        // GPS 오차 생략하기
                        val routeSize = myRoute.size
                        val avgLatLng = calculateAverageLatLng(myRoute.last().latitude,myRoute.last().longitude,myRoute[routeSize-2].latitude,myRoute[routeSize-2].longitude)
                        val diff = haversine(avgLatLng.latitude, avgLatLng.longitude, location.latitude, location.longitude)
                        Log.d("$$","diff 값 : $diff")

                        val range = haversine(location.latitude,location.longitude,myRoute.last().latitude,myRoute.last().longitude)

                        // 오차 15m 이내로 제한
                        if ((diff/1000) <=20.0) {
                            myRoute.add(LatLng(location.latitude, location.longitude))
                            speed = range /(elapsedMillis.toDouble() - routeAndTimeDTO.last().recordTime)*3.6 //거리/시간
                            binding.showSpeed.text = speed.toInt().toString()
                            polyline.coords = myRoute
                            polyline.map = mNaverMap
                        }  else{
                            // 15m 이상이면 데이터를 저장하지 않는다.
                        }
                    }else if(myRoute.size == 2){
                        // 줌은 한번만 활성화 된다.
                        if (!isCameraMoveExecuted) {
                            Log.d("$$","zoom활성화!!!!")
                            val zoomUpdate = CameraUpdate.zoomTo(17.0).animate(CameraAnimation.Easing, 1000)
                            mNaverMap.moveCamera(zoomUpdate)

                            mNaverMap.addOnCameraIdleListener {
                                mNaverMap.locationTrackingMode = LocationTrackingMode.Follow
                            }
                            isCameraMoveExecuted = true
                        }
                        myRoute.add(LatLng(location.latitude, location.longitude))
                        polyline.coords = myRoute
                        polyline.map = mNaverMap
                    }else{
                        Marker().apply {
                            position = LatLng(location.latitude, location.longitude)
                            map = mNaverMap
                            icon = MarkerIcons.LIGHTBLUE
                            captionText = "출발"
                            captionHaloColor = Color.rgb(200,255,200)
                            captionMinZoom = 12.0
                        }

                        myRoute.add(LatLng(location.latitude, location.longitude))
                    }
                    // 지도맵에 이동한 루트를 그려준다.


//                    // 기록 저장

                    routeAndTimeDTO.add(RouteAndTimeDTO(
                        longitude = location.longitude,
                        latitude = location.latitude ,
                        recordTime = elapsedMillis.toDouble()
                    ))

                    cal = calculateCalories(elapsedMillis.toDouble(),speed)

                    distance = calculateTotalDistance(myRoute) // 전체 거리

                    binding.calorieTv.text = cal.toInt().toString()
                    binding.distanceTv.text = distance
                }
            }

            btnStart.setOnClickListener {
                 //위치 추적 모드, 현위치 오버레이와 카메라 좌표가 사용자의 위치를 따라 움직입니다. API나 제스터를 사용시 위치추적모드 해제
                mNaverMap.locationTrackingMode = LocationTrackingMode.Follow // <-- 에뮬에서는 안되는듯


                // 위치추적모드가 활성화에 의한 영향으로 위치가 변할때 이벤트 처리
                mNaverMap.addOnLocationChangeListener(onLocationChangeListener)


                // 수정 필요
                mNaverMap.setOnMapClickListener { _, _ ->
                    // 지도 터치시 현재 시간 업데이트
                    lastTouchTime = currentTimeMillis()

                    // 터치가 있으면 5초 뒤에 다시 확인하는 핸들러 콜백 제거
                    handler.removeCallbacksAndMessages(null)



                    // 터치가 있으면 5초 뒤에 다시 확인하는 핸들러 콜백 추가
                    handler.postDelayed({
                        if (currentTimeMillis() - lastTouchTime >= touchTimeout) {

                            // 5초 이상 터치가 없었을 때, LocationTrackingMode.Follow 다시 활성화
                            mNaverMap.locationTrackingMode = LocationTrackingMode.Follow
                            isCameraMoveExecuted = false
                        }
                    }, touchTimeout)
                }


                chronometer.base = SystemClock.elapsedRealtime()
                chronometer.start()

                textViewMap.text = textCourse.text
                stopwatchChronometer.visibility = View.VISIBLE
                singleLayout.visibility = View.GONE

                /*val serviceIntent = Intent(requireContext(), ChronometerService::class.java)
                startForegroundService(requireContext(),serviceIntent)*/


            }
            var elapsedTime: Long = 0
            // 중지/재개 버튼 (토글 버튼 추천)
            btnStop.setOnCheckedChangeListener{ _, isChecked ->
                if(isChecked){
                    elapsedTime = SystemClock.elapsedRealtime() - chronometer.base
                    chronometer.stop()
                    mNaverMap.removeOnLocationChangeListener(onLocationChangeListener)
                }else{
                    chronometer.base = SystemClock.elapsedRealtime() - elapsedTime
                    chronometer.start()
                    mNaverMap.addOnLocationChangeListener(onLocationChangeListener)
                }
            }

            // 완료버튼 + 저장
            btnComplete.setOnClickListener {
                Toast.makeText(requireContext(),"기록을 종료합니다",Toast.LENGTH_SHORT).show()
                chronometer.stop()

                textViewMap.text = "MAP"
                mNaverMap.removeOnLocationChangeListener(onLocationChangeListener)


                val txt = binding.textCourse.text.toString()
                Log.d("$$","rat = $routeAndTimeDTO , courseName = $txt")

                sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                val storedEmail = sharedPreferences.getString(LoginActivity.KEY_EMAIL, "")
                if (storedEmail != null) {
                    recordViewModel.saveRecord(routeAndTimeDTO,txt,storedEmail,goalCal,cal,distance)
                    recordViewModel.successfulSave.observe(viewLifecycleOwner){
                        when(it){
                            is Resource.Loading -> {

                            }
                            is Resource.Success ->{

                                Toast.makeText(requireContext(),it.data,Toast.LENGTH_SHORT).show()
                                handler.postDelayed({
                                    stopwatchChronometer.visibility = View.GONE
                                    singleLayout.visibility = View.VISIBLE
                                    findNavController().navigate(R.id.action_mapFragment_to_historyFragment)

                                },3000)
                            }
                            is Resource.Error ->{

                                Toast.makeText(requireContext(),it.string,Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                // 위 두개의 코드를 어떻게 처리해야할 지 고민해봐야함
            }

        }

        btn_back = binding.btnBack
        btn_back.setOnClickListener {
            NavHostFragment.findNavController(this).navigateUp()
        }


        return view
    }

    private fun calculateAverageLatLng(latitude: Double, longitude: Double, latitude1: Double, longitude1: Double): LatLng {
        val avgLat = (latitude + longitude) / 2
        val avgLon = (latitude1 + longitude1) / 2
        return LatLng(avgLat, avgLon)
    }

    fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0 // 지구 반경 (단위: km)

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return R * c
    }
    private fun formatDistance(distance: Double): String {
        return if (distance >= 1000) {
            "${"%.2f".format(distance / 1000)} km"
        } else {
            "${"%.2f".format(distance)} m"
        }
    }
    fun calculateTotalDistance(coordinates: MutableList<LatLng>): String {
        var totalDistance = 0.0

        // 리스트의 첫 번째 좌표부터 마지막 좌표까지 반복
        for (i in 0 until coordinates.size - 1) {
            val current = coordinates[i]
            val next = coordinates[i + 1]

            // 두 지점 간의 거리를 계산하여 누적
            val distance =
                haversine(current.latitude, current.longitude, next.latitude, next.longitude)
            totalDistance += distance

        }
        return formatDistance(totalDistance)
    }
    private fun calculateCalories(elapsedMillis : Double, speed: Double): Double {
        val cal1 = (memberWeight?.toDouble() ?: return 0.0 )*elapsedMillis*3.5

        val met = 0.09* speed.pow(2.0) - 0.27*speed + 2.03
        val minute = elapsedMillis/60000
        //ex)(강도*3.5*0.001*체중)*5*운동시간(min)
        val cal = (met*3.5*0.001* memberWeight!!)*5*minute
        Log.d("$$","elapsedMilliselapsedMillis : ${minute}")
        // BMR 계산법 일일 기초대사량
       /* var bmr : Double
        if(memberGender == "male"){
            bmr = 66.5 + (13.75 * (memberWeight?.toDouble() ?: return)) + (5.003 * (memberLength?.toDouble()
                ?: return)) - (6.75 * (memberAge?.toDouble() ?: return))
        }else if(memberGender == "female"){
            bmr = 655.1 +(9.563 * (memberWeight?.toDouble() ?: return)) + (1.850 * (memberLength?.toDouble()
                ?: return)) - (4.676 * (memberAge?.toDouble() ?: return))
        }else{
            Toast.makeText(requireContext(),"성별 정보를 입력해주세요", Toast.LENGTH_SHORT).show()
        }
        val calToday = (bmr + ((memberWeight?.toDouble() ?: return)*elapsedMillis*3.5))/24*/

        return cal
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
            Log.d("$$","ititititit:$it")

            val expectedDistanceTextView = binding.expectedDistance
            val expectedTimeTextView = binding.expectedTimeView
            val calorieTextView = binding.calorieView
            val walkCountTextView = binding.walkCountView

            val distance = it.route.traavoidcaronly[0].summary.distance // 예상 거리 (m)
            val durationInSeconds = it.route.traavoidcaronly[0].summary.duration // 예상 시간 (s)
            Log.d("$$","durationInSeconds:$durationInSeconds")
            val minute = (durationInSeconds / 60)*0.001 // 분
            Log.d("$$","minute:$minute")
            val roundedValue = Math.round(minute).toInt()
            val hours = roundedValue / 60 // 시간
            val minutes = roundedValue % 60 // 분
            val stepsPerDistance = 0.8 // 걸음당 거리 (미터 단위)
            val expectedSteps = (distance / stepsPerDistance).toInt()

            //ex)(강도*3.5*0.001*체중)*5*운동시간(min)
//            val cal = (3*3.5*0.001* memberWeight!!)*5*minute

            val calorie = (3*3.5*0.001* memberWeight!!)*5*roundedValue // 예상 칼로리
            val calories = Math.round(calorie).toInt()
//            val walkCount = it.route.traavoidcaronly[0].summary.walkCount // 예상 걸음수

            val formattedTime = if (hours > 0) {
                String.format("%d시간 %d분", hours, minutes)
            } else {
                String.format("%d분", minutes)
            }

            expectedTimeTextView.text = formattedTime // 예상 시간 설정
            expectedDistanceTextView.text = distance.toString()+"m" // 예상 거리 설정
            calorieTextView.text = calories.toString()+"Kcal" // 예상 칼로리 설정
            walkCountTextView.text = expectedSteps.toString()+"걸음" // 예상 걸음수 설정


//            binding.expectedTimeView.text = it.route.traavoidcaronly[0].summary.duration.toString()//예상거리
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
    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        Log.d("$$", "onMapReady 실행")


        mNaverMap = naverMap
        mNaverMap.locationSource = locationSource
        uiSettings = naverMap.uiSettings
        uiSettings.isLocationButtonEnabled = false
        uiSettings.isRotateGesturesEnabled = false
        uiSettings.isTiltGesturesEnabled = false
        // 내위치 찾는 버튼
        val locationButtonView: LocationButtonView = binding.mylocationView
        locationButtonView.map = mNaverMap



        mNaverMap.maxZoom = 18.0
        mNaverMap.minZoom = 5.0

        /* mNaverMap.setOnMapClickListener { _, _ ->
            // 지도 터치시 현재 시간 업데이트
            lastTouchTime = currentTimeMillis()

            // 터치가 있으면 10초 뒤에 다시 확인하는 핸들러 콜백 제거
            handler.removeCallbacksAndMessages(null) // 버그 체크 필요
        }*/

        courseViewModel.getPlaceList.observe(viewLifecycleOwner) { result ->
            Log.d("$$", "getPlaceList LiveData 사용")
            lateinit var start: LatLng
            lateinit var end: LatLng
            var waypoint1: LatLng? = null
            var waypoint2: LatLng? = null
            var waypoint3: LatLng? = null
            var waypoint4: LatLng? = null
            var waypoint5: LatLng? = null



            if (result.placeList != null) {
                binding.textCourse.text = result.courseName
                when (result.placeList.size) {
                    in 2..7 -> {
                        start = LatLng(result.placeList[0].latidute, result.placeList[0].longitude)
                        Log.d("$$", "start = $start")
                        end = LatLng(
                            result.placeList.last().latidute,
                            result.placeList.last().longitude
                        )

                        for (i in 1 until min(result.placeList.size - 1, 6)) {
                            val waypoint =
                                LatLng(result.placeList[i].latidute, result.placeList[i].longitude)
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
                        Log.d("$$", "Unsupported size: ${result.placeList.size}")
                        // 예: throw IllegalArgumentException("Unsupported size: ${result.placeList.size}")
                    }
                }
                // waypoint가 없을 경우 빈리스트로 초기화
                val waypointList = mutableListOf<LatLng>()

                if (waypoint1 != null) {
                    waypointList.add(waypoint1)
                }
                if (waypoint2 != null) {
                    waypointList.add(waypoint2)
                }
                if (waypoint3 != null) {
                    waypointList.add(waypoint3)
                }
                if (waypoint4 != null) {
                    waypointList.add(waypoint4)
                }
                if (waypoint5 != null) {
                    waypointList.add(waypoint5)
                }
                getRoute(start,end ,waypointList)
            }else{
                Toast.makeText(requireContext(),"리스트를 불러오는데 실패했습니다.",Toast.LENGTH_SHORT).show()
            }
            memberViewModel.getMemberInfo.observe(viewLifecycleOwner){
                if(it is Resource.Success){
                    memberWeight = it.data.weight
                }
            }
            val cal = memberWeight?.let { calculateCalories(it.toDouble(), 4.0) }
            if (cal != null) {
                binding.calorieView.text = cal.toInt().toString()
            }
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
        const val LOCATION_PERMISSION_REQUEST_CODE = 1000
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