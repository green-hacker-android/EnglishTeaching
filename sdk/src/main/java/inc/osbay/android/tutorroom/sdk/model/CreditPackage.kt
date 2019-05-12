package inc.osbay.android.tutorroom.sdk.model

import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable

class CreditPackage @Throws(JSONException::class)
constructor(json: JSONObject) : Serializable {

    var creditID: String? = null
    var companyID: Int = 0
    var packageName: String? = null
    var packageCredit: Double = 0.toDouble()
    var packageAmount: Double = 0.toDouble()
    var packageDesc: String? = null
    var status: Int = 0
    var comment: String? = null
    var updateDate: String? = null

    init {
        creditID = json.getString("id")
        companyID = Integer.parseInt(json.getString("company_id"))
        packageName = json.getString("name")
        packageCredit = java.lang.Double.parseDouble(json.getString("credit"))
        packageAmount = java.lang.Double.parseDouble(json.getString("amount"))
        packageDesc = json.getString("description")
        status = Integer.parseInt(json.getString("status"))
        comment = json.getString("comments")
        updateDate = json.getString("updatedate")
    }
}
