package com.eachserver.tunnelserver;

import com.eachserver.api.TunnelHttpRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.GenericFilterBean;

@Service
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class TunnelFilter extends GenericFilterBean {

    private final TunnelServerWebSocketHandler serverWebSocketHandler;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        final String subdomain = req.getServerName().split("\\.")[0];

        if (!subdomain.startsWith("tunnel-")) {

            chain.doFilter(req, res);
            return;
        }

        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;

        final TunnelHttpRequest tunnelHttpRequest = new TunnelHttpRequest();
        tunnelHttpRequest.setUri(URI.create(request.getRequestURI()));
        tunnelHttpRequest.setMethod(HttpMethod.valueOf(request.getMethod()));
        tunnelHttpRequest.setHeaders(tunnelHttpRequest.getHeaders());
        tunnelHttpRequest.setBody(
                request.getReader().lines().collect(Collectors.joining(System.lineSeparator())));
        serverWebSocketHandler.sendMessage(request, response);
        response.getWriter().println(Thread.currentThread().toString());
    }
}
