package inc.osbay.android.tutorroom.ui.fragment

import android.app.Fragment
import android.app.FragmentManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.GravityCompat
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.view.SimpleDraweeView

import org.json.JSONArray
import org.json.JSONException

import java.text.ParseException
import java.util.ArrayList
import java.util.Timer
import java.util.TimerTask

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.sdk.client.ServerError
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager
import inc.osbay.android.tutorroom.sdk.client.ServerResponse
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant
import inc.osbay.android.tutorroom.sdk.database.DBAdapter
import inc.osbay.android.tutorroom.sdk.database.TutorAdapter
import inc.osbay.android.tutorroom.sdk.model.Banner
import inc.osbay.android.tutorroom.sdk.model.Notification
import inc.osbay.android.tutorroom.sdk.model.Tutor
import inc.osbay.android.tutorroom.ui.activity.FragmentHolderActivity
import inc.osbay.android.tutorroom.utils.SharedPreferenceData

class MainFragment : BackHandledFragment() {

    @BindView(R.id.tool_bar)
    internal var toolBar: Toolbar? = null
    @BindView(R.id.drawer_layout)
    internal var mDrawerLayout: DrawerLayout? = null
    @BindView(R.id.rl_main_content)
    internal var frame: RelativeLayout? = null
    @BindView(R.id.vp_main_slider)
    internal var bannerPager: ViewPager? = null

    private var mDrawerToggle: ActionBarDrawerToggle? = null
    private var requestManager: ServerRequestManager? = null
    private var accountID: String? = null
    private val bannerList = ArrayList<Banner>()
    private var mCurrentPage: Int = 0
    private var mPageNo: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferenceData = SharedPreferenceData(activity)
        accountID = sharedPreferenceData.getInt("account_id").toString()
        requestManager = ServerRequestManager(activity)
        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage(getString(R.string.loading))
        progressDialog.show()
        requestManager!!.getLessonListByAllTag(object : ServerRequestManager.OnRequestFinishedListener {
            override fun onSuccess(result: ServerResponse?) {
                getTutorList(progressDialog)
            }

            override fun onError(err: ServerError) {
                progressDialog.dismiss()
                Toast.makeText(activity, resources.getString(R.string.get_lesson_err), Toast.LENGTH_LONG).show()
            }
        })
    }

    fun getTutorList(progressDialog: ProgressDialog) {
        requestManager!!.getTutorList(object : ServerRequestManager.OnRequestFinishedListener {
            override fun onSuccess(result: ServerResponse?) {
                if (result!!.code == 1) {//For Success Response
                    val dbAdapter = TutorAdapter(activity)
                    try {
                        val jsonArray = JSONArray(result.dataSt)
                        val tutors = ArrayList<Tutor>()
                        for (i in 0 until jsonArray.length()) {
                            val tutor = Tutor(jsonArray.getJSONObject(i))
                            tutors.add(tutor)
                        }
                        dbAdapter.insertTutors(tutors)
                    } catch (je: JSONException) {
                        Log.e(CommonConstant.TAG, "Cannot parse Tutor Object", je)
                    }

                    getNotification(progressDialog)
                }
            }

            override fun onError(err: ServerError) {
                progressDialog.dismiss()
                if (activity != null) {
                    Toast.makeText(activity, getString(R.string.tu_lst_refresh_failed), Toast.LENGTH_SHORT)
                            .show()
                }
            }
        })
    }

    internal fun getNotification(progressDialog: ProgressDialog) {
        requestManager!!.getNoti(accountID, object : ServerRequestManager.OnRequestFinishedListener {
            override fun onSuccess(response: ServerResponse?) {
                progressDialog.dismiss()
                if (response!!.code == 1) {
                    val notificationList = ArrayList<Notification>()
                    try {
                        val dataArray = JSONArray(response.dataSt)
                        for (i in 0 until dataArray.length()) {
                            val notification = Notification(dataArray.getJSONObject(i))
                            notificationList.add(notification)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }

                    val dbAdapter = DBAdapter(activity)
                    dbAdapter.insertNotifications(notificationList)
                }
            }

            override fun onError(err: ServerError) {
                progressDialog.dismiss()
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(
                R.layout.fragment_main, container, false)
        ButterKnife.bind(this, rootView)

        toolBar!!.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        setTitle(resources.getString(R.string.app_name))

        // Implement Left Menu Drawer
        val leftMenuDrawerFragment = LeftMenuDrawerFragment()
        val fragmentManager = childFragmentManager
        val oldDrawer = fragmentManager.findFragmentById(R.id.left_menu_drawer)
        if (oldDrawer == null) {
            fragmentManager.beginTransaction().add(R.id.left_menu_drawer, leftMenuDrawerFragment)
                    .commitAllowingStateLoss()
        } else {
            fragmentManager.beginTransaction().replace(R.id.left_menu_drawer, leftMenuDrawerFragment)
                    .commitAllowingStateLoss()
        }
        mDrawerToggle = object : ActionBarDrawerToggle(
                activity,
                mDrawerLayout,
                toolBar,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            override fun onDrawerSlide(drawerView: View?, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                val moveFactor = resources.getDimension(R.dimen.dp250) * slideOffset
                frame!!.translationX = moveFactor
            }

            override fun onDrawerOpened(drawerView: View?) {
                super.onDrawerOpened(drawerView)

                val requestManager = ServerRequestManager(
                        activity.applicationContext)
            }

            override fun onDrawerClosed(drawerView: View?) {
                super.onDrawerClosed(drawerView)
            }
        }

        mDrawerToggle!!.isDrawerIndicatorEnabled = false
        toolBar!!.navigationIcon = createImage()
        toolBar!!.setNavigationOnClickListener { view ->
            if (mDrawerLayout!!.isDrawerOpen(Gravity.START)) {
                mDrawerLayout!!.closeDrawer(GravityCompat.START, false)
                mDrawerLayout!!.addDrawerListener(mDrawerToggle!!)
                mDrawerToggle!!.isDrawerSlideAnimationEnabled = true
            } else {
                mDrawerLayout!!.openDrawer(Gravity.START)
            }
        }
        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout!!.post { mDrawerToggle!!.syncState() }
        mDrawerLayout!!.addDrawerListener(mDrawerToggle!!)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestManager!!.getBanner(object : ServerRequestManager.OnRequestFinishedListener {
            override fun onSuccess(response: ServerResponse?) {
                if (response!!.code == 1) {
                    try {
                        val dataArray = JSONArray(response.dataSt)
                        for (i in 0 until dataArray.length()) {
                            val banner = Banner(dataArray.getJSONObject(i))
                            if (banner.bannerType!!.equals(CommonConstant.MB_HomeBanner.toString(), ignoreCase = true)) {
                                bannerList.add(banner)
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    bannerPager!!.adapter = MainImageSlideAdapter(activity)
                    mPageNo = bannerList.size
                    mCurrentPage = 0

                    val handler = Handler()
                    val runnable = {
                        if (mCurrentPage == mPageNo) {
                            mCurrentPage = 0
                        }

                        bannerPager!!.setCurrentItem(mCurrentPage++, true)
                    }

                    val swipeTimer = Timer()
                    swipeTimer.schedule(object : TimerTask() {
                        override fun run() {
                            handler.post(runnable)
                        }
                    }, 3000, 3000)

                    bannerPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                            mCurrentPage = position
                        }

                        override fun onPageSelected(position: Int) {

                        }

                        override fun onPageScrollStateChanged(state: Int) {

                        }
                    })
                }
            }

            override fun onError(err: ServerError) {

            }
        })
    }

    fun closeDrawer() {
        if (mDrawerLayout!!.isDrawerOpen(GravityCompat.START))
            mDrawerLayout!!.closeDrawer(GravityCompat.START, false)
    }

    @OnClick(R.id.package_imv)
    internal fun clickPackage() {
        val intent = Intent(activity, FragmentHolderActivity::class.java)
        intent.putExtra(FragmentHolderActivity.EXTRA_DISPLAY_FRAGMENT, PackageFragment::class.java!!.getSimpleName())
        startActivity(intent)
    }

    @OnClick(R.id.lesson_imv)
    internal fun clickSingleBooking() {
        val intent = Intent(activity, FragmentHolderActivity::class.java)
        intent.putExtra(FragmentHolderActivity.EXTRA_DISPLAY_FRAGMENT, LessonFragment::class.java!!.getSimpleName())
        startActivity(intent)
    }

    @OnClick(R.id.tutor_imv)
    internal fun clickTutor() {
        val intent = Intent(activity, FragmentHolderActivity::class.java)
        intent.putExtra(FragmentHolderActivity.EXTRA_DISPLAY_FRAGMENT, TutorListFragment::class.java!!.getSimpleName())
        startActivity(intent)
    }

    @OnClick(R.id.schedule_img)
    internal fun clickSchedule() {
        val intent = Intent(activity, FragmentHolderActivity::class.java)
        intent.putExtra(FragmentHolderActivity.EXTRA_DISPLAY_FRAGMENT, ScheduleFragment::class.java!!.getSimpleName())
        startActivity(intent)
    }

    @OnClick(R.id.store_imv)
    internal fun clickStore() {
        val intent = Intent(activity, FragmentHolderActivity::class.java)
        intent.putExtra(FragmentHolderActivity.EXTRA_DISPLAY_FRAGMENT, StoreFragment::class.java!!.getSimpleName())
        startActivity(intent)
    }

    @OnClick(R.id.online_support_img)
    internal fun clickOnlineSupport() {
        val intent = Intent(activity, FragmentHolderActivity::class.java)
        intent.putExtra(FragmentHolderActivity.EXTRA_DISPLAY_FRAGMENT, OnlineSupportFragment::class.java!!.getSimpleName())
        startActivity(intent)
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun setTitle(title: String) {
        if (toolBar != null) {
            val tvTitle = toolBar!!.findViewById<TextView>(R.id.tv_toolbar_title)
            //tvTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/microsoft_jhenghei.ttf"));
            tvTitle.text = title
            tvTitle.setCompoundDrawables(null, null, null, null)
            tvTitle.setOnClickListener(null)
        }
    }

    fun createImage(): Drawable {
        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.menu_56)
        return BitmapDrawable(resources, bitmap)
    }

    private inner class MainImageSlideAdapter private constructor(private val mContext: Context) : PagerAdapter() {

        override fun getCount(): Int {
            return bannerList.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val sdvMainImage = SimpleDraweeView(mContext)
            sdvMainImage.hierarchy.actualImageScaleType = ScalingUtils.ScaleType.FIT_XY
            val uri = Uri.parse(bannerList[position].image)
            sdvMainImage.setImageURI(uri)
            container.addView(sdvMainImage)
            return sdvMainImage
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as SimpleDraweeView)
        }
    }
}