package com.rmkrings.loader;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.rmkrings.helper.AppDefaults;
import com.rmkrings.pius_app_for_android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class HttpDeviceTokenSetter extends HttpPost {

    private final String token;
    private final String grade;
    private final ArrayList<String> courseList;
    private final String version;
    private final String credential;

    public HttpDeviceTokenSetter(String token, String grade, ArrayList<String> courseList, String version, String credential) {
        this.token = token;
        this.grade = grade;
        this.courseList = courseList;
        this.version = version;
        this.credential = credential;
    }

    @Override
    protected URL getURL() throws MalformedURLException {
        return new URL(String.format("%s/v2/deviceToken", AppDefaults.getBaseUrl()));
    }

    @Override
    protected String getBody() throws JSONException {
        try {
            ApplicationInfo ai = pius_app_for_android.getAppContext().getPackageManager().getApplicationInfo(pius_app_for_android.getAppContext().getPackageName(), PackageManager.GET_META_DATA);
            final String messagingProvider = "fcm";
            final String apiKey = (String)ai.metaData.get("apiKey");
            JSONObject jsonData = new JSONObject()
                    .put("apiKey", apiKey)
                    .put("deviceToken", token)
                    .put("grade", grade)
                    .put("courseList", new JSONArray(courseList))
                    .put("messagingProvider", messagingProvider)
                    .put("version", version)
                    .put("credential", credential);

            return jsonData.toString();
        }
        catch(PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
