package inc.osbay.android.tutorroom.sdk.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Banner {
    private String bannerId;
    private String bannerType;  // 1. Lesson Booking, 2.Package Booking
    private String companyId;
    private String position;
    private String name;
    private String image;
    private String comments;
    private String updatedate;

    public Banner(JSONObject json) throws JSONException {
        bannerId = json.getString("id");
        companyId = json.getString("company_id");
        bannerType = json.getString("banner_type");
        position = json.getString("position");
        name = json.getString("name");
        image = json.getString("image");
        comments = json.getString("comments");
        updatedate = json.getString("updatedate");
    }

    public String getBannerId() {
        return bannerId;
    }

    public void setBannerId(String bannerId) {
        this.bannerId = bannerId;
    }

    public String getBannerType() {
        return bannerType;
    }

    public void setBannerType(String bannerType) {
        this.bannerType = bannerType;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getUpdatedate() {
        return updatedate;
    }

    public void setUpdatedate(String updatedate) {
        this.updatedate = updatedate;
    }
}
