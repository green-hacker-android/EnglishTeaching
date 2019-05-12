package inc.osbay.android.tutorroom.adapter

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.util.Locale

import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.sdk.model.CountryCode

class CountryCodeAdapter(private val context: Context, private val mCountryCodes: List<CountryCode>) : BaseAdapter() {

    override fun getCount(): Int {
        return mCountryCodes.size
    }

    override fun getItem(position: Int): Any {
        return mCountryCodes[position]
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getView(position: Int, view: View, viewGroup: ViewGroup): View {
        val code = mCountryCodes[position]

        val tv = TextView(context)
        tv.setText(String.format(Locale.getDefault(), "+%d", code.code))
        tv.gravity = Gravity.END
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

        val code = mCountryCodes[position]

        val tvCountry = view!!.findViewById<TextView>(R.id.tv_country)
        tvCountry.text = code.country

        val tvCode = view.findViewById<TextView>(R.id.tv_code)
        tvCode.setText(String.format(Locale.getDefault(), "+%d", code.code))

        return view
    }
}
