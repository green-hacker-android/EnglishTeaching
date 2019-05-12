package inc.osbay.android.tutorroom.ui.fragment

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONException
import java.util.ArrayList
import java.util.Objects
import butterknife.BindView
import butterknife.ButterKnife
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.adapter.CreditTierAdapter
import inc.osbay.android.tutorroom.sdk.client.ServerError
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager
import inc.osbay.android.tutorroom.sdk.client.ServerResponse
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant
import inc.osbay.android.tutorroom.sdk.model.CreditPackage
import inc.osbay.android.tutorroom.ui.activity.FragmentHolderActivity
import inc.osbay.android.tutorroom.utils.SharedPreferenceData

class CreditTierFragment : BackHandledFragment(), CreditTierAdapter.OnItemClicked {
    @BindView(R.id.tool_bar)
    internal var toolBar: Toolbar? = null
    @BindView(R.id.credit_rv)
    internal var creditPackageRV: RecyclerView? = null
    @BindView(R.id.main_rl)
    internal var mainRL: RelativeLayout? = null
    @BindView(R.id.no_data)
    internal var noDataTV: TextView? = null
    private var mRequestManager: ServerRequestManager? = null
    private var accountId: String? = null
    private val creditPackageList = ArrayList<CreditPackage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferenceData = SharedPreferenceData(Objects.requireNonNull<Activity>(activity))
        mRequestManager = ServerRequestManager(activity)
        accountId = sharedPreferenceData.getInt("account_id").toString()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle): View? {
        val view = inflater.inflate(R.layout.fragment_credit_tier, container, false)
        ButterKnife.bind(this, view)

        toolBar!!.setBackgroundColor(activity.resources.getColor(R.color.colorPrimary))
        setSupportActionBar(toolBar)
        return view
    }

    override fun onResume() {
        super.onResume()
        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage(getString(R.string.loading))
        progressDialog.show()

        mRequestManager!!.getPriceTierList(object : ServerRequestManager.OnRequestFinishedListener {
            override fun onSuccess(response: ServerResponse?) {
                progressDialog.dismiss()

                if (response!!.code == 1) {
                    creditPackageList.clear()
                    mainRL!!.visibility = View.VISIBLE
                    noDataTV!!.visibility = View.GONE
                    val tagJsonArray: JSONArray
                    try {
                        tagJsonArray = JSONArray(response.dataSt)
                        for (i in 0 until tagJsonArray.length()) {
                            val creditPackage = CreditPackage(tagJsonArray.getJSONObject(i))
                            creditPackageList.add(creditPackage)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    showData()
                } else {
                    mainRL!!.visibility = View.GONE
                    noDataTV!!.visibility = View.VISIBLE
                }
            }

            override fun onError(err: ServerError) {
                progressDialog.dismiss()

            }
        })
    }

    internal fun showData() {
        val creditPackageAdapter = CreditTierAdapter(creditPackageList, this)
        val mLayoutManager = LinearLayoutManager(activity)
        creditPackageRV!!.layoutManager = mLayoutManager
        creditPackageRV!!.itemAnimator = DefaultItemAnimator()
        creditPackageRV!!.adapter = creditPackageAdapter
        creditPackageAdapter.notifyDataSetChanged()
    }

    override fun onBackPressed(): Boolean {
        fragmentManager.popBackStack()
        return false
    }

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        setTitle(getString(R.string.store))
        setDisplayHomeAsUpEnable(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return true
    }

    override fun onItemClick(creditPackage: CreditPackage) {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.buy_credit_package_layout)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.setCancelable(false)

        val closeImg = dialog.findViewById<ImageView>(R.id.close_img)
        val creditContent = dialog.findViewById<TextView>(R.id.credit_content)
        val creditTitle = dialog.findViewById<TextView>(R.id.credit_title)
        val buy = dialog.findViewById<TextView>(R.id.buy)
        creditContent.text = creditPackage.packageAmount.toString()
        creditTitle.text = creditPackage.packageCredit.toString()

        closeImg.setOnClickListener { view -> dialog.dismiss() }

        buy.setOnClickListener { view ->
            dialog.dismiss()
            val progressDialog = ProgressDialog(activity)
            progressDialog.setMessage(getString(R.string.loading))
            progressDialog.show()
            mRequestManager!!.buyCredit(accountId, CommonConstant.buyStorePackage, null, creditPackage.creditID,
                    object : ServerRequestManager.OnRequestFinishedListener {
                        override fun onSuccess(response: ServerResponse?) {
                            if (response!!.code == 1) {
                                progressDialog.dismiss()
                                val url = response.dataSt
                                val intent = Intent(activity, FragmentHolderActivity::class.java)
                                intent.putExtra(FragmentHolderActivity.EXTRA_DISPLAY_FRAGMENT, WebviewFragment::class.java!!.getSimpleName())
                                intent.putExtra(WebviewFragment.WEBVIEW_EXTRA, url)
                                intent.putExtra(WebviewFragment.TITLE_EXTRA, getString(R.string.store))
                                startActivity(intent)
                            }
                        }

                        override fun onError(err: ServerError) {
                            progressDialog.dismiss()
                            Toast.makeText(activity, resources.getString(R.string.faq_8), Toast.LENGTH_LONG).show()
                        }
                    })
        }
        dialog.show()
    }
}
