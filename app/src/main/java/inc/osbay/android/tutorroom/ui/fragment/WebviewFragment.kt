package inc.osbay.android.tutorroom.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import butterknife.BindView
import butterknife.ButterKnife
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.utils.WebViewClientImpl

class WebviewFragment : BackHandledFragment() {
    @BindView(R.id.tool_bar)
    internal var toolBar: Toolbar? = null
    @BindView(R.id.webview)
    internal var mWebView: WebView? = null
    internal var url: String? = null

    /*public static void onMyKeyDown(int key, KeyEvent event) {
        if ((key == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
        } else {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Title")
                    .setMessage("Do you really want to Exit?")
                    //.setIcon(R.drawable.logo)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> getActivity().finish())
                    .setNegativeButton(android.R.string.no, null).show();
        }
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        url = arguments.getString(WEBVIEW_EXTRA)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle): View? {
        val view = inflater.inflate(R.layout.activity_webview, container, false)
        ButterKnife.bind(this, view)

        toolBar!!.setBackgroundColor(activity.resources.getColor(R.color.colorPrimary))
        setSupportActionBar(toolBar)

        mWebView!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                mWebView!!.loadUrl(url)
                return true
            }
        }
        val websetting = mWebView!!.settings
        websetting.javaScriptEnabled = true
        websetting.pluginState = WebSettings.PluginState.ON
        mWebView!!.loadUrl(url)
        webView = mWebView
        return view
    }

    override fun onBackPressed(): Boolean {
        activity.finish()
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        setTitle(arguments.getString(TITLE_EXTRA))
        setDisplayHomeAsUpEnable(true)
    }

    companion object {

        var WEBVIEW_EXTRA = "WebviewActivity_EXTRA"
        var TITLE_EXTRA = "WebviewActivity_TITLE"
        internal var webView: WebView
    }
}