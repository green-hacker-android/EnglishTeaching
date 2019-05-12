package inc.osbay.android.tutorroom.sdk.model

import java.util.Locale

/**
 * Created by osbay on 10/13/16.
 */
class CountryCode {
    var codeId: Int = 0
    var country: String? = null
    var code: Int = 0

    override fun toString(): String {
        return String.format(Locale.getDefault(), "+%d", code)
    }
}
