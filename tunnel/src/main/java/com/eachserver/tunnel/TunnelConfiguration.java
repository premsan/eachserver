package com.eachserver.tunnel;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class TunnelConfiguration implements WebSocketConfigurer {

    private final TunnelServlet tunnelServlet;
    private final TunnelClientWebSocketHandler tunnelClientWebSocketHandler;
    private final TunnelServerWebSocketHandler tunnelServerWebSocketHandler;

    //    @Bean
    //    public ServletRegistrationBean tunnelServletBean() {
    //
    //        return new ServletRegistrationBean(tunnelServlet);
    //    }

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

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(tunnelServerWebSocketHandler, "/tunnel");
    }
}
