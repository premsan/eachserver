package com.eachserver.tunnel;

import jakarta.servlet.GenericServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TunnelServlet extends GenericServlet {

    private final TunnelServerWebSocketHandler serverWebSocketHandler;

    @Override
    public void service(ServletRequest req, ServletResponse res)
            throws ServletException, IOException {

        HttpServletRequest request;
        HttpServletResponse response;

        try {
            request = (HttpServletRequest) req;
            response = (HttpServletResponse) res;
        } catch (ClassCastException e) {
            throw new ServletException();
        }
        final TunnelHttpRequest tunnelHttpRequest = new TunnelHttpRequest();
        tunnelHttpRequest.setUri(URI.create(request.getRequestURI()));
        tunnelHttpRequest.setMethod(HttpMethod.valueOf(request.getMethod()));
        tunnelHttpRequest.setHeaders(tunnelHttpRequest.getHeaders());
        tunnelHttpRequest.setBody(
                request.getReader().lines().collect(Collectors.joining(System.lineSeparator())));
        serverWebSocketHandler.sendMessage(tunnelHttpRequest);
        response.getWriter().println("OOKK");
    }
}
