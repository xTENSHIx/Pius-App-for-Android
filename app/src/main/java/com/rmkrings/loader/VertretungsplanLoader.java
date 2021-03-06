package com.rmkrings.loader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import com.rmkrings.helper.AppDefaults;
import com.rmkrings.http.HttpRequest;
import com.rmkrings.http.HttpRequestData;
import com.rmkrings.interfaces.HttpResponseCallback;
import com.rmkrings.http.HttpResponseData;

public class VertretungsplanLoader extends HttpAuthenticatedGetLoader {
    private final String forGrade;

    public VertretungsplanLoader(String forGrade) {
        super();
        this.forGrade = forGrade;
    }

    @Override
    protected URL getURL(String digest) throws java.net.MalformedURLException {
        String urlString = String.format("%s/v2/vertretungsplan", AppDefaults.getBaseUrl());

        if (forGrade != null || digest != null) {
            String separator = "/?";
            if (forGrade != null) {
                urlString += String.format("%sforGrade=%s", separator, forGrade);
                separator = "&";
            }

            if (digest != null) {
                urlString += String.format("%sdigest=%s", separator, digest);
            }
        }

        return new URL(urlString);
    }

    public static void validateLogin(String forUser, String withPassword, HttpResponseCallback callback) {
        try {
            URL url = new URL(String.format("%s/validateLogin", AppDefaults.getBaseUrl()));
            URLConnection connection = url.openConnection();
            ((HttpURLConnection)connection).setRequestMethod("HEAD");
            connection.addRequestProperty("Authorization", "Basic " + getAndEncodeCredentials(forUser, withPassword));

            HttpRequest request = new HttpRequest();
            HttpRequestData data = new HttpRequestData(connection, callback);
            request.execute(data);
        } catch (IOException e) {
            callback.execute(new HttpResponseData(500, true));
            e.printStackTrace();
        }
    }
}
