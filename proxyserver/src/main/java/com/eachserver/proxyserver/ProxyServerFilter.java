package com.eachserver.proxyserver;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.GenericFilterBean;

@Service
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class ProxyServerFilter extends GenericFilterBean {

    private final ProxyServerProperties proxyServerProperties;
    private final ProxyServerWebSocketHandler serverWebSocketHandler;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;

        final String host = request.getHeader("Host");
        final String proxyHost = host.substring(host.indexOf('.') + 1);

        String username = null;
        if (proxyHost.length() < host.length()) {

            username = host.substring(0, host.indexOf('.'));
        }

        if (!(username != null && proxyServerProperties.getHosts().contains(proxyHost))) {

            chain.doFilter(req, res);
            return;
        }

        serverWebSocketHandler.sendMessage(username, request, response);
    }
}
