package com.example.calcal.mainFrag

import com.example.calcal.decorator.EventDecorator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.calcal.R
import com.example.calcal.databinding.FragmentCalendarBinding
import com.example.calcal.decorator.SelectionDecorator
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter

class CalendarFragment : Fragment() {

    private lateinit var binding: FragmentCalendarBinding
    private lateinit var selectedDates: MutableSet<CalendarDay>
    private lateinit var eventDates: MutableSet<CalendarDay>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectedDates = mutableSetOf<CalendarDay>()
        eventDates = mutableSetOf<CalendarDay>()

        // 날짜를 설정합니다. 예시로 2023년 11월 27일을 넣었습니다.
        val selectDates = listOf(CalendarDay.from(2023, 11, 27))
        selectedDates.addAll(selectDates)

        val selectDecorator = SelectionDecorator(requireContext(), selectedDates) // EventDecorator를 생성합니다.
        binding.calendarView.addDecorators(selectDecorator)

        eventDates.addAll(listOf(
            CalendarDay.from(2023, 11, 21),
            CalendarDay.from(2023, 11, 22)
        ))

        // eventDates를 초기화한 후에 eventDateStrings를 생성해야 합니다.
        val eventDateStrings = eventDates.map { it.toString() }.toMutableSet()
        val eventDecorator = EventDecorator(requireContext(), eventDateStrings)
        binding.calendarView.addDecorators(eventDecorator)


        binding.calendarView.setWeekDayFormatter(ArrayWeekDayFormatter(resources.getTextArray(R.array.custom_weekdays)));
        val months = arrayOf("1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월")
        val titleFormatter = MonthArrayTitleFormatter(months)
        binding.calendarView.setTitleFormatter(titleFormatter)



        // 좌우 화살표 사이 연, 월의 폰트 스타일 설정
        binding.calendarView.setHeaderTextAppearance(R.style.CalendarWidgetHeader)

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
            Log.d("CalendarFragment", "Decorator updated")
        }

    }

    private fun updateDecorator() {
        val selectionDecorator = SelectionDecorator(requireContext(), selectedDates)
        // 기존 데코레이터를 제거합니다.
        binding.calendarView.removeDecorators()
        // 새로운 데코레이터를 추가합니다.
        binding.calendarView.addDecorator(selectionDecorator)

        val eventDateStrings = eventDates.map { it.toString() }.toMutableSet()
        val eventDecorator = EventDecorator(requireContext(), eventDateStrings)
        eventDates.forEach { eventDateStrings.add(it.toString()) }
        eventDecorator.updateDates(eventDateStrings)
        binding.calendarView.addDecorator(eventDecorator)
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }
}
