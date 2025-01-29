package com.eachserver.tunnelserver;

import com.eachserver.api.TunnelHttpRequest;
import com.eachserver.api.TunnelHttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class TunnelServerWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> idToActiveSession = new HashMap<>();
    private final Map<String, TunnelHttpResponse> tunnelHttpResponseMap = new HashMap<>();

    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        idToActiveSession.put(session.getId(), session);
        super.afterConnectionEstablished(session);
    }

    @Override
    protected void handleTextMessage(final WebSocketSession session, final TextMessage message) {

        try {
            final TunnelHttpResponse tunnelHttpResponse =
                    objectMapper.readValue(message.getPayload(), TunnelHttpResponse.class);

            tunnelHttpResponseMap.put(tunnelHttpResponse.getId(), tunnelHttpResponse);

        } catch (final IOException e) {

            throw new RuntimeException(e);
        }
    }

    public void sendMessage(final HttpServletRequest request, final HttpServletResponse response) {

        final TunnelHttpRequest tunnelHttpRequest = new TunnelHttpRequest();

        tunnelHttpRequest.setId(UUID.randomUUID().toString());
        tunnelHttpRequest.setUri(
                UriComponentsBuilder.fromPath(request.getRequestURI())
                        .replaceQuery(request.getQueryString())
                        .build()
                        .toUri());
        tunnelHttpRequest.setMethod(request.getMethod());
        tunnelHttpRequest.setHeaders(new HashMap<>());

        for (final Enumeration<String> e = request.getHeaderNames(); e.hasMoreElements(); ) {

            final String headerName = e.nextElement();
            final List<String> headerValues = new ArrayList<>();
            tunnelHttpRequest.getHeaders().put(headerName, headerValues);

            for (final Enumeration<String> e1 = request.getHeaders(headerName);
                    e1.hasMoreElements(); ) {

                headerValues.add(e1.nextElement());
            }
        }

        try {
            tunnelHttpRequest.setBody(
                    request.getReader()
                            .lines()
                            .collect(Collectors.joining(System.lineSeparator())));

            final WebSocketSession webSocketSession =
                    idToActiveSession.get(getSessionId(request, response));

            if (webSocketSession == null) {

                response.setStatus(404);
                return;
            }

            webSocketSession.sendMessage(
                    new TextMessage(objectMapper.writeValueAsString(tunnelHttpRequest)));

            while (true) {

                final TunnelHttpResponse tunnelHttpResponse =
                        tunnelHttpResponseMap.get(tunnelHttpRequest.getId());

                if (tunnelHttpResponse == null) {

                    Thread.sleep(1000);

                    continue;
                }

                response.setStatus(tunnelHttpResponse.getStatusCode());

                final Map<String, List<String>> headers = tunnelHttpResponse.getHeaders();

                if (headers != null) {

                    headers.forEach(
                            (headerName, strings) ->
                                    strings.forEach(
                                            headerValue ->
                                                    response.setHeader(headerName, headerValue)));
                }

                if (tunnelHttpResponse.getBody() != null) {

                    response.getWriter().write(tunnelHttpResponse.getBody());
                    response.getWriter().flush();
                }

                return;
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String getSessionId(
            final HttpServletRequest request, final HttpServletResponse response) {

        final String subdomain = request.getServerName().split("\\.")[0];

        return new ArrayList<>(idToActiveSession.keySet()).get(0);
    }
}
