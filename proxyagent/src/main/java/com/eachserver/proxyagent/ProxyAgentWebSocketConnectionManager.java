package com.eachserver.proxyagent;

import com.eachserver.api.ProxyServerConnect;
import com.eachserver.api.ProxyServerHost;
import java.net.URI;
import java.util.Objects;
import org.springframework.web.client.RestClient;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.util.UriComponentsBuilder;

public class ProxyAgentWebSocketConnectionManager extends WebSocketConnectionManager {

    private static final RestClient restClient = RestClient.builder().build();

    private final ProxyAgentProperties proxyAgentProperties;

    private URI websocketUri;

    public ProxyAgentWebSocketConnectionManager(
            final ProxyAgentProperties proxyAgentProperties,
            final WebSocketClient client,
            final WebSocketHandler webSocketHandler) {

        super(client, webSocketHandler, null);

        this.proxyAgentProperties = proxyAgentProperties;
    }

    @Override
    public URI getUri() {

        if (Objects.nonNull(proxyAgentProperties.getWebsocketUriOverride())) {
            return UriComponentsBuilder.fromUri(proxyAgentProperties.getWebsocketUriOverride())
                    .path(ProxyServerConnect.PATH)
                    .build()
                    .toUri();
        }

        if (Objects.nonNull(websocketUri)) {

            return websocketUri;
        }

        final ProxyServerHost.ResponseBody responseBody =
                restClient
                        .get()
                        .uri(
                                UriComponentsBuilder.fromUri(proxyAgentProperties.getServer())
                                        .path(ProxyServerHost.PATH)
                                        .build()
                                        .toUri())
                        .headers(
                                httpHeaders ->
                                        httpHeaders.setBasicAuth(
                                                proxyAgentProperties.getUsername(),
                                                proxyAgentProperties.getPassword()))
                        .retrieve()
                        .body(ProxyServerHost.ResponseBody.class);

        websocketUri = responseBody.getHost().getUri();
        return websocketUri;
    }
}
