package inc.osbay.android.tutorroom.adapter

import android.app.Activity
import android.app.Fragment
import android.app.FragmentManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView

import com.facebook.drawee.view.SimpleDraweeView

import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.adapter.PackageAdapter.ViewHolder
import inc.osbay.android.tutorroom.sdk.model.Packagee
import inc.osbay.android.tutorroom.ui.fragment.PackageDetailFragment

class PackageAdapter internal constructor(private val packageList: List<Packagee>, private val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.package_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val packagee = packageList[position]
        val cover = packagee.coverImg
        holder.packageCover.setImageURI(Uri.parse(cover))
        holder.packageName.text = packagee.packageName
        holder.packageDesc.text = packagee.packageDescription
        holder.packagePrice.text = context.getString(R.string._credits, packagee.packagePrice.toString())
        holder.packageRL.setOnClickListener { view ->
            val mainFragment = PackageDetailFragment()
            val bundle = Bundle()
            bundle.putString(PackageDetailFragment.PackageDetailFragment_EXTRA, packagee.packageID)
            mainFragment.arguments = bundle

            val fm = (context as Activity).fragmentManager
            val fragment = fm.findFragmentById(R.id.framelayout)
            if (fragment == null) {
                fm.beginTransaction()
                        .add(R.id.framelayout, mainFragment)
                        .commit()
            } else {
                fm.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.framelayout, mainFragment)
                        .commit()
            }
        }
    }

    override fun getItemCount(): Int {
        return packageList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var packagePrice: TextView
        var packageDesc: TextView
        var packageName: TextView
        var packageCover: SimpleDraweeView
        var packageRL: RelativeLayout

        init {
            packagePrice = itemView.findViewById(R.id.package_price)
            packageDesc = itemView.findViewById(R.id.package_desc)
            packageName = itemView.findViewById(R.id.package_name)
            packageCover = itemView.findViewById(R.id.package_cover)
            packageRL = itemView.findViewById(R.id.relative_layout)
        }
    }
}
