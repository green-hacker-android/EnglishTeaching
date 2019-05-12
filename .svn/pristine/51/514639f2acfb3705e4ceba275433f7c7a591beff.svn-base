package inc.osbay.android.tutorroom.sdk.model;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;

public class Language implements Serializable {

    private String languageID;
    private String languageName;
    private String shortName;

    public Language(JSONObject json) throws JSONException {
        languageID = json.getString("Id");
        languageName = json.getString("Name");
        shortName = json.getString("ShortName");
    }

    public String getLanguageID() {
        return languageID;
    }

    public void setLanguageID(String languageID) {
        this.languageID = languageID;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
