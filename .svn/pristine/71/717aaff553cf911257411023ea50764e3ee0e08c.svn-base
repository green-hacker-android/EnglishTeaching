package inc.osbay.android.tutorroom.sdk.model;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;

public class CreditPackage implements Serializable {

    private String creditID;
    private int companyID;
    private String packageName;
    private double packageCredit;
    private double packageAmount;
    private String packageDesc;
    private int status;
    private String comment;
    private String updateDate;

    public CreditPackage(JSONObject json) throws JSONException {
        creditID = json.getString("id");
        companyID = Integer.parseInt(json.getString("company_id"));
        packageName = json.getString("name");
        packageCredit = Double.parseDouble(json.getString("credit"));
        packageAmount = Double.parseDouble(json.getString("amount"));
        packageDesc = json.getString("description");
        status = Integer.parseInt(json.getString("status"));
        comment = json.getString("comments");
        updateDate = json.getString("updatedate");
    }

    public String getCreditID() {
        return creditID;
    }

    public void setCreditID(String creditID) {
        this.creditID = creditID;
    }

    public int getCompanyID() {
        return companyID;
    }

    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public double getPackageCredit() {
        return packageCredit;
    }

    public void setPackageCredit(double packageCredit) {
        this.packageCredit = packageCredit;
    }

    public double getPackageAmount() {
        return packageAmount;
    }

    public void setPackageAmount(double packageAmount) {
        this.packageAmount = packageAmount;
    }

    public String getPackageDesc() {
        return packageDesc;
    }

    public void setPackageDesc(String packageDesc) {
        this.packageDesc = packageDesc;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}
