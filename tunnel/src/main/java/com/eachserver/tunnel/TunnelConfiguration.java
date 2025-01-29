package com.eachserver.tunnel;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.WebSocketContainer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class TunnelConfiguration {

    private final TunnelClientWebSocketHandler tunnelClientWebSocketHandler;

    @Bean
    public ServletServerContainerFactoryBean createServletServerContainerFactoryBean() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(32768);
        container.setMaxBinaryMessageBufferSize(32768);
        return container;
    }

    @Bean
    public WebSocketConnectionManager webSocketConnectionManager() {

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.setDefaultMaxTextMessageBufferSize(15000000);
        container.setDefaultMaxBinaryMessageBufferSize(15000000);

        WebSocketConnectionManager webSocketConnectionManager =
                new WebSocketConnectionManager(
                        new StandardWebSocketClient(container),
                        tunnelClientWebSocketHandler,
                        "ws://localhost:8081/tunnel");
        webSocketConnectionManager.setAutoStartup(true);
        return webSocketConnectionManager;
    }
}
