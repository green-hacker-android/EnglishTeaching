package inc.osbay.android.tutorroom.ui.fragment

import android.app.Fragment
import android.app.FragmentManager
import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

import org.json.JSONArray
import org.json.JSONException

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.adapter.LessonBookingAdapter
import inc.osbay.android.tutorroom.adapter.TagAdapter
import inc.osbay.android.tutorroom.sdk.client.ServerError
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager
import inc.osbay.android.tutorroom.sdk.client.ServerResponse
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant
import inc.osbay.android.tutorroom.sdk.database.DBAdapter
import inc.osbay.android.tutorroom.sdk.model.Lesson
import inc.osbay.android.tutorroom.sdk.model.Packagee
import inc.osbay.android.tutorroom.sdk.model.Tag

class SingleBookingChooseLessonFragment : BackHandledFragment(), LessonBookingAdapter.OnItemClicked {
    internal var mTagAdapter: TagAdapter
    internal var mLessonBookingAdapter: LessonBookingAdapter
    @BindView(R.id.package_spinner)
    internal var tagSpinner: Spinner? = null
    @BindView(R.id.lesson_rv)
    internal var lessonRV: RecyclerView? = null
    @BindView(R.id.tool_bar)
    internal var toolBar: Toolbar? = null
    @BindView(R.id.no_data)
    internal var noDataTV: TextView? = null
    private var mServerRequestManager: ServerRequestManager? = null
    private val tagList = ArrayList<Tag>()
    private val lessonList = ArrayList<Lesson>()
    private var lessonID: Int = 0
    private var tagID: String? = null
    private var packageID: String? = null
    private var lessonType: String? = null
    private var dbAdapter: DBAdapter? = null

    override fun onBackPressed(): Boolean {
        if (arguments.getString(Booking_EXTRA) == LessonDetailFragment::class.java!!.getSimpleName())
            fragmentManager.popBackStack()
        else
            activity.finish()
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbAdapter = DBAdapter(activity)
        mServerRequestManager = ServerRequestManager(activity.applicationContext)
        lessonType = arguments.getString("lesson_type")
        if (arguments.getString(Booking_EXTRA) == LessonDetailFragment::class.java!!.getSimpleName()) {
            tagID = arguments.getString("tag_id")
            lessonID = Integer.parseInt(arguments.getString("lesson_id")!!)
            if (tagID!!.equals("0", ignoreCase = true)) packageID = arguments.getString("package_id")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle): View? {
        val view = inflater.inflate(R.layout.fragment_single_booking_choose_lesson, container, false)
        ButterKnife.bind(this, view)

        toolBar!!.setBackgroundColor(activity.resources.getColor(R.color.colorPrimary))
        setSupportActionBar(toolBar)
        return view
    }

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        setTitle(getString(R.string.single_book))
        setDisplayHomeAsUpEnable(true)
    }

    override fun onResume() {
        super.onResume()
        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage(getString(R.string.loading))
        progressDialog.show()
        if (arguments.getString(Booking_EXTRA) == LessonDetailFragment::class.java!!.getSimpleName()) {
            if (tagID!!.equals("0", ignoreCase = true)) {
                tagSpinner!!.visibility = View.GONE
                mServerRequestManager!!.getLessonListByPackageID(packageID, object : ServerRequestManager.OnRequestFinishedListener {
                    override fun onSuccess(response: ServerResponse?) {
                        lessonList.clear()
                        progressDialog.dismiss()
                        if (response!!.code == 1) {
                            var lessonJsonArray: JSONArray?
                            val packageJsonArray: JSONArray
                            try {
                                packageJsonArray = JSONArray(response.dataSt)
                                for (i in 0 until packageJsonArray.length()) {
                                    val packagee = Packagee(packageJsonArray.getJSONObject(i))
                                    lessonJsonArray = packagee.lessonJsonArray
                                    for (j in 0 until lessonJsonArray!!.length()) {
                                        val packageObj = Lesson(lessonJsonArray!!.getJSONObject(j))
                                        lessonList.add(packageObj)
                                    }
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }

                            dbAdapter!!.insertLessons(lessonList)
                            setLessonList()
                        } else {
                            noDataTV!!.visibility = View.VISIBLE
                            noDataTV!!.text = getString(R.string.no_lesson)
                            lessonRV!!.visibility = View.GONE
                        }
                    }

                    override fun onError(err: ServerError) {
                        progressDialog.dismiss()
                    }
                })
            } else {
                showTagList(progressDialog)
            }
        } else if (arguments.getString(Booking_EXTRA) == ScheduleFragment::class.java!!.getSimpleName()) {
            showTagList(progressDialog)
        }
    }

    internal fun showTagList(progressDialog: ProgressDialog) {
        mServerRequestManager!!.getTagList(CommonConstant.LessonTag.toString(), object : ServerRequestManager.OnRequestFinishedListener {
            override fun onSuccess(result: ServerResponse?) {
                if (activity != null) {
                    tagList.clear()
                    val tagJsonArray: JSONArray
                    try {
                        tagJsonArray = JSONArray(result!!.dataSt)
                        for (i in 0 until tagJsonArray.length()) {
                            val tag = Tag(tagJsonArray.getJSONObject(i))
                            tagList.add(tag)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    mTagAdapter = TagAdapter(activity, tagList)
                    tagSpinner!!.adapter = mTagAdapter
                    /*tagSpinner.setDropDownListItem(packageList);
                    tagSpinner.setOnSelectionListener(new OnDropDownSelectionListener() {
                        @Override
                        public void onItemSelected(DropDownView view, int position) {
                            //Do something with the selected position
                        }
                    });*/
                    for (i in tagList.indices) {
                        if (tagList[i].tagID == tagID) {
                            tagSpinner!!.setSelection(i)
                            break
                        }
                    }

                    tagSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                            mServerRequestManager!!.getLessonByTagID(tagList[pos].tagID, object : ServerRequestManager.OnRequestFinishedListener {
                                override fun onSuccess(result: ServerResponse?) {
                                    progressDialog.dismiss()
                                    if (result!!.code == 1)
                                    //For Success Situation
                                    {
                                        lessonList.clear()
                                        var lessonJsonArray: JSONArray?
                                        val tagJsonArray: JSONArray
                                        try {
                                            tagJsonArray = JSONArray(result.dataSt)
                                            for (i in 0 until tagJsonArray.length()) {
                                                val tag = Tag(tagJsonArray.getJSONObject(i))
                                                lessonJsonArray = tag.lessonArray
                                                for (j in 0 until lessonJsonArray!!.length()) {
                                                    val packageObj = Lesson(lessonJsonArray!!.getJSONObject(j))
                                                    lessonList.add(packageObj)
                                                }
                                            }
                                        } catch (e: JSONException) {
                                            e.printStackTrace()
                                        }

                                        dbAdapter!!.insertLessons(lessonList)
                                        setLessonList()
                                    } else
                                    //For No Data Situation
                                    {
                                        lessonRV!!.visibility = View.GONE
                                        noDataTV!!.visibility = View.VISIBLE
                                        noDataTV!!.text = getString(R.string.no_lesson)
                                    }
                                }

                                override fun onError(err: ServerError) {
                                    progressDialog.dismiss()
                                    if (activity != null) {
                                        Toast.makeText(activity, getString(R.string.lesson_lst_refresh_failed), Toast.LENGTH_SHORT)
                                                .show()
                                    }
                                }
                            })
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {

                        }
                    }
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

    internal fun setLessonList() {
        mLessonBookingAdapter = LessonBookingAdapter(lessonList, lessonID, activity, this)
        val mLayoutManager = LinearLayoutManager(activity)
        lessonRV!!.layoutManager = mLayoutManager
        lessonRV!!.itemAnimator = DefaultItemAnimator()
        lessonRV!!.adapter = mLessonBookingAdapter
        lessonRV!!.visibility = View.VISIBLE
        noDataTV!!.visibility = View.GONE
    }

    @OnClick(R.id.next_tv)
    internal fun clickNext() {
        if (lessonID == 0) {
            Toast.makeText(activity, activity.getString(R.string.select_lesson), Toast.LENGTH_LONG)
                    .show()
        } else {
            val fm = fragmentManager
            val frg = fm.findFragmentById(R.id.framelayout)
            val fragment = SingleBookingChooseDateFragment()

            val bundle = Bundle()
            bundle.putString("lesson_id", lessonID.toString())
            bundle.putString("lesson_type", lessonType)
            fragment.arguments = bundle
            if (frg == null) {
                fm.beginTransaction()
                        .add(R.id.framelayout, fragment)
                        .addToBackStack(null)
                        .commit()
            } else {
                fm.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.framelayout, fragment)
                        .commit()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return true
    }

    override fun onItemClick(itemID: String?) {
        lessonID = Integer.parseInt(itemID!!)
    }

    companion object {
        var Booking_EXTRA = "Booking_EXTRA"
    }
}
