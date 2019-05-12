package inc.osbay.android.tutorroom.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient

import inc.osbay.android.tutorroom.sdk.constant.CommonConstant

class WebViewClientImpl(private val context: Context) : WebViewClient() {

    override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
        if (url.contains(CommonConstant.PAYPAL_URL))
            return false

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
        return true
    }

}