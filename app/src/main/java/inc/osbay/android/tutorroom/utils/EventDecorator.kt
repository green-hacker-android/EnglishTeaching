package inc.osbay.android.tutorroom.utils

import android.content.Context

import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import java.util.HashSet

import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant

/**
 * Decorate several days with a dot
 */
class EventDecorator(private val bookType: Int, private val color: Int, dates: Collection<CalendarDay>, private val context: Context) : DayViewDecorator {
    private val dates: HashSet<CalendarDay>

    init {
        this.dates = HashSet(dates)
    }

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        /*view.addSpan(new DotSpan(5, color));*/
        if (bookType == CommonConstant.Single)
            view.setBackgroundDrawable(context.resources.getDrawable(R.drawable.calendar_bg_yellow))
        else if (bookType == CommonConstant.Trial)
            view.setBackgroundDrawable(context.resources.getDrawable(R.drawable.calendar_bg_red))
        else if (bookType == CommonConstant.Single_Trial)
            view.setBackgroundDrawable(context.resources.getDrawable(R.drawable.calendar_bg_red_yellow))
    }
}
