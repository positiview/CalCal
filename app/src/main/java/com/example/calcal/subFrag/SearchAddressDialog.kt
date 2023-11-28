package com.example.calcal.subFrag

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calcal.adapter.AddressListAdapter
import com.example.calcal.databinding.DialongFragmentSearchAddressBinding
import com.example.calcal.modelDTO.ChannelDTO
import com.example.calcal.modelDTO.CoordinateDTO
import com.example.calcal.modelDTO.DeviceSizeDTO
import com.example.calcal.modelDTO.ItemDTO
import com.example.calcal.retrofit.RequestFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchAddressDialog(private val myArea: String) :DialogFragment() {
    lateinit var binding: DialongFragmentSearchAddressBinding
    private lateinit var addressListAdapter: AddressListAdapter
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locations : CoordinateDTO
    companion object {
        const val TAG = "SearchAddressDialog"
    }
    private var clickedTextView: TextView? = null

    fun setClickedTextView(textView: TextView) {
        clickedTextView = textView
    }

    // Other existing code...

    private fun handleItemClicked(itemDTO: ItemDTO) {
        clickedTextView?.text = itemDTO.title
        dismiss()
    }
    interface OnItemClickListener {
        fun onItemClicked(itemDTO: ItemDTO)

        fun onMyLocationClicked(myLocation: CoordinateDTO)
    }


    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }


   /* interface OnMyLocationClickListener{
        fun onMyLocationClicked(myLocation: CoordinateDTO)
    }

    private var myLocationClickListener: OnMyLocationClickListener? = null

    fun setOnMyLocationClickListener(listener: OnMyLocationClickListener){
        myLocationClickListener = listener
    }*/
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DialongFragmentSearchAddressBinding.inflate(inflater,container,false)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

       if (dialog?.window == null) {
           // 다이얼로그 창이 null인 경우 처리
           Log.e("SearchAddressDialog", "Dialog window is null.")
           dismiss()  // 다이얼로그를 종료하거나 적절한 대응을 수행하세요.
           return null  // onCreateView에서 null을 반환하면 오류가 발생하지 않습니다.
       }
       dialog?.window?.setDimAmount(0.8f)

       // 터치 이벤트를 밖으로 전파하지 않도록 설정하여 주변을 터치하면 다이얼로그가 종료되도록 합니다.
       dialog?.setCanceledOnTouchOutside(true)

        val recyclerView = binding.addressList
        recyclerView.layoutManager = LinearLayoutManager(context)


        val locationPermissionRequest =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted){
                    fusedLocationProviderClient.lastLocation
                        .addOnSuccessListener { location: Location? ->
                            Log.d("$$","내 위치 정보 location = $location")
                            if (location != null) {
                                locations = CoordinateDTO(location.latitude,location.longitude)
                            }
                            Log.d("$$","CoordinateDTO locations = $locations")

                        }
                }else{

                }
            }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    Log.d("$$","내 위치 정보 location = $location")
                    if (location != null) {
                        locations = CoordinateDTO(location.latitude,location.longitude)
                    }
                    Log.d("$$","CoordinateDTO locations = $locations")

                }
        }else{
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }






        binding.btnSearch.setOnClickListener {
            val query = binding.searchQuery.text.toString()
            Log.d("$$","쿼리 : $query")
            if (query.isNotEmpty()) { // 입력내용이 있을경우에만 처리
                requestAddress(query) { itemDTOMutableList ->
                    addressListAdapter = AddressListAdapter(itemDTOMutableList,locations,this)
                    recyclerView.adapter = addressListAdapter

                    binding.addressList.adapter = addressListAdapter
                }
            }
        }


        binding.apply {
            btnBack.setOnClickListener{
                dismiss()
            }
            directChooseOnMap.setOnClickListener {

            }
            directChooseMyLocation.setOnClickListener {
                onItemClickListener?.onMyLocationClicked(locations)
                dismiss()
            }
        }

        return binding.root
    }

    fun onItemClick(itemDTO: ItemDTO) {

        onItemClickListener?.onItemClicked(itemDTO)

        dismiss()
        /*val bundle = Bundle()
        bundle.putParcelable("addressItem", itemDTO)
        NavHostFragment.findNavController(this).navigate(R.id.action_searchAddressFragment_to_searchLocationFragment, bundle)*/
        //  이부분 나중에 ViewModel 사용하여 수정해야함 !!!
    }



    private fun requestAddress( query : String, callback: (MutableList<ItemDTO>) -> Unit){

        val clientId = "o02cEsDx90tG2_tn7Y7n"
        val clientKey = "4ZgskCsWCF"
        val service = RequestFactory.create3()
        val display = 5
        val start = 1
        val sort = "random"
        val newQuery = "$myArea $query".trim()
        Log.d("$$","query 값 : $newQuery")
        val callAddressList: Call<ChannelDTO> = service.searchLocation(newQuery, display, start, sort, clientId, clientKey)

        callAddressList.enqueue(object : Callback<ChannelDTO>{
            override fun onResponse(call: Call<ChannelDTO>, response: Response<ChannelDTO>) {
                Log.d("$$","주소 검색 결과 읍답 : $response")
                if(response.isSuccessful){
                    val respAddress: ChannelDTO? = response.body()
                    Log.d("$$","responseAddress = $respAddress")
                    if(respAddress != null && respAddress.items != null&& respAddress.items.isNotEmpty()){
                        val addressList: MutableList<ItemDTO> = respAddress.items.toMutableList()
                        callback(addressList)
                        for (address in addressList) {
                            val roadAddress = address.roadAddress
                            val jibunAddress = address.address
                            val x = address.mapx
                            val y = address.mapy

                            // 필요한 작업 수행
                            Log.d("$$","roadAddress: $roadAddress")
                            Log.d("$$","jibunAddress: $jibunAddress")
                            Log.d("$$","x: $x, y: $y")


                        }
                    }else {
                        Log.d("$$","에러: ${response.code()} - ${response.message()}")
                    }
                }else{
                    Log.e("$$","주소 검색 결과 응답 실패!!!")
                }
            }

            override fun onFailure(call: Call<ChannelDTO>, t: Throwable) {
                Log.d("$$","요청 실패 onFailure")
            }

        })


    }


    override fun onResume() {
        super.onResume()
        fragmentSize(){

            val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
            val deviceWidth = it.deviceWidth
            params?.width = (deviceWidth * 0.9).toInt()
            dialog?.window?.attributes = params as WindowManager.LayoutParams
        }

    }

    private fun fragmentSize(callback: (DeviceSizeDTO) -> Unit){
        val windowManager = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getRealSize(size)

        val deviceSizeDTO = DeviceSizeDTO(deviceWidth = size.x, deviceHeight = size.y)
        callback(deviceSizeDTO)

    }
}


