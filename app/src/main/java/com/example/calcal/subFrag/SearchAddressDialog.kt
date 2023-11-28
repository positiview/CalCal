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
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calcal.adapter.AddressListAdapter
import com.example.calcal.databinding.DialongFragmentSearchAddressBinding
import com.example.calcal.modelDTO.ChannelDTO
import com.example.calcal.modelDTO.CoordinateDTO
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

        /*arguments?.let{bundle ->

            myArea = bundle.getString("myArea") ?: ""
        }*/

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




}


