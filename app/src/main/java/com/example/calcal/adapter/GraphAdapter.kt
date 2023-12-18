package com.example.calcal.adapter

import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import at.grabner.circleprogress.CircleProgressView
import com.example.calcal.R
import com.example.calcal.helper.AddressHelper
import com.example.calcal.mainFrag.GraphFragment
import com.example.calcal.modelDTO.CalDTO
import com.example.calcal.viewModel.MemberViewModel

class GraphAdapter(private val calListRecord : Map<String, List<CalDTO>>, private val goalcal:Int, private val listener: GraphFragment, private val navController: NavController): RecyclerView.Adapter<RecyclerView.ViewHolder,>()   {
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
            val joggingList = calListRecord.getOrDefault("jogging", emptyList())
            val etcList = calListRecord.getOrDefault("etc", emptyList())
            var selectedRecordId: Int? = null

            if (position != RecyclerView.NO_POSITION) {
                if (joggingList.isNotEmpty() && position - 1 < joggingList.size) {
                    selectedRecordId = joggingList[position - 1].recordId
                    val bundle = Bundle().apply {
                        putInt("selectedRecordId", selectedRecordId!!)
                    }
                    navController.navigate(R.id.action_graphFragment_to_historyFragment, bundle)
                } else if (etcList.isNotEmpty() && position - 1 < etcList.size) {
                    selectedRecordId = etcList[position - 1].recordId
                    val bundle = Bundle().apply {
                        putInt("selectedRecordId", selectedRecordId)
                    }
                    navController.navigate(R.id.action_graphFragment_to_calcheckFragment, bundle)
                } else {
                    // 두 리스트 모두 비어있거나 모든 요소가 조회되었을 때의 처리
                    return
                }
            }
        }

        fun bind(calRecord: CalDTO,isJoggingList: Boolean) {
            exerTitle.text = if (isJoggingList) {
                calRecord.courseName
            } else {
                calRecord.exname
            }
            exerCalChild.text = calRecord.calorie.toInt().toString()
            val elapsedTimeInSeconds = (calRecord.time/1000).toInt()
            val hours = elapsedTimeInSeconds / 3600
            val minutes = (elapsedTimeInSeconds % 3600) / 60
            val seconds = elapsedTimeInSeconds % 60
            var formattedTime = ""
            if (isJoggingList) {
                // joggingList에 대한 처리
                formattedTime = if (elapsedTimeInSeconds >= 3600) {
                    String.format("%02d시 %02d분 %02d초", hours, minutes, seconds)
                } else if (elapsedTimeInSeconds >= 60) {
                    String.format("%02d분 %02d초", minutes, seconds)
                } else {
                    String.format("%02d초", seconds)
                }
            } else {
                // etcList에 대한 처리
                formattedTime = calRecord.exTime.toString()
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
        val calorieGoal: TextView = itemView.findViewById(R.id.ring_graph_allcal)

        init {
            modifyCalorieGoal.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            Log.d("GraphAdapter", "onClick executed")
            if(v == modifyCalorieGoal ){
                listener.onModifyCalorieGoal(calorieGoal)

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
        val joggingList = calListRecord.getOrDefault("jogging", emptyList())
        val etcList = calListRecord.getOrDefault("etc", emptyList())
        when (holder) {
            is ViewHolder -> {
                if (position <= joggingList.size) {
                    val calRecord = joggingList[position-1]
                    holder.bind(calRecord, true)
                } else if (position - 1 - joggingList.size < etcList.size) {
                    val calRecord = etcList[position - 1 - joggingList.size]
                    holder.bind(calRecord, false)
                }
            }
            is FirstViewHolder -> {
                holder.ringGraph.maxValue = goalcal.toFloat()

                val calorieCombined = (joggingList + etcList).sumOf { it.calorie }
                Log.d("$$","calorieCombined = $calorieCombined // goalcal = $goalcal")

                // 여기에서 holder.calorieGoal이 0이 아닐 때만 계산을 수행하도록 수정하였습니다.
                holder.calorieGoal.text = goalcal.toString()

                holder.ringGraph.setValueAnimated(0f, calorieCombined.toFloat(), 1000)

                val animator = ValueAnimator.ofInt(0, calorieCombined.toInt())
                animator.addUpdateListener { animation ->
                    val value = animation.animatedValue as Int
                    holder.ringGraphCurcal.text = value.toString()
                }
                animator.duration = 1000
                animator.start()
            }
            is LastViewHolder -> {
                // LastViewHolder에 대한 처리를 여기에 작성하세요.
            }
        }
    }

    override fun getItemCount(): Int {
        var total = 2
        for (list in calListRecord.values) {
            total += list.size
        }
        return total
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> ring_graph
            position == calListRecord.size + 1 -> graph_list_plus
            else -> graph_list
        }
    }


}
