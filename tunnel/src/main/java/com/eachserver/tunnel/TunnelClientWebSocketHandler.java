package com.eachserver.tunnel;

import com.eachserver.api.TunnelHttpRequest;
import com.eachserver.api.TunnelHttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class TunnelClientWebSocketHandler extends TextWebSocketHandler {

    private final CloseableHttpClient build =
            HttpClientBuilder.create().disableRedirectHandling().build();

    private final ObjectMapper objectMapper;
    private final RestClient restClient =
            RestClient.builder()
                    .requestFactory(new HttpComponentsClientHttpRequestFactory(build))
                    .build();

    @Override
    protected void handleTextMessage(final WebSocketSession session, final TextMessage message) {

        try {

            final TunnelHttpRequest httpRequest =
                    objectMapper.readValue(message.getPayload(), TunnelHttpRequest.class);

            System.out.println(httpRequest);

            ResponseEntity<String> response;
            try {

                response =
                        restClient
                                .method(HttpMethod.valueOf(httpRequest.getMethod()))
                                .uri(
                                        UriComponentsBuilder.fromHttpUrl("http://127.0.0.1:8080")
                                                .path(httpRequest.getUri().getPath())
                                                .query(httpRequest.getUri().getQuery())
                                                .build()
                                                .toUriString())
                                .headers(
                                        httpHeaders -> {
                                            for (final String headerName :
                                                    httpRequest.getHeaders().keySet()) {

                                                httpHeaders.put(
                                                        headerName,
                                                        httpRequest.getHeaders().get(headerName));
                                            }
                                        })
                                .body(httpRequest.getBody())
                                .retrieve()
                                .toEntity(String.class);
            } catch (RestClientResponseException restClientResponseException) {

                response =
                        new ResponseEntity<>(
                                restClientResponseException.getResponseBodyAsString(),
                                restClientResponseException.getResponseHeaders(),
                                restClientResponseException.getStatusCode());
            }

            final TunnelHttpResponse httpResponse = new TunnelHttpResponse();
            httpResponse.setId(httpRequest.getId());
            httpResponse.setHeaders(response.getHeaders());
            httpResponse.setStatusCode(response.getStatusCode().value());
            httpResponse.setBody(response.getBody());

            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(httpResponse)));

        } catch (final IOException e) {

            throw new RuntimeException(e);
        }
    }
}
