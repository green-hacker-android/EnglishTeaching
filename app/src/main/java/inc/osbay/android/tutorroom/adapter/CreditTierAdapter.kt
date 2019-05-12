package inc.osbay.android.tutorroom.adapter

import android.support.percent.PercentRelativeLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.adapter.CreditTierAdapter.ViewHolder
import inc.osbay.android.tutorroom.sdk.model.CreditPackage

class CreditTierAdapter(private val creditPackageList: List<CreditPackage>, private val onClick: OnItemClicked) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.credit_tier_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val creditPackage = creditPackageList[position]
        holder.creditTitle.text = creditPackage.packageName
        holder.credit.text = creditPackage.packageCredit.toString()
        holder.creditAmount.text = creditPackage.packageAmount.toString()
        holder.percentRL.setOnClickListener { view -> onClick.onItemClick(creditPackage) }
    }

    override fun getItemCount(): Int {
        return creditPackageList.size
    }

    interface OnItemClicked {
        fun onItemClick(creditPackage: CreditPackage)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var percentRL: PercentRelativeLayout
        var creditTitle: TextView
        var credit: TextView
        var creditAmount: TextView

        init {
            percentRL = itemView.findViewById(R.id.percent_rl)
            creditTitle = itemView.findViewById(R.id.packagee)
            credit = itemView.findViewById(R.id.credit)
            creditAmount = itemView.findViewById(R.id.credit_ammount)
        }
    }
}
