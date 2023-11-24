package com.example.calcal.adapter

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import at.grabner.circleprogress.CircleProgressView
import com.example.calcal.R
import com.example.calcal.mainFrag.GraphFragment

class GraphAdapter(private val mData: List<String>, private val listener: GraphFragment): RecyclerView.Adapter<RecyclerView.ViewHolder>()   {
    companion object {
        private const val ring_graph = 0
        private const val graph_list = 1
        private const val graph_list_plus = 2
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val exerTitle: TextView = itemView.findViewById(R.id.exer_title)
        val exerCalChild:TextView = itemView.findViewById(R.id.exer_cal_child)
        val exerCalParent:TextView = itemView.findViewById(R.id.exer_cal_parent)
        val exerIng:View = itemView.findViewById(R.id.exer_ing)
        val walkCount:View = itemView.findViewById(R.id.walk_count)
        val exerDistance:View = itemView.findViewById(R.id.exer_distance)
        val exerTime:View = itemView.findViewById(R.id.exer_time)
        val btnExermap:View = itemView.findViewById(R.id.btn_exermap)

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
    inner class FirstViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ringGraph: CircleProgressView = itemView.findViewById(R.id.ring_graph)
        val ringGraphCurcal: TextView = itemView.findViewById(R.id.ring_graph_curcal)

    }
    inner class LastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnGraphPlus:View = itemView.findViewById(R.id.btn_graph_plus)
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                if (position < mData.size) {
                    val text = mData[position]
                    holder.exerTitle.text = text
                }
            }
            is FirstViewHolder -> {
                holder.ringGraph.setValueAnimated(0f, 88f, 1000) // 1초 동안 0에서 100까지 애니메이션합니다.

                val animator = ValueAnimator.ofInt(0, 360)
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

    override fun getItemCount(): Int = mData.size

    override fun getItemViewType(position: Int): Int {
        return if (position < mData.size && mData[position] == "요소 추가") {
            graph_list_plus
        } else if(position == 0 && mData[position] == "그래프"){
            ring_graph
        }else {
            graph_list
        }
    }


}
