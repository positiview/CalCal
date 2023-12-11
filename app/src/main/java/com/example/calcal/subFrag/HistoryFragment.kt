package com.example.calcal.subFrag

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calcal.MainActivity
import com.example.calcal.R
import com.example.calcal.adapter.RecordListAdapter
import com.example.calcal.databinding.FragmentHistoryBinding
import com.example.calcal.modelDTO.RouteAndTimeDTO
import com.example.calcal.modelDTO.RouteRecordDTO
import com.example.calcal.repository.RecordRepository
import com.example.calcal.repository.RecordRepositoryImpl
import com.example.calcal.signlogin.LoginActivity
import com.example.calcal.subFrag.MapFragment.Companion.LOCATION_PERMISSION_REQUEST_CODE
import com.example.calcal.util.CustomLoading
import com.example.calcal.util.LatLngBoundsCalculator.Companion.calculateBounds
import com.example.calcal.util.Resource
import com.example.calcal.viewModel.RecordViewModel
import com.example.calcal.viewModelFactory.RecordViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.UiSettings
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource

class HistoryFragment :Fragment(), OnMapReadyCallback {

    private lateinit var recordListAdapter: RecordListAdapter
    private lateinit var mNaverMap: NaverMap
    private lateinit var uiSettings: UiSettings
    private lateinit var locationSource: FusedLocationSource
    private val recordRepository: RecordRepository = RecordRepositoryImpl()
    private val recordViewModelFactory = RecordViewModelFactory(recordRepository)
    private val recordViewModel: RecordViewModel by viewModels() { recordViewModelFactory }
    private lateinit var sharedPreferences: SharedPreferences

    lateinit var behavior: BottomSheetBehavior<LinearLayout>
    var initialMapHeight = 0

    private lateinit var binding : FragmentHistoryBinding
            override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater,container, false)
        binding.txtEmpty.visibility = View.GONE
        val view = binding.root



        sharedPreferences = requireContext().getSharedPreferences(LoginActivity.PREF_NAME, Context.MODE_PRIVATE)
        val storedEmail = sharedPreferences.getString(LoginActivity.KEY_EMAIL, "")
        if (storedEmail != null){
            recordViewModel.getRecord(storedEmail)
        }




        initEvent()

        binding.goToMyCal.setOnClickListener{
            findNavController().navigate(R.id.action_historyFragment_to_graphFragment)
        }

        return  view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val options = NaverMapOptions()
            .mapType(NaverMap.MapType.Terrain)
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance(options).also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)
    }


    private fun initEvent() {
        persistentBottomSheetEvent()


    }


    private fun persistentBottomSheetEvent() {
        var height= 0
        val bottomSheet = binding.bottomSheet
        behavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheet.post {
            height = bottomSheet.height  // 뷰의 높이를 픽셀 단위로 얻습니다.
            // 여기에서 height를 사용하면 됩니다.

            behavior.isFitToContents = false
            Log.d("$$","검토 : hightPixels ${Resources.getSystem().displayMetrics.heightPixels} - height 여기 $height")
            behavior.expandedOffset = Resources.getSystem().displayMetrics.heightPixels - height
        }
        // BottomSheetBehavior 초기화



        // 드래그 이벤트 처리
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // 슬라이드 이벤트 처리
                // slideOffset은 0 (완전히 닫힘)에서 1 (완전히 열림)까지의 값입니다.
                // 여기에 슬라이드에 따른 추가적인 로직을 넣을 수 있습니다.
                if (slideOffset <= 0.4f) {
                    binding.bottomSheetBtn.rotation = slideOffset * (-180F / 0.4f)
                } else {
                    binding.bottomSheetBtn.rotation = -180F
                }
                binding.recyclerRecord.animate().x(binding.recyclerRecord.width * (1 - slideOffset)).setDuration(0).start()
                binding.recyclerRecord.alpha = slideOffset


            }
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // 상태가 변경될 때 호출됩니다.

                // 여기에 상태에 따른 추가적인 로직을 넣을 수 있습니다.
                when(newState){
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.bottomSheetBtn.setOnClickListener{
                            behavior.state = BottomSheetBehavior.STATE_EXPANDED
                        }
                        val params = binding.mapContainer.layoutParams
                        params?.height = initialMapHeight
                        binding.mapContainer.layoutParams = params
                    }
                    BottomSheetBehavior.STATE_DRAGGING ->{
                        Log.d("$$","onStateChanged: 드래그")
                    }
                    BottomSheetBehavior.STATE_EXPANDED ->{
                        Log.d("$$","onStateChanged: 펼침")
                        binding.bottomSheetBtn.setOnClickListener{
                            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                        val params = binding.mapContainer.layoutParams
                        params?.height = (initialMapHeight * 6)/10
                        binding.mapContainer.layoutParams = params
                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED ->{

                        val params = binding.mapContainer.layoutParams
                        params?.height = (initialMapHeight * 6)/10
                        binding.mapContainer.layoutParams = params
                        binding.bottomSheetBtn.setOnClickListener{
                            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                    }
                    BottomSheetBehavior.STATE_HIDDEN ->{
                        Log.d("$$","onStageChanged: 숨김")
                    }
                    BottomSheetBehavior.STATE_SETTLING->{
                        Log.d("$$","onStageChanged: 고정됨")
                    }
                }

            }

        })
    }
    fun onItemClick(rrDTO: RouteRecordDTO) {
        binding.calorieResult.text = rrDTO.calorie.toInt().toString()
        binding.distanceResult.text = rrDTO.distance

        val elapsedTimeInSeconds = ((rrDTO.ratList.last().recordTime - rrDTO.ratList.first().recordTime) / 1000).toInt()

        val hours = elapsedTimeInSeconds / 3600
        val minutes = (elapsedTimeInSeconds % 3600) / 60
        val seconds = elapsedTimeInSeconds % 60
        var formattedTime = ""

        if(elapsedTimeInSeconds>=0){
            formattedTime = String.format("%02d",seconds)
        }else if(elapsedTimeInSeconds>=60){
            formattedTime = String.format("%02d:%02d",minutes,seconds)
        }else if(elapsedTimeInSeconds>=3600){
            formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }

        binding.chronometerResult.text = formattedTime


        binding.chronometerResult.text = ((rrDTO.ratList.last().recordTime - rrDTO.ratList.first().recordTime)/1000).toInt().toString()
        behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        recordViewModel.getSelectedRecord(rrDTO.ratList)
        recordViewModel.getSelectedRecord.observe(viewLifecycleOwner){

            val list: List<RouteAndTimeDTO> = it
            Log.d("$$","이동한 경로 : $list")
            val coords: List<LatLng> = list.map { LatLng(it.latitude, it.longitude) }
            val cameraUpdate = CameraUpdate.fitBounds(calculateBounds(coords), 100)
            val path = PathOverlay()
            path.coords = coords
            path.color = Color.GREEN
            path.passedColor = Color.GRAY
            path.width = 10
            path.patternImage = OverlayImage.fromResource(R.drawable.shevron)
            path.patternInterval = 10
            path.map = mNaverMap
            mNaverMap.moveCamera(cameraUpdate)


            val startTime = list.first().recordTime
            val endTime = list.last().recordTime

            val timeRange = endTime - startTime
            Log.d("$$","timeRange = $timeRange")
            val animator = ValueAnimator.ofFloat(0F, 1F)
            if (timeRange < 60000) {
                animator.duration = (timeRange / 10).toLong()
            } else {
                animator.duration = 6000 // 애니메이션 시간을 6초로 설정
            }

            animator.addUpdateListener { animation ->
                val progress = animation.animatedValue as Float

                for (dto in list) {
                    val currentTime = dto.recordTime // 현재 DTO의 시간 값을 가져옵니다.

                    val currentProgress: Float = if (timeRange > 0) {
                        ((currentTime - startTime)/ timeRange).toFloat()
                    } else {
                        0F
                    }

                    // progress 값에 따라 경로의 진척률을 업데이트합니다.
                    if (currentProgress <= progress) {
                        path.progress = currentProgress.toDouble()
                    }
                }
            }
            animator.start()
            /*animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator, isReverse: Boolean) {
                    super.onAnimationStart(animation, isReverse)
                    behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }

                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    // 애니메이션이 종료되면 BottomSheet를 반 펼쳐진 상태로 변경
                    behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                }
            })*/
        }
    }

    override fun onMapReady(nMap: NaverMap) {
        mNaverMap = nMap
        mNaverMap.maxZoom = 18.5
        mNaverMap.minZoom = 5.0
        uiSettings = nMap.uiSettings
        uiSettings.isLocationButtonEnabled = false
        uiSettings.isRotateGesturesEnabled = false
        uiSettings.isTiltGesturesEnabled = false
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        mNaverMap.locationSource = locationSource
        binding.mapContainer.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                initialMapHeight = binding.mapContainer.height // 실제 크기를 얻습니다.

                // 리스너를 제거합니다. 이렇게 하지 않으면 뷰의 크기가 변경될 때마다 호출됩니다.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    binding.mapContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)
                } else {
                    binding.mapContainer.viewTreeObserver.removeGlobalOnLayoutListener(this)
                }
            }
        })
        Log.d("$$","initialMapHeight : $initialMapHeight")


        val recyclerView = binding.recyclerRecord
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, true)


        recordViewModel.getRecord.observe(viewLifecycleOwner){
            Log.d("$$","getRecord Observe@!!")


            when(it){
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    if(it.data == null){
                        Toast.makeText(requireContext(),"기록이 없습니다",Toast.LENGTH_SHORT).show()
                        binding.txtEmpty.visibility = View.VISIBLE
                    }else{
                        Log.d("$$","getRecord 성공성공!! ${it.data}")
                        recordListAdapter = RecordListAdapter(it.data,this)
                        recyclerView.adapter = recordListAdapter
                        recyclerView.scrollToPosition(recordListAdapter.itemCount - 1)
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(),it.string,Toast.LENGTH_SHORT).show()
                }
            }
        }
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