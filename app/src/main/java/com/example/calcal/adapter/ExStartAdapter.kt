package com.example.calcal.adapter

import android.annotation.SuppressLint
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
import com.example.calcal.viewModel.ExerciseViewModel

class ExStartAdapter(
    private val mData: MutableList<String>,
    private val excal: Int,
    private val listener: ExercisestartFragment,
    private val navController: NavController,
    private val viewModel: ExerciseViewModel,
    private val onUserInput: (Double) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    @SuppressLint("SetTextI18n")
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val exStartTitle: TextView = itemView.findViewById(R.id.ex_start_title)
        val exStartContent: TextView = itemView.findViewById(R.id.ex_start_content)
        val btnExStartCal: TextView = itemView.findViewById(R.id.btn_ex_start_cal)


        init {
            itemView.setOnClickListener(this)
            btnExStartCal.visibility = View.GONE
            btnExStartCal.setOnClickListener { v ->
                val builder = AlertDialog.Builder(v.context, R.style.DialogTheme)
                val inflater = LayoutInflater.from(v.context)
                val view = inflater.inflate(R.layout.ex_custom_dialog, null)
                val input = view.findViewById<EditText>(R.id.dialog_input)
                val okButton = view.findViewById<Button>(R.id.dialog_ok)
                val cancelButton = view.findViewById<Button>(R.id.dialog_cancel)

                builder.setView(view)


                val dialog = builder.create()
                val dialogBack = inflater.inflate(R.layout.ex_info_dialog, null, false)
                dialogBack.background = ColorDrawable(Color.TRANSPARENT)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                okButton.setOnClickListener {
                    val userInputValue = input.text.toString().toDoubleOrNull() ?: 0.0
                    btnExStartCal.text = "$userInputValue kcal"
                    dialog.dismiss()
                    onUserInput(userInputValue) // 사용자 입력을 콜백 함수를 통해 전달
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
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.ex_start_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as ViewHolder
        val currentItem = mData[position]
        viewHolder.exStartContent.text = mData[position]

        if (position == 0) {
            viewHolder.exStartContent.text = excal?.toString() ?: "Default Text"
        }

        viewHolder.exStartTitle.text = currentItem

        if (position == 1) {
            viewHolder.exStartTitle.text = currentItem

            // 위치가 1인 경우에만 exStartContent를 숨기고 btnExStartCal를 보이게 합니다.
            viewHolder.exStartContent.visibility = View.GONE
            viewHolder.btnExStartCal.visibility = View.VISIBLE
        } else {
            // 그 외의 경우에는 exStartContent를 보이게 합니다.
            viewHolder.exStartContent.visibility = View.VISIBLE
        }
    }
}