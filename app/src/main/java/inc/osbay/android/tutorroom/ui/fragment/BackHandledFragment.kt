package inc.osbay.android.tutorroom.ui.fragment

import android.app.Fragment
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView

import inc.osbay.android.tutorroom.R

abstract class BackHandledFragment : Fragment() {
    protected var backHandlerInterface: BackHandlerInterface

    abstract fun onBackPressed(): Boolean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (activity !is BackHandlerInterface) {
            throw ClassCastException(
                    "Hosting activity must implement BackHandlerInterface")
        } else {
            backHandlerInterface = activity as BackHandlerInterface
        }
    }

    override fun onStart() {
        super.onStart()

        // Mark this fragment as the selected Fragment.
        backHandlerInterface.setmSelectedFragment(this)
    }

    fun setSupportActionBar(toolbar: Toolbar) {
        val compatActivity = activity as AppCompatActivity
        compatActivity.setSupportActionBar(toolbar)

        val actionBar = compatActivity.supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)

    }

    fun setStatusBarColor(color: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            // finally change the color

            window.statusBarColor = Color.parseColor(color)
        }
    }

    open fun setTitle(title: String) {
        val toolbar = activity.findViewById<Toolbar>(R.id.tool_bar)
        if (toolbar != null) {
            val tvTitle = toolbar.findViewById<TextView>(R.id.tv_toolbar_title)
            tvTitle.typeface = Typeface.createFromAsset(activity.assets, "fonts/microsoft_jhenghei.ttf")
            tvTitle.text = title
            tvTitle.setCompoundDrawables(null, null, null, null)
            tvTitle.setOnClickListener(null)
        }
    }

    fun setTitle(title: String, listener: OnTitleTextClickListener) {
        val toolbar = activity.findViewById<Toolbar>(R.id.tool_bar)
        if (toolbar != null) {
            val tvTitle = toolbar.findViewById<TextView>(R.id.tv_toolbar_title)
            tvTitle.text = title

            tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    ContextCompat.getDrawable(activity, R.mipmap.ic_expand_arrow), null)

            tvTitle.setOnClickListener { view -> listener.onTitleTextClicked() }
        }
    }

    fun showActionBar() {
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.show()
    }

    fun hideActionBar() {
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.hide()
    }

    fun setDisplayHomeAsUpEnable(status: Boolean) {
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(status)
            actionBar.setHomeButtonEnabled(status)
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_cross_white)
        }
    }

    interface BackHandlerInterface {
        fun setmSelectedFragment(backHandledFragment: BackHandledFragment)
    }

    interface OnTitleTextClickListener {
        fun onTitleTextClicked()
    }
}
