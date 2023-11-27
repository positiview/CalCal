package com.example.calcal.decorator


import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.calcal.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import java.text.SimpleDateFormat
import java.util.*

val dummyData: Map<String, String> = mapOf(
    "2023-11-21" to "O",
    "2023-11-22" to "X"
)

class EventDecorator(private val context: Context, private var dates: MutableSet<String>) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dates.contains(sdf.format(day.date))
    }

    override fun decorate(view: DayViewFacade) {
        val drawable = when (dummyData[dates.first()]) {
            "O" -> ContextCompat.getDrawable(context, R.drawable.red_circle)
            "X" -> ContextCompat.getDrawable(context, R.drawable.x_shape)
            else -> null
        }

        if (drawable != null) {
            view.setSelectionDrawable(drawable)
        }
    }

    fun updateDates(newDates: MutableSet<String>) {
        this.dates = newDates
    }
}

