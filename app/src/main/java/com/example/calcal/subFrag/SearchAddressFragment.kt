package com.example.calcal.subFrag

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.calcal.R
import com.example.calcal.adapter.AddressListAdapter
import com.example.calcal.databinding.FragmentSearchAddressBinding
import com.example.calcal.modelDTO.Address
import com.example.calcal.modelDTO.AddressDTO
import com.example.calcal.modelDTO.NaverGeocodingResponseDTO
import com.example.calcal.retrofit.RequestFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchAddressFragment:Fragment() {
    lateinit var binding: FragmentSearchAddressBinding
    lateinit var addressListAdapter: AddressListAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchAddressBinding.inflate(inflater,container,false)

        arguments?.let{bundle ->
            binding.searchQuery.setText(bundle.getString("address"))
        }

        binding.searchQuery.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                requestAddress(query){
                    addressListAdapter.setAddressList(it)
                }
            }
        })
        addressListAdapter = AddressListAdapter{
            // Navigation Component를 사용하여 이전 fragment로 이동

                val copyAddress = it
                val addressDTO = AddressDTO(copyAddress.roadAddress, copyAddress.addressElements[0].longName, copyAddress.x, copyAddress.y)
                val bundle = Bundle()
                bundle.putParcelable("addressDTO", addressDTO)
                findNavController().navigate(R.id.action_searchAddressFragment_to_searchLocationFragment, bundle)
            //  이부분 나중에 ViewModel 사용하여 수정해야함 !!!
        }






        binding.apply {
            directChooseOnMap.setOnClickListener {

            }
            directChooseMyLocation.setOnClickListener {

            }
        }

        return binding.root
    }
    private fun requestAddress(query : String, callback: (List<Address>) -> Unit){
        val apiKeyId = "clurvbfncz"
        val apiKey = "WuVnFkJnFdIt7L03dhCZw7iCyNCeLGtNh3UsrhrI"
        val service = RequestFactory.create2()
        val callAddressList: Call<NaverGeocodingResponseDTO> = service.geocode(query, apiKeyId, apiKey)

        callAddressList.enqueue(object : Callback<NaverGeocodingResponseDTO>{
            override fun onResponse(call: Call<NaverGeocodingResponseDTO>, response: Response<NaverGeocodingResponseDTO>) {
                Log.d("$$","주소 검색 결과 읍답 : $response")
                if(response.isSuccessful){
                    val respAddress: NaverGeocodingResponseDTO? = response.body()

                    if(respAddress != null && respAddress.addresses.isNotEmpty()){
                        val addressList: List<Address> = respAddress.addresses
                        callback(addressList)
                        for (address in addressList) {
                            val roadAddress = address.roadAddress
                            val jibunAddress = address.jibunAddress
                            val x = address.x
                            val y = address.y

                            // 필요한 작업 수행
                            println("roadAddress: $roadAddress")
                            println("jibunAddress: $jibunAddress")
                            println("x: $x, y: $y")


                        }
                    }else {
                        println("에러: ${response.code()} - ${response.message()}")
                    }
                }else{
                    Log.e("$$","주소 검색 결과 응답 실패!!!")
                }
            }

            override fun onFailure(call: Call<NaverGeocodingResponseDTO>, t: Throwable) {
                Log.d("$$","요청 실패 onFailure")
            }

        })


    }
}