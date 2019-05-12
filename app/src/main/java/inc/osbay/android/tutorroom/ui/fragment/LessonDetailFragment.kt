package inc.osbay.android.tutorroom.ui.fragment

import android.app.Fragment
import android.app.FragmentManager
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.facebook.drawee.view.SimpleDraweeView

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.sdk.model.Lesson

class LessonDetailFragment : BackHandledFragment() {

    @BindView(R.id.tool_bar)
    internal var toolBar: Toolbar? = null
    @BindView(R.id.lesson_cover)
    internal var lessonCover: SimpleDraweeView? = null
    @BindView(R.id.lesson_title)
    internal var lessonTitle: TextView? = null
    @BindView(R.id.lesson_desc)
    internal var lessonDesc: TextView? = null
    @BindView(R.id.class_min)
    internal var classMin: TextView? = null
    @BindView(R.id.price_tv)
    internal var price: TextView? = null
    private var lesson: Lesson? = null
    private var tagID: String? = null
    private var packageID: String? = null
    private var lessonType: String? = null

    override fun onBackPressed(): Boolean {
        fragmentManager.popBackStack()
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lesson = arguments.getSerializable("lesson") as Lesson
        tagID = arguments.getString("tag_id")
        lessonType = arguments.getString("lesson_type")
        if (tagID!!.equals("0", ignoreCase = true)) packageID = arguments.getString("package_id")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle): View? {
        val view = inflater.inflate(R.layout.fragment_lesson_detail, container, false)
        ButterKnife.bind(this, view)

        toolBar!!.setBackgroundColor(activity.resources.getColor(R.color.colorPrimary))
        setSupportActionBar(toolBar)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lessonCover!!.setImageURI(Uri.parse(lesson!!.lessonCover))
        lessonTitle!!.text = lesson!!.lessonName
        lessonDesc!!.text = lesson!!.lessonDescription
        classMin!!.text = getString(R.string.minute, lesson!!.classMin.toString())
        price!!.text = lesson!!.lessonPrice.toString()
    }

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        setTitle(getString(R.string.lesson_detail))
        setDisplayHomeAsUpEnable(true)
    }

    @OnClick(R.id.book_btn_tv)
    internal fun bookLesson() {
        val fm = fragmentManager
        val frg = fm.findFragmentById(R.id.framelayout)
        val fragment = SingleBookingChooseLessonFragment()

        val bundle = Bundle()
        bundle.putString(SingleBookingChooseLessonFragment.Booking_EXTRA, LessonDetailFragment::class.java!!.getSimpleName())
        bundle.putString("lesson_id", lesson!!.lessonId)
        bundle.putString("lesson_type", lessonType)
        bundle.putString("tag_id", tagID)
        if (tagID!!.equals("0", ignoreCase = true)) bundle.putString("package_id", packageID)
        fragment.arguments = bundle
        if (frg == null) {
            fm.beginTransaction()
                    .add(R.id.framelayout, fragment)
                    .commit()
        } else {
            fm.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.framelayout, fragment)
                    .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return true
    }
}
