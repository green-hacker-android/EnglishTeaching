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
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.adapter.LessonAdapter
import inc.osbay.android.tutorroom.adapter.TagAdapter
import inc.osbay.android.tutorroom.sdk.client.ServerError
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager
import inc.osbay.android.tutorroom.sdk.client.ServerResponse
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant
import inc.osbay.android.tutorroom.sdk.model.Lesson
import inc.osbay.android.tutorroom.sdk.model.Tag
import inc.osbay.android.tutorroom.utils.SharedPreferenceData

class LessonFragment : BackHandledFragment(), LessonAdapter.OnItemClicked {

    internal var sharedPreferences: SharedPreferenceData
    @BindView(R.id.tool_bar)
    internal var toolBar: Toolbar? = null
    @BindView(R.id.package_rv)
    internal var lessonRV: RecyclerView? = null
    internal var mTagAdapter: TagAdapter
    internal var lessonAdapter: LessonAdapter
    @BindView(R.id.package_spinner)
    internal var lessonSpinner: Spinner? = null
    @BindView(R.id.no_data)
    internal var noData: TextView? = null
    private var mServerRequestManager: ServerRequestManager? = null
    private val tagList = ArrayList<Tag>()
    private val lessonList = ArrayList<Lesson>()
    private var tagID: String? = null

    override fun onBackPressed(): Boolean {
        activity.finish()
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = SharedPreferenceData(activity)
        mServerRequestManager = ServerRequestManager(activity.applicationContext)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle): View? {
        val view = inflater.inflate(R.layout.fragment_package, container, false)
        ButterKnife.bind(this, view)

        toolBar!!.setBackgroundColor(activity.resources.getColor(R.color.colorPrimary))
        setSupportActionBar(toolBar)
        lessonSpinner!!.visibility = View.VISIBLE
        return view
    }

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        setTitle(getString(R.string.lesson))
        setDisplayHomeAsUpEnable(true)
    }

    override fun onResume() {
        super.onResume()
        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage(getString(R.string.loading))
        progressDialog.show()
        if (tagList.size == 0) {
            progressDialog.show()
        }

        mServerRequestManager!!.getTagList(CommonConstant.LessonTag.toString(), object : ServerRequestManager.OnRequestFinishedListener {
            override fun onSuccess(result: ServerResponse?) {
                if (activity != null) {
                    if (result!!.code == 1) {
                        tagList.clear()
                        val tagJsonArray: JSONArray
                        try {
                            tagJsonArray = JSONArray(result.dataSt)
                            for (i in 0 until tagJsonArray.length()) {
                                val tag = Tag(tagJsonArray.getJSONObject(i))
                                tagList.add(tag)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                        mTagAdapter = TagAdapter(activity, tagList)
                        lessonSpinner!!.adapter = mTagAdapter
                        /*packageSpinner.setDropDownListItem(packageList);
                    packageSpinner.setOnSelectionListener(new OnDropDownSelectionListener() {
                        @Override
                        public void onItemSelected(DropDownView view, int position) {
                            //Do something with the selected position
                        }
                    });*/
                        lessonSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                                mServerRequestManager!!.getLessonByTagID(tagList[pos].tagID, object : ServerRequestManager.OnRequestFinishedListener {
                                    override fun onSuccess(result: ServerResponse?) {
                                        progressDialog.dismiss()
                                        if (result!!.code == 1)
                                        //For Success Situation
                                        {
                                            tagID = tagList[pos].tagID
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

                                            setLessonList()
                                        } else
                                        //For No Data Situation
                                        {
                                            lessonRV!!.visibility = View.GONE
                                            noData!!.visibility = View.VISIBLE
                                            noData!!.text = getString(R.string.no_lesson)
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
                                Toast.makeText(activity, getString(R.string.lesson_lst_refresh_failed), Toast.LENGTH_SHORT)
                                        .show()
                            }
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

    fun setLessonList() {
        lessonRV!!.visibility = View.VISIBLE
        noData!!.visibility = View.GONE
        lessonAdapter = LessonAdapter(lessonList, activity, this)
        val mLayoutManager = LinearLayoutManager(activity)
        lessonRV!!.layoutManager = mLayoutManager
        lessonRV!!.itemAnimator = DefaultItemAnimator()
        lessonRV!!.adapter = lessonAdapter
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
        bundle.putString("tag_id", tagID)
        bundle.putString("lesson_type", CommonConstant.singleLessonType.toString())
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
}