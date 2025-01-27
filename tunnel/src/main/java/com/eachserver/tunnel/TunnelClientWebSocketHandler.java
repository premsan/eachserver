package com.eachserver.tunnel;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Service
@RequiredArgsConstructor
public class TunnelClientWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final RestClient restClient = RestClient.create();

    @Override
    protected void handleTextMessage(final WebSocketSession session, final TextMessage message) {

        try {

            final TunnelHttpRequest httpRequest =
                    objectMapper.readValue(message.asBytes(), TunnelHttpRequest.class);

            final ResponseEntity<String> response =
                    restClient
                            .method(httpRequest.getMethod())
                            .uri(httpRequest.getUri())
                            .headers(httpHeaders -> httpHeaders.addAll(httpRequest.getHeaders()))
                            .body(httpRequest.getBody())
                            .retrieve()
                            .toEntity(String.class);

            final TunnelHttpResponse httpResponse = new TunnelHttpResponse();
            httpResponse.setHeaders(response.getHeaders());
            httpResponse.setStatusCode(response.getStatusCode());
            httpResponse.setBody(httpResponse.getBody());

            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(httpResponse)));

        } catch (final IOException e) {

            throw new RuntimeException(e);
        }
    }
}
