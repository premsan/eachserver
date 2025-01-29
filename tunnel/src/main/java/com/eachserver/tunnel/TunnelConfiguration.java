package com.eachserver.tunnel;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class TunnelConfiguration {

    private final TunnelClientWebSocketHandler tunnelClientWebSocketHandler;

    @Bean
    public WebSocketConnectionManager webSocketConnectionManager() {

        WebSocketConnectionManager webSocketConnectionManager =
                new WebSocketConnectionManager(
                        new StandardWebSocketClient(),
                        tunnelClientWebSocketHandler,
                        "ws://localhost:8081/tunnel");
        webSocketConnectionManager.setAutoStartup(true);
        return webSocketConnectionManager;
    }
}
