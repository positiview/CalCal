package com.example.calcal.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.calcal.R
import com.example.calcal.databinding.AddressItemCardviewBinding
import com.example.calcal.modelDTO.Address
import com.example.calcal.modelDTO.LocationDTO

class AddressListAdapter(private val onClickListener: (Address) -> Unit): RecyclerView.Adapter<AddressListAdapter.ViewHolder>() {
    private var address = listOf<Address>()

    class  ViewHolder(val binding: AddressItemCardviewBinding, private val onClickListener: (Address) -> Unit): RecyclerView.ViewHolder(binding.root){
        fun bind(addressDTO: Address){
            binding.roadAddress.text = addressDTO.roadAddress
            binding.postalCode.text = addressDTO.addressElements[0].longName
            binding.distance.text = addressDTO.distance.toString()



            // ViewHolder를 클릭했을 때의 이벤트 처리
            binding.root.setOnClickListener {
                onClickListener(addressDTO)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = AddressItemCardviewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view,onClickListener)
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