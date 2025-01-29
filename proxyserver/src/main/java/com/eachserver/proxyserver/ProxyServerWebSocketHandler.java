package com.eachserver.proxyserver;

import com.eachserver.api.ProxyHttpRequest;
import com.eachserver.api.ProxyHttpResponse;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class ProxyServerWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> idToActiveSession = new ConcurrentHashMap<>();
    private final Map<String, ProxyHttpResponse> responseMap = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        idToActiveSession.put(
                session.getId(),
                new ConcurrentWebSocketSessionDecorator(session, 10000, 10 * 1024 * 1024));
        super.afterConnectionEstablished(session);
    }

    @Override
    protected void handleTextMessage(final WebSocketSession session, final TextMessage message) {

        try {
            final ProxyHttpResponse proxyHttpResponse =
                    objectMapper.readValue(message.getPayload(), ProxyHttpResponse.class);

            responseMap.put(proxyHttpResponse.getId(), proxyHttpResponse);

        } catch (final IOException e) {

            throw new RuntimeException(e);
        }
    }

    public void sendMessage(final HttpServletRequest request, final HttpServletResponse response) {

        final ProxyHttpRequest proxyHttpRequest = new ProxyHttpRequest();

        proxyHttpRequest.setId(UUID.randomUUID().toString());
        proxyHttpRequest.setUri(
                UriComponentsBuilder.fromPath(request.getRequestURI())
                        .replaceQuery(request.getQueryString())
                        .build()
                        .toUri());
        proxyHttpRequest.setMethod(request.getMethod());
        proxyHttpRequest.setHeaders(new HashMap<>());

        for (final Enumeration<String> e = request.getHeaderNames(); e.hasMoreElements(); ) {

            final String headerName = e.nextElement();
            final List<String> headerValues = new ArrayList<>();
            proxyHttpRequest.getHeaders().put(headerName, headerValues);

            for (final Enumeration<String> e1 = request.getHeaders(headerName);
                    e1.hasMoreElements(); ) {

                headerValues.add(e1.nextElement());
            }
        }

        try {
            proxyHttpRequest.setBody(
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
                    new TextMessage(objectMapper.writeValueAsString(proxyHttpRequest)));

            while (true) {

                final ProxyHttpResponse proxyHttpResponse =
                        responseMap.get(proxyHttpRequest.getId());

                if (proxyHttpResponse == null) {

                    Thread.sleep(100);

                    continue;
                }

                response.setStatus(proxyHttpResponse.getStatusCode());

                final Map<String, List<String>> headers = proxyHttpResponse.getHeaders();

                if (headers != null) {

                    headers.forEach(
                            (headerName, strings) ->
                                    strings.forEach(
                                            headerValue ->
                                                    response.setHeader(headerName, headerValue)));
                }

                if (proxyHttpResponse.getBody() != null) {

                    response.getWriter().write(proxyHttpResponse.getBody());
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
