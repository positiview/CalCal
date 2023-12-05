package com.example.calcal.subFrag


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calcal.MainActivity
import com.example.calcal.R
import com.example.calcal.adapter.CourseListAdapter
import com.example.calcal.databinding.FragmentSearchLocationBinding
import com.example.calcal.modelDTO.Result
import com.example.calcal.modelDTO.ReverseGeocodingResponseDTO
import com.example.calcal.modelDTO.CoordinateDTO
import com.example.calcal.modelDTO.CourseListDTO
import com.example.calcal.modelDTO.ItemDTO
import com.example.calcal.repository.CourseRepository
import com.example.calcal.repository.CourseRepositoryImpl
import com.example.calcal.retrofit.RequestFactory
import com.example.calcal.signlogin.LoginActivity
import com.example.calcal.util.Resource
import com.example.calcal.viewModel.CourseViewModel
import com.example.calcal.viewModelFactory.CourseViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SearchLocationFragment:Fragment() {
    private lateinit var binding : FragmentSearchLocationBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var courseListAdapter: CourseListAdapter // DB에서 저장한 코스 리스트를 가져온다
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient //자동으로 gps값을 받아온다.
    private lateinit var locationCallback: LocationCallback //gps응답 값을 가져온다.
    private var location_departure: CoordinateDTO? = null
    private var myLocation: Result? = null
    private var location_waypoint1: CoordinateDTO? = null
    private var location_waypoint2: CoordinateDTO? = null
    private var location_waypoint3: CoordinateDTO? = null
    private var location_waypoint4: CoordinateDTO? = null
    private var location_waypoint5: CoordinateDTO? = null
    private var waypointCount = 0
    private var placeList = listOf<CoordinateDTO>()

    private var location_arrival: CoordinateDTO? = null
    private var myArea:String = ""
    private var selectedPlaceOrNot: Boolean = false

    private lateinit var courseRepository: CourseRepositoryImpl
    private lateinit var courseViewModelFactory: CourseViewModelFactory


//    private val viewModel: CourseViewModel by lazy {
//        ViewModelProvider(this, courseViewModelFactory)[CourseViewModel::class.java]
//    }
    private val viewModel: CourseViewModel by activityViewModels() { courseViewModelFactory }
    private lateinit var directSearchMapFragment: DirectSearchMapFragment

    // DirectSearchMapFragment 인스턴스를 설정합니다.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        directSearchMapFragment = DirectSearchMapFragment()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        courseRepository = CourseRepositoryImpl(requireContext())
        courseViewModelFactory = CourseViewModelFactory(courseRepository)
    }

    // DirectSearchMapFragment의 인스턴스를 반환하는 메서드
    fun getDirectSearchMapFragmentInstance(): DirectSearchMapFragment {
        return directSearchMapFragment
    }

    // DirectSearchMapFragment에 값을 업데이트하는 메서드
    fun updateDirectSearchMapFragment(coordinateDTO: CoordinateDTO?) {
        directSearchMapFragment.setLocationAndAddress(coordinateDTO)
        directSearchMapFragment.setCurrentLocation(LatLng(coordinateDTO?.latidute ?: 0.0, coordinateDTO?.longitude ?: 0.0))
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchLocationBinding.inflate(inflater,container,false)
        val view = binding.root


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())


        Log.d("$$", "viewmodel.getPlaceList : ${viewModel.getPlaceList}")
        // selectedPlaceOrNot 은 처음SearchLocationFragment에 왔을때 제외하고 mapFragment 갔다오면 선택했던 PlaceList들의 내용을 미리 표시
        if(selectedPlaceOrNot){
            viewModel.getPlaceList.observe(viewLifecycleOwner){
                when(it.placeList.size){
                    in 2 .. 7 ->{
                        binding.courseEdit.setText(it.courseName)
                        binding.departure.text = it.placeList[0].addressName
                        location_departure = CoordinateDTO(0,it.placeList[0].addressName,it.placeList[0].longitude,it.placeList[0].latidute)
                        binding.arrival.text = it.placeList.last().addressName
                        location_arrival = CoordinateDTO(0,it.placeList.last().addressName,it.placeList.last().longitude,it.placeList.last().latidute)

                        for(i in 1 until it.placeList.size-1){
                            val addressName = it.placeList[i].addressName
                            val coordinate = CoordinateDTO(0,it.placeList[i].addressName,it.placeList[i].longitude,it.placeList[i].latidute)
                            when(i){
                                1 -> {
                                    binding.waypoint1Text.text = addressName
                                    location_waypoint1 = coordinate
                                }
                                2 -> {
                                    binding.waypoint2Text.text = addressName
                                    location_waypoint2 = coordinate
                                }
                                3 -> {
                                    binding.waypoint3Text.text = addressName
                                    location_waypoint3 = coordinate
                                }
                                4 -> {
                                    binding.waypoint4Text.text = addressName
                                    location_waypoint4 = coordinate
                                }
                                5 -> {
                                    binding.waypoint5Text.text = addressName
                                    location_waypoint5 = coordinate
                                }
                            }
                        }

                    }
                    else->{

                    }
                }


            }
        }else{
            checkGrantAndGetMyLocation()
            // 출발지를 내위치로

        }
        courseConfirmBtnEnableCheck()

       /* val layoutManager = GridLayoutManager(requireContext(), 1)
        binding.selectedLocation.layoutManager = layoutManager*/
        val recyclerView = binding.courseList
        recyclerView.layoutManager = LinearLayoutManager(context)

        sharedPreferences = requireActivity().getSharedPreferences(LoginActivity.PREF_NAME, Context.MODE_PRIVATE)
        val userEmail =  sharedPreferences.getString(LoginActivity.KEY_EMAIL, "")
        if (userEmail != null) {
            // 코스 데이터 불러오기

            viewModel.getCourse(userEmail)
        }

        // 코스 목록 관찰
        viewModel.getCourse.observe(viewLifecycleOwner) { result ->
            Log.d("SearchLocationFragment", "Course observe result: $result")
            when (result) {
                is Resource.Loading -> {
                    Log.d("SearchLocationFragment", "Course loading...")
                    // 로딩 중 처리
                }
                is Resource.Success -> {
                    Log.d("SearchLocationFragment", "Course data: ${result.data}")
                    // 데이터 로드 성공 처리
                    Log.d("$$","getCourse값 : ${it.data}")
                    if (it.data != null) {
                        courseListAdapter = CourseListAdapter(it.data, this)
                        recyclerView.adapter = courseListAdapter
                    }
                }
                is Resource.Error -> {
                    // 에러 처리
                }
            }
        }



        // 버튼들
        binding.apply{
            // 출발 도착 변경 스위치
            btnSwitch.setOnClickListener {
                val temp = departure.text
                val temp2 = location_departure
                departure.text = arrival.text
                location_departure = location_arrival
                arrival.text = temp
                location_arrival = temp2

            }
            // 왕복 입력 스위치 //수정 필요
            btnRoundTrip.setOnClickListener {
                if(arrival.text != null && arrival.text != departure.text){

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
                    when(newWaypoint){
                        waypoint1Text -> location_waypoint1 = location_arrival
                        waypoint2Text -> location_waypoint2 = location_arrival
                        waypoint3Text -> location_waypoint3 = location_arrival
                        waypoint4Text -> location_waypoint4 = location_arrival
                        waypoint5Text -> location_waypoint5 = location_arrival

                    }
                    arrival.text = departure.text
                    location_arrival= location_departure
                }else if(arrival.text == null){

                    arrival.text = departure.text
                    location_arrival= location_departure
                }

                updateAddWaypointVisibility(waypointCount)

            }


            // 경유지 추가 버튼
            addWaypoint.setOnClickListener {
                waypointCount++
                updateAddWaypointVisibility(waypointCount)
                val waypoints = listOf(waypoint1, waypoint2, waypoint3, waypoint4, waypoint5)
                waypoints.firstOrNull { !it.isVisible }?.visibility = View.VISIBLE
                // 웨이포인트 1번 부터 5번까지 리스트중 첫번째로 visible되어 있지 않은 waypoint가 있는지 확인하는 코드.. 확인 도중 있다면 보이게 하고 종료 없으면 null 로 반환후 종료

            }

            // 경유지1 취소 버튼
            waypoint1Cancel.setOnClickListener {
                waypoint1.visibility = View.GONE
                waypoint1Text.hint = "[경유지]"
                waypoint1Text.text = ""
                location_waypoint1 = null
                waypointCount--
                updateAddWaypointVisibility(waypointCount)
            }
            // 경유지2 취소 버튼
            waypoint2Cancel.setOnClickListener {
                waypoint2.visibility = View.GONE
                waypoint2Text.hint = "[경유지]"
                waypoint2Text.text = ""
                location_waypoint2 = null
                waypointCount--
                updateAddWaypointVisibility(waypointCount)
            }
            // 경유지3 취소 버튼
            waypoint3Cancel.setOnClickListener {
                waypoint3.visibility = View.GONE
                waypoint3Text.hint = "[경유지]"
                waypoint3Text.text = ""
                location_waypoint3 = null
                waypointCount--
                updateAddWaypointVisibility(waypointCount)
            }
            // 경유지4 취소 버튼
            waypoint4Cancel.setOnClickListener {
                waypoint4.visibility = View.GONE
                waypoint4Text.hint = "[경유지]"
                waypoint4Text.text = ""
                location_waypoint4 = null
                waypointCount--
                updateAddWaypointVisibility(waypointCount)

            }
            // 경유지5 취소 버튼
            waypoint5Cancel.setOnClickListener {
                waypoint5.visibility = View.GONE
                waypoint5Text.hint = "[경유지]"
                waypoint5Text.text = ""
                location_waypoint5 = null
                waypointCount--
                updateAddWaypointVisibility(waypointCount)
            }

            // 중요함!! 출발 경유지 도착 주소 선택 시 값 저장 --> 해당 SearchAddressDialog 열림
            val waypoints = arrayOf(departure,waypoint1Text, waypoint2Text, waypoint3Text, waypoint4Text, waypoint5Text,arrival)
            // 위치 검색을 위한 DIALOG 열림


            waypoints.forEachIndexed() {index, waypoint ->
                waypoint.setOnClickListener{
                    openSearchAddressDialog(waypoint,index)

                }
            }

            // 코스 확인 및 저장 버튼 !!
            courseConfirm.setOnClickListener {
                updatePlaceList()

                fun getCurrentDateTimeAsString(): String {
                    val dateFormat = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
                    return dateFormat.format(Date())
                }

                val current = getCurrentDateTimeAsString()

                val courseName = binding.courseEdit.text.toString().takeIf { it.isNotBlank() } ?: "코스 $current"
                Log.d("$$","저장 버튼 누름 / 코스이름 $courseName placeList = $placeList")
                selectedPlaceOrNot = true
                val sharedPreferences =
                    context?.getSharedPreferences(LoginActivity.PREF_NAME, Context.MODE_PRIVATE)
                val email = sharedPreferences?.getString(LoginActivity.KEY_EMAIL, "")
                if (email != null) {
                    viewModel.saveCourse(email,courseName,placeList)
                }
                findNavController().navigate(R.id.action_searchlocationFragment_to_mapFragment)
            }
        }
        return view
    }

    // placeList 갱신
    private fun updatePlaceList() {
        val placeList = mutableListOf<CoordinateDTO>()
        if(location_departure != null){
            placeList.add(location_departure!!)
        }
        if(location_waypoint1 != null){
            placeList.add(location_waypoint1!!)
        }
        if(location_waypoint2 != null){
            placeList.add(location_waypoint2!!)
        }
        if(location_waypoint3 != null){
            placeList.add(location_waypoint3!!)
        }
        if(location_waypoint4 != null){
            placeList.add(location_waypoint4!!)
        }
        if(location_waypoint5 != null){
            placeList.add(location_waypoint5!!)
        }
        if(location_arrival != null){
            placeList.add(location_arrival!!)
        }
        this.placeList = placeList
    }


    // 출발 도착 주소 설정시, 확인 버튼 활성화
    private fun courseConfirmBtnEnableCheck() {
        binding.courseConfirm.isEnabled = location_departure !=null && location_arrival !=null && location_departure != location_arrival
        if(location_departure !=null && location_arrival !=null && location_departure == location_arrival && waypointCount == 0){
            Toast.makeText(requireContext(),"출발지와 목적지가 같을때 최소 1개의 추가 경유지가 필요합니다.",Toast.LENGTH_SHORT).show()
        }

    }


    // 저장된 리스트 목록을 클릭하면 동작...
    fun onItemClick(courseList: CourseListDTO){
        Log.d("$$","onItemClick 동작 courseList : $courseList")
        viewModel.getPlaceList(courseList)
        findNavController().navigate(R.id.action_searchlocationFragment_to_mapFragment) // 추후 추가 작업 및 수정 필요 할수도..?

    }

    // 지역 검색을 위한 다이아로그 활성화
    private fun openSearchAddressDialog(textView: TextView, index: Int) {
        val searchAddressDialog = SearchAddressDialog(myArea)
        searchAddressDialog.setWaypointTextView(textView)
        searchAddressDialog.setOnItemClickListener(object : SearchAddressDialog.OnItemClickListener {
            override fun onItemClicked(itemDTO: ItemDTO) {
                Log.d("$$","onItemClicked 설정 textView = $textView , itemDTO = $itemDTO")
                textView.text = itemDTO.title
                val coords = CoordinateDTO(0,longitude = itemDTO.mapx.toDouble()/10000000, latidute = itemDTO.mapy.toDouble()/10000000, addressName = itemDTO.roadAddress)
                coordinateData(index,coords)
                courseConfirmBtnEnableCheck()
                updatePlaceList()
                Log.d("$$","33 departure = $location_departure // waypoint1 = $location_waypoint1 // waypoint2 = $location_waypoint2 // arrival = $location_arrival")
            }

            override fun onMyLocationClicked() {


                getMyLocation {
                    if(it!=null){

                        textView.text = "[내 위치] ${it.region.area1.name} ${it.region.area2.name} ${it.region.area3.name} ${it.region.area4.name}".trim()
                        val coords = CoordinateDTO(0,longitude = it.region.area4.coords.center.x, latidute = it.region.area4.coords.center.y, addressName = textView.text.toString())
                        coordinateData(index,coords)
                        courseConfirmBtnEnableCheck()
                        updatePlaceList()
                        Log.d("$$","44 departure = $location_departure // waypoint1 = $location_waypoint1 // waypoint2 = $location_waypoint2 // arrival = $location_arrival")
                    }
                }
            }

            override fun onMapClicked(coordinateDTO: CoordinateDTO?, locations: LatLng) { // 선택한 위치 정보(주소와 좌표)와 내위치 좌표

                val fragment = DirectSearchMapFragment()

                // 선택된 주소이름 위치정보를 전달
                fragment.setLocationAndAddress(coordinateDTO)

                // 현재 위치 정보를 전달
                fragment.setCurrentLocation(locations)
                courseConfirmBtnEnableCheck()
                updatePlaceList()

                fragment.show(parentFragmentManager, "DirectSearchMapFragment")

            }

        })


        val clickedAddress =  when(index){
            0 -> location_departure
            1 -> location_waypoint1
            2 -> location_waypoint2
            3 -> location_waypoint3
            4 -> location_waypoint4
            5 -> location_waypoint5
            6 -> location_arrival
            else -> {
                null
            }
        }


        searchAddressDialog.setClickedTextView(textView,clickedAddress)

        searchAddressDialog.show(childFragmentManager, SearchAddressDialog.TAG)

    }

    // 해당 좌표 저장
    private fun coordinateData(index: Int, coordinateDTO: CoordinateDTO) {

        when(index){
            0 ->{
                location_departure = coordinateDTO
            }
            1->{
                location_waypoint1 = coordinateDTO
            }
            2->{
                location_waypoint2 = coordinateDTO
            }
            3->{
                location_waypoint3 = coordinateDTO
            }
            4->{
                location_waypoint4 = coordinateDTO
            }
            5->{
                location_waypoint5 = coordinateDTO
            }
            6->{
                location_arrival = coordinateDTO
            }
        }
    }

    private fun updateAddWaypointVisibility(waypointCount:Int) {
        binding.addWaypoint.visibility = if (waypointCount < 5) View.VISIBLE else View.GONE    }



    // 내위치 권한 및 정보 가져오기
    private fun checkGrantAndGetMyLocation() {

        val locationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // 권한이 허용되면 위치 정보 가져오기
                getMyLocation(){
                    if(it != null){

                        myArea = it.region.area2.name // 내 지역 데이터 저장

                        val actualAddress = "${it.region.area1.name} ${it.region.area2.name} ${it.region.area3.name} ${it.region.area4.name}".trim()

                        binding.departure.text = "[내 위치] $actualAddress"

                    }else{
                        Toast.makeText(requireContext(),"내 위치를 찾을 수 없습니다.",Toast.LENGTH_SHORT).show()
                    }



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
                if(it != null){

                    myArea = it.region.area2.name // 내 지역 데이터 저장

                    val actualAddress = "${it.region.area1.name} ${it.region.area2.name} ${it.region.area3.name} ${it.region.area4.name}".trim()
                    binding.departure.text = "[내 위치] $actualAddress"
                    Log.d("$$","00 departure = $location_departure // waypoint1 = $location_waypoint1 // waypoint2 = $location_waypoint2 // arrival = $location_arrival")

                }else{
                    Toast.makeText(requireContext(),"내 위치를 찾을 수 없습니다.",Toast.LENGTH_SHORT).show()
                }

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
                        val ad = address.firstOrNull()
                        if(ad !=null){
                            val actualAddress = "${ad.region.area1.name} ${ad.region.area2.name} ${ad.region.area3.name} ${ad.region.area4.name}".trim()
                            location_departure = CoordinateDTO(0, longitude = location.longitude, latidute = location.latitude,
                                addressName = actualAddress
                            )
                        }
                        callback(address.firstOrNull())

                    }
                } ?: run {
                    // 마지막으로 알려진 위치가 없는 경우 처리
                    Log.d("$$", "마지막으로 알려진 위치가 없습니다.")
                    callback(null)

                }
            }

    }

    // 지역검색 API
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
                        Log.d("$$","내 위치 주소 resultList >> $resultList")

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
    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.hideBottomNavigation()
    }

    override fun onPause() {
        super.onPause()
        (activity as? MainActivity)?.showBottomNavigation()
    }

}


