package inc.osbay.android.tutorroom.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import inc.osbay.android.tutorroom.R
import inc.osbay.android.tutorroom.adapter.MainCreditPackageAdapter.ViewHolder
import inc.osbay.android.tutorroom.sdk.model.CreditPackage

class MainCreditPackageAdapter(private val creditPackageList: List<CreditPackage>, private val onClick: OnItemClicked) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.credit_package_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val creditPackage = creditPackageList[position]
        holder.creditTitle.text = creditPackage.packageName
        holder.creditTitle.text = creditPackage.packageCredit.toString()
        holder.creditContent.text = creditPackage.packageAmount.toString()
        holder.buy.setOnClickListener { view -> onClick.onItemClick(creditPackage) }
    }

    override fun getItemCount(): Int {
        return creditPackageList.size
    }

    interface OnItemClicked {
        fun onItemClick(creditPackage: CreditPackage)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var creditTitle: TextView
        var creditContent: TextView
        var buy: TextView

        init {
            creditTitle = itemView.findViewById(R.id.credit_title)
            creditContent = itemView.findViewById(R.id.credit_content)
            buy = itemView.findViewById(R.id.buy)
        }
    }
}
