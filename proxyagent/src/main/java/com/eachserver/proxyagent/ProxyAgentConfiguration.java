package com.eachserver.proxyagent;

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
public class ProxyAgentConfiguration {

    private static final int MAX_MESSAGE_BUFFER_SIZE = 10 * 1024 * 1024;

    private final ProxyAgentProperties proxyAgentProperties;
    private final ProxyAgentWebSocketHandler proxyAgentWebSocketHandler;

    @Bean
    public ServletServerContainerFactoryBean createServletServerContainerFactoryBean() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(MAX_MESSAGE_BUFFER_SIZE);
        container.setMaxBinaryMessageBufferSize(MAX_MESSAGE_BUFFER_SIZE);
        return container;
    }

    @Bean
    public WebSocketConnectionManager webSocketConnectionManager() {

        WebSocketConnectionManager webSocketConnectionManager =
                new ProxyAgentWebSocketConnectionManager(
                        proxyAgentProperties,
                        new StandardWebSocketClient(),
                        proxyAgentWebSocketHandler);

        webSocketConnectionManager
                .getHeaders()
                .setBasicAuth(
                        proxyAgentProperties.getUsername(), proxyAgentProperties.getPassword());
        return webSocketConnectionManager;
    }
}
