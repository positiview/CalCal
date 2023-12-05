package com.example.calcal.decorator

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import com.example.calcal.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class SelectionDecorator(private val context: Context, dates: MutableSet<CalendarDay>, private val eventDates: Collection<CalendarDay>)  :
    DayViewDecorator {
    private val grayBox = ContextCompat.getDrawable(context,R.drawable.calendar_selector)
    private var dates: MutableSet<CalendarDay> = dates

    override fun shouldDecorate(day: CalendarDay): Boolean {
        if (eventDates.contains(day)) {
            return false
        }
        return true
    }

    override fun decorate(view: DayViewFacade) {
        view.setSelectionDrawable(grayBox!!)
    }
    fun updateDates(newDates: MutableSet<CalendarDay>) {
        this.dates = newDates
    }
}