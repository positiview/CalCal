package com.example.calcal.subFrag

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import com.example.calcal.R
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.calcal.adapter.AddressListAdapter
import com.example.calcal.databinding.FragmentSearchAddressBinding
import com.example.calcal.modelDTO.Address
import com.example.calcal.modelDTO.AddressDTO
import com.example.calcal.modelDTO.ChannelDTO
import com.example.calcal.modelDTO.CoordinateDTO
import com.example.calcal.modelDTO.ItemDTO
import com.example.calcal.modelDTO.NaverGeocodingResponseDTO
import com.example.calcal.retrofit.RequestFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class SearchAddressFragment:Fragment() {
    lateinit var binding: FragmentSearchAddressBinding
    private lateinit var addressListAdapter: AddressListAdapter
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locations : CoordinateDTO
    private lateinit var myArea : String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchAddressBinding.inflate(inflater,container,false)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        arguments?.let{bundle ->
            binding.searchQuery.setText(bundle.getString("address"))
            myArea= bundle.getString("myArea") ?: ""
        }



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
            if (query.isNotEmpty()) { // 예: 텍스트 길이가 2 이상인 경우에만 처리
                requestAddress(query) { itemDTOMutableList ->
                    addressListAdapter = AddressListAdapter(itemDTOMutableList,locations){
                        // Navigation Component를 사용하여 이전 fragment로 이동


                        val bundle = Bundle()
                        bundle.putParcelable("addressItem", it)
                        findNavController().navigate(R.id.action_searchAddressFragment_to_searchLocationFragment, bundle)
                        //  이부분 나중에 ViewModel 사용하여 수정해야함 !!!
                    }
                    binding.addressList.adapter = addressListAdapter
                }
            }
        }







        binding.apply {
            btnBack.setOnClickListener{
                findNavController().navigateUp()
            }
            directChooseOnMap.setOnClickListener {

            }
            directChooseMyLocation.setOnClickListener {

            }
        }

        return binding.root
    }

    private fun setAddressSearch() {
        /*binding.searchQuery.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                Log.d("$$","쿼리 : $query")
                if (query.length >= 2) { // 예: 텍스트 길이가 2 이상인 경우에만 처리
                    requestAddress(query) {
                        addressListAdapter.setAddressList(locations,it)
                    }
                }
            }
        })*/
       /* binding.btnSearch.setOnClickListener {
            val query = binding.searchQuery.text.toString()
            Log.d("$$","쿼리 : $query")
            if (query.isNotEmpty()) { // 예: 텍스트 길이가 2 이상인 경우에만 처리
                requestAddress(query) {
                    addressListAdapter = AddressListAdapter(it,locations){
                        // Navigation Component를 사용하여 이전 fragment로 이동



                        val bundle = Bundle()
                        bundle.putParcelable("addressItem", it)
                        findNavController().navigate(R.id.action_searchAddressFragment_to_searchLocationFragment, bundle)
                        //  이부분 나중에 ViewModel 사용하여 수정해야함 !!!
                    }
                }
            }
        }*/


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


