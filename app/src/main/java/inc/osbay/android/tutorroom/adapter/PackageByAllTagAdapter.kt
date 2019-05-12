package inc.osbay.android.tutorroom.adapter

import android.content.Context
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import org.json.JSONArray
import org.json.JSONException

import java.util.ArrayList

import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.sdk.model.Packagee
import inc.osbay.android.tutorroom.sdk.model.Tag

class PackageByAllTagAdapter(private val tagList: List<Tag>, private val context: Context) : RecyclerView.Adapter<PackageByAllTagAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.package_by_tag_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tag = tagList[position]
        holder.tagName.text = tag.tagName
        holder.tagDesc.text = tag.tagDescription

        val packageList = ArrayList<Packagee>()
        val packageJsonArray = tag.packageArray
        try {
            for (j in 0 until packageJsonArray!!.length()) {
                val packageObj = Packagee(packageJsonArray!!.getJSONObject(j))
                packageList.add(packageObj)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val packageAdapter = PackageAdapter(packageList, context)
        val mLayoutManager = LinearLayoutManager(context)
        holder.packageRV.layoutManager = mLayoutManager
        holder.packageRV.itemAnimator = DefaultItemAnimator()
        holder.packageRV.adapter = packageAdapter
        packageAdapter.notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return tagList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var packageRV: RecyclerView
        var tagDesc: TextView
        var tagName: TextView

        init {
            packageRV = itemView.findViewById(R.id.package_rv)
            tagDesc = itemView.findViewById(R.id.tag_desc)
            tagName = itemView.findViewById(R.id.tag_name)
        }
    }
}