package dev.grcq.nitrolib.core.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@UtilityClass
public class HttpUtil {

    public static JsonObject getJson(String urlString, Map<String, String> headers) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            headers.forEach(connection::setRequestProperty);

            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                LogUtil.error("Failed to get JSON from URL '%s' with code %d", responseCode, urlString);
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

    public static JsonObject postJson(String url, Map<String, String> headers, String body) {
        return null;
    }

}
