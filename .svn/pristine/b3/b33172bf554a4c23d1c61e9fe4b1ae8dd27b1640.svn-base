package inc.osbay.android.tutorroom.sdk.client;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Model class of Server Response.
 *
 * @author Ambrose
 */
public class ServerResponse {

    private String cmdSt;

    private String dataSt;

    private int code;

    private String message;

    private int mStatus;

    public JSONObject getJSONObjResult() {
        try {
            JSONObject obj = new JSONObject(dataSt);
            return obj;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getDataSt() {
        return dataSt;
    }

    public void setDataSt(String dataSt) {
        this.dataSt = dataSt;
    }

    public String getCmdSt() {
        return cmdSt;
    }

    public void setCmdSt(String cmdSt) {
        this.cmdSt = cmdSt;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    /**
     * Status Code of Server Response.
     *
     * @author Ambrose
     */
    public final class Status {

        /**
         * Status code for SUCCESS case.
         */
        public static final int SUCCESS = 1;
        public static final int NO_DATA = 0;
        public static final int Account_NotExist = 1101;
        public static final int Account_PasswordError = 1102;
        public static final int Account_EmailAlreadyExit = 1103;
        public static final int Account_UserNameAlreadyExist = 1104;
        public static final int SendMail_Fail = 1105;
        public static final int Password_Set_Fail = 1106;
        public static final int Password_Set_Success = 1107;
        public static final int INSUFFICIENT_CREDIT = 1301;


        /**
         * Status code for NEED REFRESH case to refresh token.
         */
        public static final int NEED_REFRESH = 202;

        private Status() {
        }

    }
}
