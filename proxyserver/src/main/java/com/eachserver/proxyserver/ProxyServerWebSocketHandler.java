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
import java.util.Objects;
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

    private static final int WAIT_RESPONSE_MAX_MS = 10_000;
    private static final int WAIT_RESPONSE_SLEEP_MS = 100;
    private static final int MAX_MESSAGE_BUFFER_SIZE = 10 * 1024 * 1024;

    private final Map<String, WebSocketSession> sessionByUsername = new ConcurrentHashMap<>();
    private final Map<String, ProxyHttpResponse> responseById = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        final String username = session.getPrincipal().getName();

        final WebSocketSession sessionWrapper =
                new ConcurrentWebSocketSessionDecorator(
                        session, WAIT_RESPONSE_MAX_MS, MAX_MESSAGE_BUFFER_SIZE);
        final WebSocketSession previousSession = sessionByUsername.put(username, sessionWrapper);

        if (Objects.nonNull(previousSession)) {

            previousSession.close();
        }

        super.afterConnectionEstablished(sessionWrapper);
    }

    @Override
    protected void handleTextMessage(final WebSocketSession session, final TextMessage message) {

        try {
            final ProxyHttpResponse proxyHttpResponse =
                    objectMapper.readValue(message.getPayload(), ProxyHttpResponse.class);

            responseById.put(proxyHttpResponse.getId(), proxyHttpResponse);

        } catch (final IOException e) {

            throw new RuntimeException(e);
        }
    }

    public void sendMessage(
            final String username,
            final HttpServletRequest request,
            final HttpServletResponse response) {

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

            final WebSocketSession session = sessionByUsername.get(username);

            if (session == null) {

                response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
                return;
            }

            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(proxyHttpRequest)));

            final long waitStartMs = System.currentTimeMillis();

            while (true) {

                final ProxyHttpResponse proxyHttpResponse =
                        responseById.remove(proxyHttpRequest.getId());

                if (proxyHttpResponse == null) {

                    if ((System.currentTimeMillis() - waitStartMs) > WAIT_RESPONSE_MAX_MS) {

                        response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
                        return;
                    }

                    Thread.sleep(WAIT_RESPONSE_SLEEP_MS);

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

        } catch (final IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
