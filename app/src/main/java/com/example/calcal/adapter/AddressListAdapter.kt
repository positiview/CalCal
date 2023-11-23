package com.example.calcal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.calcal.databinding.AddressItemCardviewBinding
import com.example.calcal.modelDTO.Address
import com.example.calcal.modelDTO.NaverAddressResponseDTO

class AddressListAdapter: RecyclerView.Adapter<AddressListAdapter.ViewHolder>() {
    private var address = listOf<Address>()

    class  ViewHolder(val binding: AddressItemCardviewBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(addressDTO: Address){
            binding.roadAddress.text = addressDTO.roadAddress
            binding.postalCode.text = addressDTO.addressElements[0].longName
            binding.distance.text = addressDTO.distance.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = AddressItemCardviewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return minOf(address.size, 10)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val addressDTO = address[position]

        holder.bind(addressDTO)
    }

    fun setAddressList(addressList: List<Address>){
        this.address = addressList
        notifyDataSetChanged()
    }

}