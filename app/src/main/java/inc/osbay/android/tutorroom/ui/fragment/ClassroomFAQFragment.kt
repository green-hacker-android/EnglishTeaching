package inc.osbay.android.tutorroom.ui.fragment

import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.ui.activity.ClassRoomActivity

class ClassroomFAQFragment : BackHandledFragment() {
    var classType: String? = null
    @BindView(R.id.arrow1)
    internal var arrow1: ImageView? = null
    @BindView(R.id.arrow2)
    internal var arrow2: ImageView? = null
    @BindView(R.id.arrow3)
    internal var arrow3: ImageView? = null
    @BindView(R.id.arrow4)
    internal var arrow4: ImageView? = null
    @BindView(R.id.arrow5)
    internal var arrow5: ImageView? = null
    @BindView(R.id.arrow6)
    internal var arrow6: ImageView? = null
    @BindView(R.id.arrow7)
    internal var arrow7: ImageView? = null
    @BindView(R.id.arrow8)
    internal var arrow8: ImageView? = null
    @BindView(R.id.arrow9)
    internal var arrow9: ImageView? = null
    @BindView(R.id.arrow10)
    internal var arrow10: ImageView? = null
    @BindView(R.id.arrow11)
    internal var arrow11: ImageView? = null
    @BindView(R.id.arrow12)
    internal var arrow12: ImageView? = null
    @BindView(R.id.arrow13)
    internal var arrow13: ImageView? = null
    @BindView(R.id.arrow14)
    internal var arrow14: ImageView? = null
    @BindView(R.id.arrow15)
    internal var arrow15: ImageView? = null
    @BindView(R.id.arrow16)
    internal var arrow16: ImageView? = null
    @BindView(R.id.arrow17)
    internal var arrow17: ImageView? = null
    @BindView(R.id.arrow18)
    internal var arrow18: ImageView? = null
    @BindView(R.id.faq1_content_ll)
    internal var faq1ContentLL: LinearLayout? = null
    @BindView(R.id.faq2_content_ll)
    internal var faq2ContentLL: LinearLayout? = null
    @BindView(R.id.faq3_content_ll)
    internal var faq3ContentLL: LinearLayout? = null
    @BindView(R.id.faq4_content_ll)
    internal var faq4ContentLL: LinearLayout? = null
    @BindView(R.id.faq5_content_ll)
    internal var faq5ContentLL: LinearLayout? = null
    @BindView(R.id.faq6_content_ll)
    internal var faq6ContentLL: LinearLayout? = null
    @BindView(R.id.faq7_content_ll)
    internal var faq7ContentLL: LinearLayout? = null
    @BindView(R.id.faq8_content_ll)
    internal var faq8ContentLL: LinearLayout? = null
    @BindView(R.id.faq9_content_ll)
    internal var faq9ContentLL: LinearLayout? = null
    @BindView(R.id.faq10_content_ll)
    internal var faq10ContentLL: LinearLayout? = null
    @BindView(R.id.faq11_content_ll)
    internal var faq11ContentLL: LinearLayout? = null
    @BindView(R.id.faq12_content_ll)
    internal var faq12ContentLL: LinearLayout? = null
    @BindView(R.id.faq13_content_ll)
    internal var faq13ContentLL: LinearLayout? = null
    @BindView(R.id.faq14_content_ll)
    internal var faq14ContentLL: LinearLayout? = null
    @BindView(R.id.faq15_content_ll)
    internal var faq15ContentLL: LinearLayout? = null
    @BindView(R.id.faq16_content_ll)
    internal var faq16ContentLL: LinearLayout? = null
    @BindView(R.id.faq17_content_ll)
    internal var faq17ContentLL: LinearLayout? = null
    @BindView(R.id.faq18_content_ll)
    internal var faq18ContentLL: LinearLayout? = null
    @BindView(R.id.tool_bar)
    internal var toolBar: Toolbar? = null
    internal var isShow = BooleanArray(18)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        classType = arguments.getString(EXTRA_DISPLAY_FRAGMENT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_classroom_faq, container, false)
        ButterKnife.bind(this, view)

        toolBar!!.setBackgroundColor(activity.resources.getColor(R.color.colorPrimary))
        setSupportActionBar(toolBar)
        return view
    }

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        setTitle(getString(R.string.ac_faq))
        setDisplayHomeAsUpEnable(true)
    }

    override fun onBackPressed(): Boolean {
        fragmentManager.popBackStack()
        if (classType!!.equals(ClassRoomActivity::class.java!!.getSimpleName(), ignoreCase = true)) {
            activity.finish()
        }
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return false
    }

    @OnClick(R.id.faq1_rl)
    internal fun clickFaq1() {
        if (isShow[0] == false) {
            isShow[0] = true
            faq1ContentLL!!.visibility = View.VISIBLE
            arrow1!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_resume_up_arrow))
        } else {
            isShow[0] = false
            faq1ContentLL!!.visibility = View.GONE
            arrow1!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_arrow_head_down_black))
        }
    }

    @OnClick(R.id.faq2_rl)
    internal fun clickFaq2() {
        if (isShow[1] == false) {
            isShow[1] = true
            faq2ContentLL!!.visibility = View.VISIBLE
            arrow2!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_resume_up_arrow))
        } else {
            isShow[1] = false
            faq2ContentLL!!.visibility = View.GONE
            arrow2!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_arrow_head_down_black))
        }
    }

    @OnClick(R.id.faq3_rl)
    internal fun clickFaq3() {
        if (isShow[2] == false) {
            isShow[2] = true
            faq3ContentLL!!.visibility = View.VISIBLE
            arrow3!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_resume_up_arrow))
        } else {
            isShow[3] = false
            faq3ContentLL!!.visibility = View.GONE
            arrow3!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_arrow_head_down_black))
        }
    }

    @OnClick(R.id.faq4_rl)
    internal fun clickFaq4() {
        if (isShow[3] == false) {
            isShow[3] = true
            faq4ContentLL!!.visibility = View.VISIBLE
            arrow4!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_resume_up_arrow))
        } else {
            isShow[3] = false
            faq4ContentLL!!.visibility = View.GONE
            arrow4!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_arrow_head_down_black))
        }
    }

    @OnClick(R.id.faq5_rl)
    internal fun clickFaq5() {
        if (isShow[4] == false) {
            isShow[4] = true
            faq5ContentLL!!.visibility = View.VISIBLE
            arrow5!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_resume_up_arrow))
        } else {
            isShow[4] = false
            faq5ContentLL!!.visibility = View.GONE
            arrow5!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_arrow_head_down_black))
        }
    }

    @OnClick(R.id.faq6_rl)
    internal fun clickFaq6() {
        if (isShow[5] == false) {
            isShow[5] = true
            faq6ContentLL!!.visibility = View.VISIBLE
            arrow6!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_resume_up_arrow))
        } else {
            isShow[5] = false
            faq6ContentLL!!.visibility = View.GONE
            arrow6!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_arrow_head_down_black))
        }
    }

    @OnClick(R.id.faq7_rl)
    internal fun clickFaq7() {
        if (isShow[6] == false) {
            isShow[6] = true
            faq7ContentLL!!.visibility = View.VISIBLE
            arrow7!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_resume_up_arrow))
        } else {
            isShow[6] = false
            faq7ContentLL!!.visibility = View.GONE
            arrow7!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_arrow_head_down_black))
        }
    }

    @OnClick(R.id.faq8_rl)
    internal fun clickFaq8() {
        if (isShow[7] == false) {
            isShow[7] = true
            faq8ContentLL!!.visibility = View.VISIBLE
            arrow8!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_resume_up_arrow))
        } else {
            isShow[7] = false
            faq8ContentLL!!.visibility = View.GONE
            arrow8!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_arrow_head_down_black))
        }
    }

    @OnClick(R.id.faq9_rl)
    internal fun clickFaq9() {
        if (isShow[8] == false) {
            isShow[8] = true
            faq9ContentLL!!.visibility = View.VISIBLE
            arrow9!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_resume_up_arrow))
        } else {
            isShow[8] = false
            faq9ContentLL!!.visibility = View.GONE
            arrow9!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_arrow_head_down_black))
        }
    }

    @OnClick(R.id.faq10_rl)
    internal fun clickFaq10() {
        if (isShow[9] == false) {
            isShow[9] = true
            faq10ContentLL!!.visibility = View.VISIBLE
            arrow10!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_resume_up_arrow))
        } else {
            isShow[9] = false
            faq10ContentLL!!.visibility = View.GONE
            arrow10!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_arrow_head_down_black))
        }
    }

    @OnClick(R.id.faq11_rl)
    internal fun clickFaq11() {
        if (isShow[10] == false) {
            isShow[10] = true
            faq11ContentLL!!.visibility = View.VISIBLE
            arrow11!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_resume_up_arrow))
        } else {
            isShow[10] = false
            faq11ContentLL!!.visibility = View.GONE
            arrow11!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_arrow_head_down_black))
        }
    }

    @OnClick(R.id.faq12_rl)
    internal fun clickFaq12() {
        if (isShow[11] == false) {
            isShow[11] = true
            faq12ContentLL!!.visibility = View.VISIBLE
            arrow12!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_resume_up_arrow))
        } else {
            isShow[11] = false
            faq12ContentLL!!.visibility = View.GONE
            arrow12!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_arrow_head_down_black))
        }
    }

    @OnClick(R.id.faq13_rl)
    internal fun clickFaq13() {
        if (isShow[12] == false) {
            isShow[12] = true
            faq13ContentLL!!.visibility = View.VISIBLE
            arrow13!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_resume_up_arrow))
        } else {
            isShow[12] = false
            faq13ContentLL!!.visibility = View.GONE
            arrow13!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_arrow_head_down_black))
        }
    }

    @OnClick(R.id.faq14_rl)
    internal fun clickFaq14() {
        if (isShow[13] == false) {
            isShow[13] = true
            faq14ContentLL!!.visibility = View.VISIBLE
            arrow14!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_resume_up_arrow))
        } else {
            isShow[13] = false
            faq14ContentLL!!.visibility = View.GONE
            arrow14!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_arrow_head_down_black))
        }
    }

    @OnClick(R.id.faq15_rl)
    internal fun clickFaq15() {
        if (isShow[14] == false) {
            isShow[14] = true
            faq15ContentLL!!.visibility = View.VISIBLE
            arrow15!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_resume_up_arrow))
        } else {
            isShow[14] = false
            faq15ContentLL!!.visibility = View.GONE
            arrow15!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_arrow_head_down_black))
        }
    }

    @OnClick(R.id.faq16_rl)
    internal fun clickFaq16() {
        if (isShow[15] == false) {
            isShow[15] = true
            faq16ContentLL!!.visibility = View.VISIBLE
            arrow16!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_resume_up_arrow))
        } else {
            isShow[15] = false
            faq16ContentLL!!.visibility = View.GONE
            arrow16!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_arrow_head_down_black))
        }
    }

    @OnClick(R.id.faq17_rl)
    internal fun clickFaq17() {
        if (isShow[16] == false) {
            isShow[16] = true
            faq17ContentLL!!.visibility = View.VISIBLE
            arrow17!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_resume_up_arrow))
        } else {
            isShow[16] = false
            faq17ContentLL!!.visibility = View.GONE
            arrow17!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_arrow_head_down_black))
        }
    }

    @OnClick(R.id.faq18_rl)
    internal fun clickFaq18() {
        if (isShow[17] == false) {
            isShow[17] = true
            faq18ContentLL!!.visibility = View.VISIBLE
            arrow18!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_resume_up_arrow))
        } else {
            isShow[17] = false
            faq18ContentLL!!.visibility = View.GONE
            arrow18!!.setImageDrawable(activity.resources.getDrawable(R.mipmap.ic_arrow_head_down_black))
        }
    }

    companion object {

        val EXTRA_DISPLAY_FRAGMENT = "ClassroomFAQFragment.EXTRA_DISPLAY_FRAGMENT"
    }
}
