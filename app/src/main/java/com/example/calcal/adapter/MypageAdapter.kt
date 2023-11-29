package com.example.calcal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.calcal.R
import com.example.calcal.mainFrag.MypageFragment

class MypageAdapter(private val mData: List<String>, private val listener: MypageFragment): RecyclerView.Adapter<MypageAdapter.ViewHolder>()  {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val mypageList: TextView = itemView.findViewById(R.id.mypage_list)
        val mypage_list_img:ImageView = itemView.findViewById(R.id.mypage_list_img)
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mypage_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val text = mData[position]
        holder.mypageList.text = text
        if (position == 4) {
            // 특정 위치(position)의 요소의 색상을 변경
            holder.mypageList.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.verygray))
            holder.mypage_list_img.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.verygray))
            holder.mypage_list_view.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.verygray))
        }
    }

    override fun getItemCount(): Int = mData.size




}
