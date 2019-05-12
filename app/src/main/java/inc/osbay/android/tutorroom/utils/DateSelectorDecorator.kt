package inc.osbay.android.tutorroom.utils

import android.app.Activity
import android.graphics.drawable.Drawable
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import inc.osbay.android.tutorroom.R

/**
 * Use a custom selector
 */
class DateSelectorDecorator(context: Activity, classType: Int) : DayViewDecorator {

    private var drawable: Drawable? = null

    init {
        if (classType == 1)
        //Lesson Booking
            drawable = context.resources.getDrawable(R.drawable.date_selector_red)
        else if (classType == 2)
        //Package Booking
            drawable = context.resources.getDrawable(R.drawable.date_selector_yellow)
    }

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return true
    }

    override fun decorate(view: DayViewFacade) {
        view.setSelectionDrawable(drawable!!)
    }
}
