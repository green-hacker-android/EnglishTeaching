package inc.osbay.android.tutorroom.adapter

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.sdk.model.Packagee
import inc.osbay.android.tutorroom.sdk.model.Tag

class TagAdapter(private val context: Context, private val tagList: List<Tag>) : BaseAdapter() {

    override fun getCount(): Int {
        return tagList.size
    }

    override fun getItem(position: Int): Any {
        return tagList[position]
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getView(position: Int, view: View, viewGroup: ViewGroup): View {
        val aPackage = tagList[position]

        val tv = TextView(context)
        tv.text = aPackage.tagName
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

        val aPackage = tagList[position]

        val tvCountry = view!!.findViewById<TextView>(R.id.tv_country)
        tvCountry.text = aPackage.tagName

        val tvCode = view.findViewById<TextView>(R.id.tv_code)
        tvCode.visibility = View.GONE

        return view
    }
}
