package com.example.calcal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calcal.R
import com.example.calcal.mainFrag.NotiFragment

class NotiAdapter(private val mData: List<String>, private val listener: NotiFragment): RecyclerView.Adapter<NotiAdapter.ViewHolder>()  {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val notiTitle: TextView = itemView.findViewById(R.id.noti_title)
        val notiDetail:TextView = itemView.findViewById(R.id.noti_detail)
        val mypage_list_view:View = itemView.findViewById(R.id.mypage_list_view)
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.noti_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val text = mData[position]
        holder.notiTitle.text = text
        holder.notiDetail.text = text

    }

    override fun getItemCount(): Int = mData.size




}
