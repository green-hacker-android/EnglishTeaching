package inc.osbay.android.tutorroom.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.support.v7.widget.Toolbar

import butterknife.BindView
import butterknife.ButterKnife
import inc.osbay.android.tutorroom.R

class ForgetPasswordFragment : BackHandledFragment() {

    @BindView(R.id.tool_bar)
    internal var toolBar: Toolbar? = null
    @BindView(R.id.forget_password_email)
    internal var forgetPassET: EditText? = null
    @BindView(R.id.forget_password_submit)
    internal var forgetPassTV: TextView? = null

    override fun onBackPressed(): Boolean {
        activity.finish()
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle): View? {
        val view = inflater.inflate(R.layout.fragment_forget_password, container, false)
        ButterKnife.bind(this, view)
        return view
    }
}
