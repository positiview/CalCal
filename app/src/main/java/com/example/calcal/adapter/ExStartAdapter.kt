package com.example.calcal.adapter

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.calcal.R
import com.example.calcal.subFrag.ExercisestartFragment

class ExStartAdapter (private val mData: List<String>, private val listener: ExercisestartFragment, private val navController: NavController): RecyclerView.Adapter<RecyclerView.ViewHolder,>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val exStartTitle: TextView = itemView.findViewById(R.id.ex_start_title)
        val exStartContent: TextView = itemView.findViewById(R.id.ex_start_content)
        val btnExStartCal: TextView = itemView.findViewById(R.id.btn_ex_start_cal)


        init {
            itemView.setOnClickListener(this)
            btnExStartCal.visibility = View.GONE
            btnExStartCal.setOnClickListener { v ->
                val builder = AlertDialog.Builder(v.context)
                val inflater = LayoutInflater.from(v.context)
                val view = inflater.inflate(R.layout.ex_custom_dialog, null)
                val input = view.findViewById<EditText>(R.id.dialog_input)
                val okButton = view.findViewById<Button>(R.id.dialog_ok)
                val cancelButton = view.findViewById<Button>(R.id.dialog_cancel)

                builder.setView(view)

                val dialog = builder.create()
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                okButton.setOnClickListener {
                    btnExStartCal.text = input.text.toString()
                    dialog.dismiss()
                }
                cancelButton.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()
            }
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                if (v == btnExStartCal) {

                } else {

                    listener.onItemClick(position)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ex_start_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = mData[position]
        if (holder is ViewHolder) {
            holder.exStartTitle.text = currentItem

            if (position == 1) {
                holder.exStartTitle.text = currentItem

                // exStartContent를 숨기고 btnExStartCal를 보이게 합니다.
                holder.exStartContent.visibility = View.GONE
                holder.btnExStartCal.visibility = View.VISIBLE
            }
        }

    }
}