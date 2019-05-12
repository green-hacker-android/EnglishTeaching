package inc.osbay.android.tutorroom.ui.fragment

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
import android.widget.TextView
import android.widget.Toast

import org.json.JSONArray
import org.json.JSONException

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.adapter.PackageByAllTagAdapter
import inc.osbay.android.tutorroom.sdk.client.ServerError
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager
import inc.osbay.android.tutorroom.sdk.client.ServerResponse
import inc.osbay.android.tutorroom.sdk.model.Tag
import inc.osbay.android.tutorroom.utils.SharedPreferenceData

class PackageFragment : BackHandledFragment() {

    internal var sharedPreferences: SharedPreferenceData
    @BindView(R.id.tool_bar)
    internal var toolBar: Toolbar? = null
    @BindView(R.id.package_rv)
    internal var tagRV: RecyclerView? = null
    @BindView(R.id.no_data)
    internal var noDataTV: TextView? = null
    internal var tagList: MutableList<Tag> = ArrayList()
    private var mServerRequestManager: ServerRequestManager? = null

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
        return view
    }

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        setTitle(getString(R.string.packagee))
        setDisplayHomeAsUpEnable(true)
    }

    override fun onResume() {
        super.onResume()
        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage(getString(R.string.loading))
        progressDialog.show()

        mServerRequestManager!!.getPackageListByAllTag(object : ServerRequestManager.OnRequestFinishedListener {
            override fun onSuccess(response: ServerResponse?) {
                progressDialog.dismiss()
                if (response!!.code == 1) {
                    tagList.clear()
                    val tagJsonArray: JSONArray
                    try {
                        tagJsonArray = JSONArray(response.dataSt)
                        for (i in 0 until tagJsonArray.length()) {
                            val tag = Tag(tagJsonArray.getJSONObject(i))
                            tagList.add(tag)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    val packageAdapter = PackageByAllTagAdapter(tagList, activity)
                    val mLayoutManager = LinearLayoutManager(activity)
                    tagRV!!.layoutManager = mLayoutManager
                    tagRV!!.itemAnimator = DefaultItemAnimator()
                    tagRV!!.adapter = packageAdapter
                    packageAdapter.notifyDataSetChanged()
                } else {
                    tagRV!!.visibility = View.GONE
                    noDataTV!!.visibility = View.VISIBLE
                    noDataTV!!.text = getString(R.string.no_package)
                }
            }

            override fun onError(err: ServerError) {
                progressDialog.dismiss()
                Toast.makeText(activity, getString(R.string.check_internet), Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return true
    }
}
