package inc.osbay.android.tutorroom.sdk.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Packagee implements Serializable {
    private String packageID;
    private double packagePrice;
    private String packageName;
    private String packageDescription;
    private String coverImg;
    private double totalTime;
    private int lessonCount;
    private JSONArray lessonJsonArray;

    public Packagee(JSONObject json) throws JSONException {
        packageID = json.getString("package_id");
        packagePrice = Double.parseDouble(json.getString("package_price"));
        packageName = json.getString("package_name");
        packageDescription = json.getString("package_description");
        coverImg = json.getString("cover");
        if (json.has("total_time"))
            totalTime = json.getDouble("total_time");
        if (json.has("lesson_count"))
            lessonCount = Integer.parseInt(json.getString("lesson_count"));
        if (json.has("lesson_list"))
            lessonJsonArray = new JSONArray(json.getString("lesson_list"));
    }

    public double getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(double totalTime) {
        this.totalTime = totalTime;
    }

    public int getLessonCount() {
        return lessonCount;
    }

    public void setLessonCount(int lessonCount) {
        this.lessonCount = lessonCount;
    }

    public JSONArray getLessonJsonArray() {
        return lessonJsonArray;
    }

    public void setLessonJsonArray(JSONArray lessonJsonArray) {
        this.lessonJsonArray = lessonJsonArray;
    }

    public String getPackageID() {
        return packageID;
    }

    public void setPackageID(String packageID) {
        this.packageID = packageID;
    }

    public double getPackagePrice() {
        return packagePrice;
    }

    public void setPackagePrice(double packagePrice) {
        this.packagePrice = packagePrice;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageDescription() {
        return packageDescription;
    }

    public void setPackageDescription(String packageDescription) {
        this.packageDescription = packageDescription;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }
}
