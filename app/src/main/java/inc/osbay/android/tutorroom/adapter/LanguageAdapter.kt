package inc.osbay.android.tutorroom.adapter

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import org.json.JSONObject
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.sdk.model.Language

class LanguageAdapter(private val context: Context, private val languageList: List<Language>) : BaseAdapter() {

    override fun getCount(): Int {
        return languageList.size
    }

    override fun getItem(position: Int): String? {
        return languageList[position].languageName
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getView(position: Int, view: View, viewGroup: ViewGroup): View {
        val language = languageList[position]

        val tv = TextView(context)
        tv.text = language.languageName
        tv.gravity = Gravity.CENTER
        tv.setPadding(0, 0, 10, 0)
        tv.setTextColor(Color.BLACK)

        return tv
    }

    override fun getDropDownView(position: Int, view: View?, viewGroup: ViewGroup): View {
        var view = view
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_spinner_country_code,
                    viewGroup, false)
        }

        val language = languageList[position]

        val tvCountry = view!!.findViewById<TextView>(R.id.tv_country)
        tvCountry.text = language.languageName

        val tvCode = view.findViewById<TextView>(R.id.tv_code)
        tvCode.visibility = View.GONE
        //tvCode.setText(String.format(Locale.getDefault(), "+%d", language.getCode()));

        return view
    }
}