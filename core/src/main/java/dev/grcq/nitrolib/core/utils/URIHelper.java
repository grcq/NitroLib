package dev.grcq.nitrolib.core.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class URIHelper {

    @NotNull
    private String protocol;
    @NotNull
    private String host;
    @Nullable
    private Integer port;
    @Nullable
    private String username;
    @Nullable
    private String password;
    @Nullable
    private String pathname;
    @Nullable
    private List<KeyValue<String, Object>> query;
    @Nullable
    private String fragment;

    public URIHelper(@NotNull String uri) {
        String[] parts = uri.split("://");
        this.protocol = parts[0];

        int hostIndex = 1;
        String[] credentials = parts[1].split("@");
        if (credentials.length > 1) {
            String[] userPass = credentials[0].split(":");
            this.username = userPass[0];
            this.password = userPass[1];
        } else {
            hostIndex = 0;
        }

        String[] hostPort = credentials[hostIndex].split(":");
        this.host = hostPort[0];
        if (hostPort.length > 1) {
            this.port = Integer.parseInt(hostPort[1]);
        }

        String[] pathQuery = credentials[hostIndex + 1].split("\\?");
        this.pathname = pathQuery[0];
        if (pathQuery.length > 1) {
            String[] queryParts = pathQuery[1].split("#");
            String[] query = queryParts[0].split("&");
            for (String keyValue : query) {
                String[] kv = keyValue.split("=");
                this.query.add(new KeyValue<>(kv[0], kv.length > 1 ? kv[1] : null));
            }
            if (queryParts.length > 1) {
                this.fragment = queryParts[1];
            }
        }
    }

    public URIHelper(@NotNull String protocol, @NotNull String host, @Nullable Integer port, @Nullable String username, @Nullable String password, @Nullable String pathname, @Nullable List<KeyValue<String, Object>> query) {
        this(protocol, host, port, username, password, pathname, query, null);
    }

    public URIHelper(@NotNull String protocol, @NotNull String host, @Nullable Integer port, @Nullable String username, @Nullable String password, @Nullable String pathname) {
        this(protocol, host, port, username, password, pathname, null, null);
    }

    public URIHelper(@NotNull String protocol, @NotNull String host, @Nullable Integer port, @Nullable String username, @Nullable String password) {
        this(protocol, host, port, username, password, null, null, null);
    }

    public URIHelper(@NotNull String protocol, @NotNull String host, @Nullable Integer port, @Nullable String username) {
        this(protocol, host, port, username, null, null, null, null);
    }

    public URIHelper(@NotNull String protocol, @NotNull String host, @Nullable Integer port) {
        this(protocol, host, port, null, null, null, null, null);
    }

    public URIHelper(@NotNull String protocol, @NotNull String host) {
        this(protocol, host, null, null, null, null, null, null);
    }

    public String getURI() {
        StringBuilder uri = new StringBuilder();
        uri.append(protocol).append("://");
        if (username != null) {
            uri.append(username).append(":");
            if (password != null) {
                uri.append(password);
            }
            uri.append("@");
        }

        uri.append(host);
        if (port != null) {
            uri.append(":").append(port);
        }
        if (pathname != null) {
            if (!pathname.startsWith("/")) {
                uri.append("/");
            }

            uri.append(pathname);
        }
        if (query != null && !query.isEmpty()) {
            uri.append("?");
            for (int i = 0; i < query.size(); i++) {
                KeyValue<String, Object> keyValue = query.get(i);
                uri.append(keyValue.getKey()).append("=").append(keyValue.getValue()).append("&");
            }
            uri.deleteCharAt(uri.length() - 1);
        }
        if (fragment != null) {
            uri.append("#").append(fragment);
        }
        return uri.toString();
    }

    public URI toURI() {
        return URI.create(getURI());
    }
}
