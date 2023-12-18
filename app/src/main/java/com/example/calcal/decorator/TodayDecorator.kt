package com.example.calcal.decorator

import android.content.Context
import android.graphics.Color
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.example.calcal.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class TodayDecorator(context: Context)  :
    DayViewDecorator {
    private val todayBox = ContextCompat.getDrawable(context,R.drawable.today_back)
    private var date = CalendarDay.today()

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return day.equals(date)
    }

    override fun decorate(view: DayViewFacade) {

            view.setSelectionDrawable(todayBox!!)
            view.addSpan(ForegroundColorSpan(Color.BLACK))
    }
    fun updateDate(newDate: CalendarDay) { // 메서드 이름 및 매개변수를 변경했습니다.
        this.date = newDate
    }
}