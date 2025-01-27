package com.eachserver.tunnel;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Service
@RequiredArgsConstructor
public class TunnelServerWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> idToActiveSession = new HashMap<>();

    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        idToActiveSession.put(session.getId(), session);
        super.afterConnectionEstablished(session);
    }

    @Override
    protected void handleTextMessage(final WebSocketSession session, final TextMessage message) {

        System.out.println(message);
    }

    public void sendMessage(final TunnelHttpRequest tunnelHttpRequest) {
        idToActiveSession.forEach(
                (s, webSocketSession) -> {
                    try {
                        webSocketSession.sendMessage(
                                new TextMessage(
                                        objectMapper.writeValueAsString(tunnelHttpRequest)));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
