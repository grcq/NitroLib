package dev.grcq.nitrolib.core.http;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.grcq.nitrolib.core.serialization.elements.FileElement;
import dev.grcq.nitrolib.core.utils.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpClient {

    private String baseUrl;
    private Method method = Method.GET;
    private Map<String, String> headers;
    private int timeout = 5000;
    private String body;

    /**
     * Creates a new HttpClient instance.
     * @return The HttpClient instance.
     */
    public static HttpClient create() {
        return new HttpClient();
    }

    /**
     * Creates a new HttpClient instance with the specified URL.
     * @param url The URL of the request.
     * @return The HttpClient instance.
     */
    public static HttpClient create(String url) {
        return new HttpClient().get(url);
    }

    /**
     * Creates a new HttpClient instance with the specified URL and method.
     * @param url The URL of the request.
     * @param method The method of the request.
     * @return The HttpClient instance.
     */
    public static HttpClient create(String url, Method method) {
        return new HttpClient().request(url, method);
    }

    /**
     * Sets the URL and method of the request.
     * @param url The URL of the request.
     * @param method The method of the request.
     * @return The HttpClient instance.
     */
    public HttpClient request(String url, Method method) {
        this.method = method;
        this.baseUrl = url;
        return this;
    }

    /**
     * Sets the request method to GET.
     * @param url The URL of the request.
     * @return The HttpClient instance.
     */
    public HttpClient get(String url) {
        this.method = Method.GET;
        this.baseUrl = url;
        return this;
    }

    /**
     * Sets the request method to POST.
     * @param url The URL of the request.
     * @return The HttpClient instance.
     */
    public HttpClient post(String url) {
        this.method = Method.POST;
        this.baseUrl = url;
        return this;
    }

    /**
     * Sets the request method to OPTIONS.
     * @param url The URL of the request.
     * @return The HttpClient instance.
     */
    public HttpClient put(String url) {
        this.method = Method.PUT;
        this.baseUrl = url;
        return this;
    }

    /**
     * Sets the request method to DELETE.
     * @param url The URL of the request.
     * @return The HttpClient instance.
     */
    public HttpClient delete(String url) {
        this.method = Method.DELETE;
        this.baseUrl = url;
        return this;
    }

    /**
     * Adds a header to the request.
     * @param key The key of the header.
     * @param value The value of the header.
     * @return The HttpClient instance.
     */
    public HttpClient header(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    /**
     * Sets the headers of the request.
     * @param headers The headers of the request.
     * @return The HttpClient instance.
     */
    public HttpClient headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    /**
     * Sets the timeout of the request.
     * @param timeout The timeout of the request.
     * @return The HttpClient instance.
     */
    public HttpClient timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * Sets the body of the request.
     * @param body The body of the request.
     * @return The HttpClient instance.
     */
    public HttpClient body(String body) {
        this.body = body;
        return this;
    }

    /**
     * Sets the body of the request.
     * @param body The body of the request.
     * @return The HttpClient instance.
     */
    public HttpClient body(JsonElement body) {
        this.body = body.toString();
        return this;
    }

    /**
     * Sets the body of the request.
     * @param body The body of the request.
     * @return The HttpClient instance.
     */
    public HttpClient body(FileElement body) {
        this.body = body.toJson();
        return this;
    }

    /**
     * Executes the request synchronously and returns the response.
     * @return The response.
     */
    public JsonObject execute() {
        try {
            URL url = new URL(baseUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method.name());
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestProperty(Headers.CONTENT_TYPE, "application/json");
            if (headers != null) {
                headers.forEach(connection::setRequestProperty);
            }
            if (body != null) {
                connection.setDoOutput(true);
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = body.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    return JsonParser.parseString(response.toString()).getAsJsonObject();
                }
            }

            LogUtil.error("Failed to execute HTTP request. Response code: " + responseCode);
            return null;
        } catch (IOException e) {
            LogUtil.handleException("Failed to execute HTTP request.", e);
            return null;
        }
    }

    /**
     * Executes the request asynchronously and calls the callback when the response is received.
     * @param callback The callback to call when the response is received.
     */
    public void execute(Callback callback) {
        new Thread(() -> {
            JsonObject response = execute();
            callback.onResponse(response);
        }).start();
    }

    @FunctionalInterface
    public interface Callback {
        void onResponse(JsonObject response);
    }

    public interface Headers {
        String AUTHORIZATION = "Authorization";
        String CONTENT_TYPE = "Content-Type";
        String USER_AGENT = "User-Agent";
        String ACCEPT = "Accept";
        String ACCEPT_LANGUAGE = "Accept-Language";
        String ACCEPT_ENCODING = "Accept-Encoding";
        String CONNECTION = "Connection";
        String HOST = "Host";
        String ORIGIN = "Origin";
        String REFERER = "Referer";
        String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
        String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
        String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
        String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
        String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
        String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
        String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
        String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
        String X_REQUESTED_WITH = "X-Requested-With";
        String X_FORWARDED_FOR = "X-Forwarded-For";
        String X_FORWARDED_PROTO = "X-Forwarded-Proto";
        String X_FORWARDED_HOST = "X-Forwarded-Host";
        String X_FORWARDED_PORT = "X-Forwarded-Port";
        String X_FORWARDED_SERVER = "X-Forwarded-Server";
        String X_REAL_IP = "X-Real-IP";
        String X_API_KEY = "X-Api-Key";
        String X_API_SECRET = "X-Api-Secret";
        String X_API_TOKEN = "X-Api-Token";
        String X_API_SIGNATURE = "X-Api-Signature";
        String X_API_TIMESTAMP = "X-Api-Timestamp";
        String X_API_NONCE = "X-Api-Nonce";
        String X_API_USER = "X-Api-User";
    }

    public enum Method {
        GET, POST, OPTIONS, PUT, DELETE
    }

}
