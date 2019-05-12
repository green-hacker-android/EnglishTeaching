package inc.osbay.android.tutorroom.sdk.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Tag implements Serializable {
    private String tagID;
    private String tagType;
    private String tagName;
    private String tagDescription;
    private JSONArray packageArray;
    private JSONArray lessonArray;

    public Tag(JSONObject json) throws JSONException {
        tagID = json.getString("tag_id");
        tagType = json.getString("tag_type");
        tagName = json.getString("tag_name");
        tagDescription = json.getString("tag_description");
        if (json.has("package_list"))
            packageArray = new JSONArray(json.getString("package_list"));
        if (json.has("lesson_list"))
            lessonArray = new JSONArray(json.getString("lesson_list"));
    }

    public JSONArray getLessonArray() {
        return lessonArray;
    }

    public void setLessonArray(JSONArray lessonArray) {
        this.lessonArray = lessonArray;
    }

    public JSONArray getPackageArray() {
        return packageArray;
    }

    public void setPackageArray(JSONArray packageArray) {
        this.packageArray = packageArray;
    }

    public String getTagID() {
        return tagID;
    }

    public void setTagID(String tagID) {
        this.tagID = tagID;
    }

    public String getTagType() {
        return tagType;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagDescription() {
        return tagDescription;
    }

    public void setTagDescription(String tagDescription) {
        this.tagDescription = tagDescription;
    }
}