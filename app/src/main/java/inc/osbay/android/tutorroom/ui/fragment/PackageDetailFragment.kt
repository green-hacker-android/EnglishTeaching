package inc.osbay.android.tutorroom.ui.fragment

import android.app.Fragment
import android.app.FragmentManager
import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.facebook.drawee.view.SimpleDraweeView

import org.json.JSONArray
import org.json.JSONException

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.adapter.LessonByPackageDetailAdapter
import inc.osbay.android.tutorroom.sdk.client.ServerError
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager
import inc.osbay.android.tutorroom.sdk.client.ServerResponse
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant
import inc.osbay.android.tutorroom.sdk.database.DBAdapter
import inc.osbay.android.tutorroom.sdk.model.Lesson
import inc.osbay.android.tutorroom.sdk.model.Packagee
import inc.osbay.android.tutorroom.utils.SharedPreferenceData

class PackageDetailFragment : BackHandledFragment(), LessonByPackageDetailAdapter.OnItemClicked {
    @BindView(R.id.tool_bar)
    internal var toolBar: Toolbar? = null
    @BindView(R.id.lesson_rv)
    internal var lessonRV: RecyclerView? = null
    @BindView(R.id.package_name)
    internal var packageName: TextView? = null
    @BindView(R.id.package_desc)
    internal var packageDesc: TextView? = null
    @BindView(R.id.lesson_count)
    internal var lessonCount: TextView? = null
    @BindView(R.id.total_time)
    internal var totalTime: TextView? = null
    @BindView(R.id.credit)
    internal var credit: TextView? = null
    @BindView(R.id.no_lesson)
    internal var noLesson: TextView? = null
    @BindView(R.id.package_cover)
    internal var packageCover: SimpleDraweeView? = null
    private val lessonList = ArrayList<Lesson>()
    private var sharedPreferences: SharedPreferenceData? = null
    private var mServerRequestManager: ServerRequestManager? = null
    private var packageeID: String? = null
    private var packagee: Packagee? = null

    override fun onBackPressed(): Boolean {
        fragmentManager.popBackStack()
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = SharedPreferenceData(activity)
        //accountId = String.valueOf(sharedPreferences.getInt("account_id"));
        mServerRequestManager = ServerRequestManager(activity.applicationContext)
        packageeID = arguments.getString(PackageDetailFragment_EXTRA)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle): View? {
        val view = inflater.inflate(R.layout.fragment_package_detail, container, false)
        ButterKnife.bind(this, view)

        toolBar!!.setBackgroundColor(activity.resources.getColor(R.color.colorPrimary))
        setSupportActionBar(toolBar)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage(getString(R.string.loading))
        progressDialog.show()
        mServerRequestManager!!.getLessonListByPackageID(packageeID, object : ServerRequestManager.OnRequestFinishedListener {
            override fun onSuccess(response: ServerResponse?) {
                progressDialog.dismiss()
                if (response!!.code == 1) {
                    var lessonJsonArray: JSONArray?
                    val packageJsonArray: JSONArray
                    try {
                        packageJsonArray = JSONArray(response.dataSt)
                        for (i in 0 until packageJsonArray.length()) {
                            packagee = Packagee(packageJsonArray.getJSONObject(i))
                            lessonJsonArray = packagee!!.lessonJsonArray
                            for (j in 0 until lessonJsonArray!!.length()) {
                                val packageObj = Lesson(lessonJsonArray!!.getJSONObject(j))
                                lessonList.add(packageObj)
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    showLessonList()
                } else {
                    noLesson!!.visibility = View.VISIBLE
                    lessonRV!!.visibility = View.GONE
                }
            }

            override fun onError(err: ServerError) {
                progressDialog.dismiss()
            }
        })
    }

    internal fun showLessonList() {
        val cover = packagee!!.coverImg
        packageCover!!.setImageURI(Uri.parse(cover))
        packageName!!.text = packagee!!.packageName
        packageDesc!!.text = packagee!!.packageDescription
        lessonCount!!.text = packagee!!.lessonCount.toString()
        totalTime!!.text = packagee!!.totalTime.toString()
        credit!!.text = packagee!!.packagePrice.toString()

        if (lessonList.size > 0) {
            noLesson!!.visibility = View.GONE
            lessonRV!!.visibility = View.VISIBLE
            val lessonAdapter = LessonByPackageDetailAdapter(lessonList, activity, this)
            val mLayoutManager = LinearLayoutManager(activity)
            lessonRV!!.layoutManager = mLayoutManager
            lessonRV!!.itemAnimator = DefaultItemAnimator()
            lessonRV!!.adapter = lessonAdapter
            lessonAdapter.notifyDataSetChanged()
        } else {
            noLesson!!.visibility = View.VISIBLE
            lessonRV!!.visibility = View.GONE
        }
    }

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        setTitle(getString(R.string.packagee_detail))
        setDisplayHomeAsUpEnable(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return true
    }

    override fun onItemClick(lesson: Lesson) {
        val fm = fragmentManager
        val frg = fm.findFragmentById(R.id.framelayout)
        val fragment = LessonDetailFragment()

        val bundle = Bundle()
        bundle.putSerializable("lesson", lesson)
        bundle.putString("tag_id", "0")
        bundle.putString("package_id", packageeID)
        bundle.putString("lesson_type", CommonConstant.packageLessonType.toString())
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

    companion object {

        var PackageDetailFragment_EXTRA = "PackageDetailFragment.EXTRA"
    }
}