package inc.osbay.android.tutorroom.sdk.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Lesson implements Serializable {
    private String lessonId;
    private double lessonPrice;
    private String lessonName;
    private String lessonDescription;
    private String lessonCover;
    private int classMin;
    private String lessonPath;

    public Lesson() {
    }

    public Lesson(JSONObject json) throws JSONException {
        lessonId = json.getString("lesson_id");
        lessonPrice = json.getDouble("lesson_price");
        lessonName = json.getString("lesson_name");
        lessonDescription = json.getString("lesson_description");
        lessonCover = json.getString("lesson_cover");
        classMin = json.getInt("class_min");
        lessonPath = json.getString("lesson_path");
    }

    public String getLessonPath() {
        return lessonPath;
    }

    public void setLessonPath(String lessonPath) {
        this.lessonPath = lessonPath;
    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public double getLessonPrice() {
        return lessonPrice;
    }

    public void setLessonPrice(double lessonPrice) {
        this.lessonPrice = lessonPrice;
    }

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public String getLessonDescription() {
        return lessonDescription;
    }

    public void setLessonDescription(String lessonDescription) {
        this.lessonDescription = lessonDescription;
    }

    public String getLessonCover() {
        return lessonCover;
    }

    public void setLessonCover(String lessonCover) {
        this.lessonCover = lessonCover;
    }

    public int getClassMin() {
        return classMin;
    }

    public void setClassMin(int classMin) {
        this.classMin = classMin;
    }
}
