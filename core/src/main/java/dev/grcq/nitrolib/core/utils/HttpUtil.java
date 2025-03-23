package dev.grcq.nitrolib.core.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@UtilityClass
public class HttpUtil {

    public static JsonObject getJson(String urlString) {
        return getJson(urlString, null);
    }

    public static JsonObject getJson(String urlString, @Nullable Map<String, String> headers) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if (headers != null) headers.forEach(connection::setRequestProperty);

            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                LogUtil.verbose("Failed to get JSON from URL '%s' with code %d", -1, urlString, responseCode);
                return null;
            }

            InputStream inputStream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            LogUtil.handleException("Failed to get JSON from URL '%s'", e, 5, urlString);
            return null;
        }
    }

    public static JsonObject postJson(String url, String body) {
        return postJson(url, null, body);
    }

    public static JsonObject postJson(String url, Map<String, String> headers, String body) {
        try {
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            if (headers != null) headers.forEach(connection::setRequestProperty);

            connection.setDoOutput(true);
            connection.getOutputStream().write(body.getBytes());

            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                LogUtil.verbose("Failed to post JSON to URL '%s' with code %d", -1, url, responseCode);
                return null;
            }

            InputStream inputStream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            LogUtil.handleException("Failed to post JSON to URL '%s'", e, 5, url);
            return null;
        }
    }

}
