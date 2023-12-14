package com.example.calcal.adapter

import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import at.grabner.circleprogress.CircleProgressView
import com.example.calcal.R
import com.example.calcal.helper.AddressHelper
import com.example.calcal.mainFrag.GraphFragment
import com.example.calcal.modelDTO.CalDTO
import com.google.android.material.button.MaterialButton

class GraphAdapter(private val calListRecord : List<CalDTO>, private val listener: GraphFragment, private val navController: NavController): RecyclerView.Adapter<RecyclerView.ViewHolder,>()   {
    companion object {
        private const val ring_graph = 0 // 링그래프
        private const val graph_list = 1 // 오늘 운동한 기록 카드
        private const val graph_list_plus = 2 // 오늘 운동 기록 추가하기
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        // graph_list
        val exerTitle: TextView = itemView.findViewById(R.id.exer_title)
        val exerCalChild:TextView = itemView.findViewById(R.id.exer_cal_child)
        val exerCalParent:TextView = itemView.findViewById(R.id.exer_cal_parent) // 목표 칼로리
        val exerIng:View = itemView.findViewById(R.id.exer_ing)
        val exerPlace:TextView = itemView.findViewById(R.id.exer_place)
        val exerDistance:TextView = itemView.findViewById(R.id.exer_distance)
        val exerTime:TextView = itemView.findViewById(R.id.exer_time)
        val btnExermap:View = itemView.findViewById(R.id.btn_exermap)
        val continuity:TextView = itemView.findViewById(R.id.continuity)

        init {
            itemView.setOnClickListener(this)
            btnExermap.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                if (v == btnExermap) {
                    val selectedRecordId = calListRecord[position - 1].recordId
                    val bundle = Bundle().apply {
                        putInt("selectedRecordId",selectedRecordId)
                    }
                    navController.navigate(R.id.action_graphFragment_to_historyFragment,bundle)
                } else {

                    listener.onItemClick(position)
                }
            }
        }

        fun bind(calRecord: CalDTO) {
            exerTitle.text = calRecord.courseName
            exerCalChild.text = calRecord.calorie.toInt().toString()
            val elapsedTimeInSeconds = (calRecord.time/1000).toInt()
            val hours = elapsedTimeInSeconds / 3600
            val minutes = (elapsedTimeInSeconds % 3600) / 60
            val seconds = elapsedTimeInSeconds % 60
            var formattedTime = ""
            if(elapsedTimeInSeconds>=3600){
                formattedTime = String.format("%02d시 %02d분 %02d초", hours, minutes, seconds)
            }else if(elapsedTimeInSeconds>=60){
                formattedTime = String.format("%02d분 %02d초",minutes,seconds)
            }else if(elapsedTimeInSeconds>=0){
                formattedTime = String.format("%02d초",seconds)
            }
            val coords = "${calRecord.longitude},${calRecord.latitude}"
            AddressHelper.getAddressName(coords){
                val ad = it.firstOrNull()
                if(ad !=null) {
                    val actualAddress =
                        "${ad.region.area2.name} ${ad.region.area3.name} ${ad.region.area4.name}".trim()
                    exerPlace.text = actualAddress
                }
            }

            exerTime.text = formattedTime
            exerDistance.text = calRecord.distance
            if(calRecord.goalCalorie == 0.0){
                exerCalParent.text = "/"+calRecord.calorie.toInt().toString()
            }else{
                exerCalParent.text = "/"+calRecord.goalCalorie.toInt().toString()
            }
            continuity.text = calRecord.countDays.toString()+"일"
        }
    }

    // 오늘 전체 그래프 합계 기록
    inner class FirstViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),View.OnClickListener {
        val ringGraph: CircleProgressView = itemView.findViewById(R.id.ring_graph)
        val ringGraphCurcal: TextView = itemView.findViewById(R.id.ring_graph_cal)
        val modifyCalorieGoal: TextView = itemView.findViewById(R.id.modify_calorie_goal)

        init {
            modifyCalorieGoal.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            Log.d("GraphAdapter", "onClick executed")
            if(v == modifyCalorieGoal ){

                listener.onModifyCalorieGoal()
            }

        }
    }
    // 그래프 추가
    inner class LastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val btnGraphPlus: View = itemView.findViewById(R.id.btn_graph_plus)

        init {
            btnGraphPlus.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                when (v) {
                    btnGraphPlus -> navController.navigate(R.id.action_graphFragment_to_exercisestartFragment) // 수정 필요
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == graph_list_plus) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.graph_list_plus, parent, false)
            LastViewHolder(view)
        } else if (viewType == ring_graph) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.ring_graph, parent, false)


            FirstViewHolder(view)
        }else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.graph_list, parent, false)
            ViewHolder(view)
        }
    }

    // 카드 꾸미기
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                if (position <= calListRecord.size) {
                    val calRecord = calListRecord[position-1]
                    holder.bind(calRecord)
                }


            }
            is FirstViewHolder -> {
                val calorieCombined = calListRecord.sumOf { it.calorie }

                holder.ringGraph.setValueAnimated(0f, (calorieCombined/1000).toFloat(), 1000) // 1초 동안 0에서 100까지 애니메이션합니다.

                val animator = ValueAnimator.ofInt(0, calorieCombined.toInt())
                animator.addUpdateListener { animation ->
                    val value = animation.animatedValue as Int
                    holder.ringGraphCurcal.text = value.toString()
                }
                animator.duration = 1000 // 1초 동안 0에서 25까지 애니메이션합니다.
                animator.start()
            }
            is LastViewHolder -> {
                // LastViewHolder에 대한 처리를 여기에 작성하세요.
            }
        }
    }

    override fun getItemCount(): Int = calListRecord.size+2

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> ring_graph
            position == calListRecord.size + 1 -> graph_list_plus
            else -> graph_list
        }
    }


}
