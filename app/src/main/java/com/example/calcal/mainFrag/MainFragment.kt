package com.example.calcal.mainFrag

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.calcal.R
import com.example.calcal.databinding.FragmentMainBinding
import com.example.calcal.modelDTO.RouteAndTimeDTO
import com.example.calcal.modelDTO.RouteRecordDTO
import com.example.calcal.modelDTO.TestDTO
import com.example.calcal.repository.MemberRepositoryImpl
import com.example.calcal.repository.RecordRepository
import com.example.calcal.repository.RecordRepositoryImpl
import com.example.calcal.retrofit.RequestFactory
import com.example.calcal.signlogin.GenderActivity
import com.example.calcal.signlogin.LoginActivity
import com.example.calcal.signlogin.LoginActivity.Companion.PREF_NAME
import com.example.calcal.util.LatLngBoundsCalculator.Companion.calculateBounds
import com.example.calcal.util.Resource
import com.example.calcal.viewModel.MemberViewModel
import com.example.calcal.viewModel.RecordViewModel
import com.example.calcal.viewModelFactory.MemberViewModelFactory
import com.example.calcal.viewModelFactory.RecordViewModelFactory
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.PathOverlay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mNaverMap: NaverMap
    private val recordRepository: RecordRepository = RecordRepositoryImpl()
    private val recordViewModelFactory = RecordViewModelFactory(recordRepository)
    private val recordViewModel: RecordViewModel by viewModels() { recordViewModelFactory }
    private lateinit var binding: FragmentMainBinding
    private val apiService = RequestFactory.create()
    private lateinit var memberViewModel: MemberViewModel
    private lateinit var sharedPreferences: SharedPreferences
    //    private lateinit var btn_alarm : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = MemberRepositoryImpl()
        memberViewModel = ViewModelProvider(this, MemberViewModelFactory(repository))[MemberViewModel::class.java]
        sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)


        val userEmail =  sharedPreferences.getString(LoginActivity.KEY_EMAIL, "")
        if (userEmail != null) {
            memberViewModel.getMemberInfo(userEmail)
            recordViewModel.getRecord(userEmail)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false) // 뷰 바인딩 초기화

        val options = NaverMapOptions()
            .mapType(NaverMap.MapType.Terrain)
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance(options).also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)

        val overlay = binding.overlay
        overlay.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_historyFragment)
             }

        binding.btnWalking.setOnClickListener{
            findNavController().navigate(R.id.action_mainFragment_to_searchLocationFragment)
        }

        /*binding.mapMain.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_historyFragment)
        }*/
        binding.btnDaycount.setOnClickListener{
            findNavController().navigate(R.id.navi_calendar)
        }
        binding.btnKgcheck.setOnClickListener{
            findNavController().navigate(R.id.action_mainFragment_to_modifyFragment)
        }

        binding.btnExerciseinfo.setOnClickListener{
            findNavController().navigate(R.id.action_mainFragment_to_exerciseInfoFragment)
        }
        binding.btnExercisestart.setOnClickListener{
            findNavController().navigate(R.id.action_mainFragment_to_exercisestartFragment)
        }



        return binding.root
    }

    override fun onMapReady(p0: NaverMap) {
        mNaverMap = p0
        mNaverMap.maxZoom = 19.0
        val uiSettings = mNaverMap.uiSettings
        uiSettings.isZoomControlEnabled = false
        uiSettings.isScaleBarEnabled = false
        recordViewModel.getRecord.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    val lastRecord = resource.data?.lastOrNull()
                    if (lastRecord != null) {
                        binding.messageHidden.visibility = View.GONE
                        binding.map.visibility = View.VISIBLE
                        binding.overlay.visibility =View.VISIBLE
                        val ratList: List<RouteAndTimeDTO> = lastRecord.ratList
                        val coords: List<LatLng> = ratList.map { rat -> LatLng(rat.latitude, rat.longitude) }
                        val cameraUpdate = CameraUpdate.fitBounds(calculateBounds(coords), 20)
                        val path = PathOverlay()
                        path.coords = coords
                        path.color = Color.GREEN
                        path.map = mNaverMap
                        mNaverMap.moveCamera(cameraUpdate)
                    } else {
                        binding.messageHidden.text = "운동한 기록이 없어요"
                        binding.messageHidden.visibility = View.VISIBLE
                        binding.map.visibility =View.GONE
                        binding.overlay.visibility =View.GONE

                    }
                }
                is Resource.Error -> {
                    binding.messageHidden.text = "데이터 로딩 실패"
                    binding.messageHidden.visibility = View.VISIBLE
                    binding.map.visibility =View.GONE
                    binding.overlay.visibility =View.GONE
                }
                else ->{

                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        memberViewModel.getMemberInfo.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    val memberDTO = resource.data
                    if (memberDTO?.weight == 0 || memberDTO?.length == 0 || memberDTO?.age == 0 ) {
                        AlertDialog.Builder(context).apply {
                            setTitle("정보 누락")
                            setMessage("정확한 계산을 위해 성별,몸무게, 키, 나이를 입력해주세요.")
                            setPositiveButton("확인") { dialog, which ->
                                val intent = Intent(context, GenderActivity::class.java)
                                intent.putExtra("memberDTO", memberDTO)
                                startActivity(intent)
                            }
                            show()
                        }
                    }
                }
                is Resource.Error -> {
                    // 에러 메시지를 사용자에게 보여주거나, 데이터를 찾지 못했을 때의 처리
                }
                else -> {}
            }

        }



        binding.btnGraph.setOnClickListener{
            findNavController().navigate(R.id.navi_graph)
        }
        binding.btnAlarm.setOnClickListener{
            findNavController().navigate(R.id.action_mainFragment_to_notiFragment)
        }

        val newEmail = sharedPreferences.getString(LoginActivity.KEY_EMAIL, "")

        binding.btnTest.text = newEmail
        binding.btnTest.setOnClickListener {
            Log.d("$$","버튼 누름")
            val testDTO = TestDTO("이름인부분23","제목이고",123)
            val call: Call<String> = apiService.saveData(testDTO)


            call.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Log.d("$$","onResponse 응답 response : $response")
                    if (response.isSuccessful) {
                        // 서버 응답이 성공적으로 받아졌을 때
                        val responseBody: String? = response.body()


                        // responseBody에서 "Success" 등의 값을 확인하거나 원하는 처리를 수행
                        if (responseBody == "Success") {
                            // 성공 처리
                        } else {
                            // 다른 응답 처리
                        }
                    } else {
                        // 서버 응답이 실패했을 때
                        Log.d("$$", "onResponse 실패 response : ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d("$$","onFailure 발생")
                }
            })
        }

    }
}