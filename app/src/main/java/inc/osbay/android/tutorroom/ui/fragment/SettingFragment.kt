package inc.osbay.android.tutorroom.ui.fragment

import android.app.Fragment
import android.app.FragmentManager
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import inc.osbay.android.tutorroom.R

class SettingFragment : BackHandledFragment() {
    @BindView(R.id.tool_bar)
    internal var toolBar: Toolbar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle): View? {
        val rootView = inflater.inflate(R.layout.fragment_setting, container, false)
        ButterKnife.bind(this, rootView)

        toolBar!!.setBackgroundColor(activity.resources.getColor(R.color.colorPrimary))
        setSupportActionBar(toolBar)
        return rootView
    }

    override fun onStart() {
        super.onStart()
        setTitle(getString(R.string.setting))
        setDisplayHomeAsUpEnable(true)
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return true
    }

    override fun onBackPressed(): Boolean {
        fragmentManager.popBackStack()
        return false
    }

    @OnClick(R.id.language_rl)
    internal fun language() {
        val fm = fragmentManager
        val frg = fm.findFragmentById(R.id.framelayout)
        val fragment = LanguageSettingFragment()
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
