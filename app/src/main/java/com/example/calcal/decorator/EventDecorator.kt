package com.example.calcal.decorator

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.TextView
import com.example.calcal.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class EventDecorator(context: Context, dates: Collection<CalendarDay>) : DayViewDecorator {
    @SuppressLint("UseCompatLoadingForDrawables")
    private val drawable: Drawable? = context.getDrawable(R.drawable.cal_inset)
    private var dates: HashSet<CalendarDay> = HashSet(dates)

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        drawable?.let { view.setSelectionDrawable(it) }
    }

    fun updateDates(newDates: Collection<CalendarDay>) {
        this.dates = HashSet(newDates)
    }
}