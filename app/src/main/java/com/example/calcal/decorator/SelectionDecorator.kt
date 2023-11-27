package com.example.calcal.decorator

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.calcal.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class SelectionDecorator(private val context: Context, dates: MutableSet<CalendarDay>)  :
    DayViewDecorator {

    private var dates: MutableSet<CalendarDay> = dates

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return this.dates.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        val grayBox = ContextCompat.getDrawable(context, R.drawable.graph_back)
        view.setSelectionDrawable(grayBox!!)
    }
    fun updateDates(newDates: MutableSet<CalendarDay>) {
        this.dates = newDates
    }
}