package inc.osbay.android.tutorroom.ui.fragment

import android.app.Dialog
import android.app.Fragment
import android.app.FragmentManager
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.adapter.MainCreditPackageAdapter
import inc.osbay.android.tutorroom.sdk.client.ServerError
import inc.osbay.android.tutorroom.sdk.client.ServerRequestManager
import inc.osbay.android.tutorroom.sdk.client.ServerResponse
import inc.osbay.android.tutorroom.sdk.constant.CommonConstant
import inc.osbay.android.tutorroom.sdk.model.CreditPackage
import inc.osbay.android.tutorroom.ui.activity.FragmentHolderActivity
import inc.osbay.android.tutorroom.utils.SharedPreferenceData

class StoreFragment : BackHandledFragment(), MainCreditPackageAdapter.OnItemClicked {

    @BindView(R.id.tool_bar)
    internal var toolBar: Toolbar? = null
    internal var sharedPreferences: SharedPreferenceData
    @BindView(R.id.credit_ammount)
    internal var creditTV: TextView? = null
    @BindView(R.id.main_credit_package)
    internal var creditPackageRV: RecyclerView? = null
    @BindView(R.id.sb_select_credit)
    internal var creditBar: SeekBar? = null
    @BindView(R.id.tv_selected_credit)
    internal var buyingCreditTV: TextView? = null
    @BindView(R.id.tv_total_cost)
    internal var costTV: TextView? = null
    @BindView(R.id.buy_dynamic)
    internal var buyDynamicTV: TextView? = null
    private var mServerRequestManager: ServerRequestManager? = null
    private var accountId: String? = null
    private val creditPackageList = ArrayList<CreditPackage>()
    private var creditAmount: Double = 0.toDouble()
    private val minAmount = 1
    private var creditPackage: CreditPackage? = null

    override fun onBackPressed(): Boolean {
        activity.finish()
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = SharedPreferenceData(activity)
        accountId = sharedPreferences.getInt("account_id").toString()
        creditAmount = sharedPreferences.getDouble("credit_amount")
        mServerRequestManager = ServerRequestManager(activity.applicationContext)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle): View? {
        val view = inflater.inflate(R.layout.fragment_store, container, false)
        ButterKnife.bind(this, view)

        toolBar!!.setBackgroundColor(activity.resources.getColor(R.color.colorPrimary))
        setSupportActionBar(toolBar)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        creditBar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                /*if (progress < minAmount) {
                    progress = minAmount;
                    seekBar.setProgress(progress);
                }*/
                seekBar.progress = i
                buyingCreditTV!!.text = i.toString()
                val amount = i * creditAmount
                costTV!!.text = amount.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })
        buyingCreditTV!!.text = getString(R.string._1)
        costTV!!.text = (1 * creditAmount).toString()
    }

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        setTitle(getString(R.string.store))
        setDisplayHomeAsUpEnable(true)
    }

    @OnClick(R.id.buy_dynamic)
    internal fun buyDynamic() {
        buyCredit(CommonConstant.buyCredit, buyingCreditTV!!.text.toString(), null)
    }

    override fun onResume() {
        super.onResume()
        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage(getString(R.string.loading))
        progressDialog.show()

        mServerRequestManager!!.getAccountCredit(accountId, object : ServerRequestManager.OnRequestFinishedListener {
            override fun onSuccess(response: ServerResponse?) {
                try {
                    val dataObj = JSONObject(response!!.dataSt)
                    val creditSt = dataObj.getString("credit")
                    creditTV!!.text = creditSt

                    mServerRequestManager!!.getPriceTier(object : ServerRequestManager.OnRequestFinishedListener {
                        override fun onSuccess(result: ServerResponse?) {
                            progressDialog.dismiss()
                            if (result!!.code == 1) {
                                creditPackageList.clear()
                                creditPackageRV!!.visibility = View.VISIBLE
                                val tagJsonArray: JSONArray
                                try {
                                    tagJsonArray = JSONArray(result.dataSt)
                                    for (i in 0 until tagJsonArray.length()) {
                                        val creditPackage = CreditPackage(tagJsonArray.getJSONObject(i))
                                        creditPackageList.add(creditPackage)
                                    }
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }

                                showData()
                            } else {
                                creditPackageRV!!.visibility = View.GONE
                            }
                        }

                        override fun onError(err: ServerError) {
                            progressDialog.dismiss()
                            Log.i("Get Price Tier Failed", err.message)
                        }
                    })
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onError(err: ServerError) {
                progressDialog.dismiss()
            }
        })
    }

    internal fun showData() {
        val creditPackageAdapter = MainCreditPackageAdapter(creditPackageList, this)
        val mLayoutManager = LinearLayoutManager(activity)
        creditPackageRV!!.layoutManager = mLayoutManager
        creditPackageRV!!.itemAnimator = DefaultItemAnimator()
        creditPackageRV!!.adapter = creditPackageAdapter
        creditPackageAdapter.notifyDataSetChanged()
    }

    @OnClick(R.id.price_tier)
    internal fun openCreditTier() {
        val fm = fragmentManager
        val frg = fm.findFragmentById(R.id.framelayout)
        val fragment = CreditTierFragment()
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

    fun buyCredit(buyType: Int, credit: String?, packageID: String?) {
        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage(getString(R.string.loading))
        progressDialog.show()
        mServerRequestManager!!.buyCredit(accountId, buyType, credit, packageID,
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return true
    }

    override fun onItemClick(creditPackage: CreditPackage) {
        this.creditPackage = creditPackage
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
            buyCredit(CommonConstant.buyStorePackage, null, creditPackage.creditID)
        }
        dialog.show()
    }
}