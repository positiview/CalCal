package com.example.calcal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calcal.R
import com.example.calcal.modelDTO.RouteRecordDTO
import com.example.calcal.subFrag.HistoryFragment

class RecordListAdapter(private val recordList: List<RouteRecordDTO>, private val listener: HistoryFragment) : RecyclerView.Adapter<RecordListAdapter.ViewHolder>(){

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener{

        private val recordName: TextView = view.findViewById(R.id.record_name)
        private val calorieRecord: TextView= view.findViewById(R.id.record_calorie)
        private val dateRecord: TextView = view.findViewById(R.id.record_date)
        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION){
                listener.onItemClick(recordList[position])
            }
        }

        fun bind(rList: RouteRecordDTO) {
            recordName.text = rList.courseName
            calorieRecord.text = rList.calorie.toInt().toString()
            dateRecord.text = rList.regDate.toString()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecordListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.record_item_cardview,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordListAdapter.ViewHolder, position: Int) {
        val rList = recordList[position]
        holder.bind(rList)
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }


}