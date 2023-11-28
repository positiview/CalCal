package com.example.calcal.mainFrag

import com.example.calcal.decorator.EventDecorator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.calcal.R
import com.example.calcal.databinding.FragmentCalendarBinding
import com.example.calcal.decorator.SelectionDecorator
import com.example.calcal.decorator.TodayDecorator
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter
import com.prolificinteractive.materialcalendarview.format.TitleFormatter
import org.w3c.dom.Text

class CalendarFragment : Fragment() {

    private lateinit var binding: FragmentCalendarBinding
    private lateinit var btn_back : Button
    private lateinit var selectedDates: MutableSet<CalendarDay>
    private lateinit var eventDates: MutableSet<CalendarDay>
    private lateinit var selectionDecorator: SelectionDecorator
    private lateinit var eventDecorator: EventDecorator


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        btn_back = binding.btnBack
        btn_back.setOnClickListener {
            NavHostFragment.findNavController(this).navigateUp()
        }


        selectedDates = mutableSetOf<CalendarDay>()
        eventDates = mutableSetOf<CalendarDay>()

        binding.calandarTotalCalorie.findViewById<TextView>(R.id.calandar_total_calorie)
        binding.calandarDayCalorie.findViewById<TextView>(R.id.calandar_day_calorie)
        binding.calandarExercise.findViewById<TextView>(R.id.calandar_exercise)
        binding.relayExercise.findViewById<TextView>(R.id.relay_exercise)

        eventDates.addAll(listOf(
            CalendarDay.from(2023, 11, 21),
            CalendarDay.from(2023, 11, 22)
        ))

        eventDecorator = EventDecorator(requireContext(), eventDates)
        binding.calendarView.addDecorators(eventDecorator)

        selectionDecorator = SelectionDecorator(requireContext(), selectedDates, eventDates)
        binding.calendarView.addDecorators(selectionDecorator)

        val todayDecorator = TodayDecorator(requireContext())
        binding.calendarView.addDecorators(todayDecorator)

        binding.calendarView.setWeekDayFormatter(ArrayWeekDayFormatter(resources.getTextArray(R.array.custom_weekdays)));

        val months = arrayOf("1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월")
        binding.calendarView.setTitleFormatter { calendar ->
            "${calendar.year}년 ${months[if (calendar.month > 12) 11 else calendar.month- 1]}"
        }


        // 좌우 화살표 사이 연, 월의 폰트 스타일 설정
        binding.calendarView.setHeaderTextAppearance(R.style.CalendarWidgetHeader)

        binding.calendarView.post {
            binding.calendarView.addDecorators(eventDecorator, selectionDecorator, todayDecorator)
            binding.calendarView.invalidateDecorators()
        }

        binding.calendarView.setOnDateChangedListener { _, date, selected ->
            if (selected) {
                if (selectedDates.contains(date)) {
                    // 이미 선택된 날짜를 다시 클릭하면 선택을 취소합니다.
                    selectedDates.remove(date)
                } else {
                    // 다른 날짜를 선택하면 이전에 선택한 날짜의 선택을 취소하고, 새로운 날짜를 선택합니다.
                    selectedDates.clear()
                    selectedDates.add(date)
                }
            }


            // 별도의 메서드를 통해 데코레이터를 업데이트합니다.
            updateDecorator()

        }
    }



    private fun updateDecorator() {
        // 데코레이터의 날짜 데이터를 업데이트
        selectionDecorator.updateDates(selectedDates)
        eventDecorator.updateDates(eventDates)

        Log.d("CalendarFragment", "Decorator updated")
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }
}
