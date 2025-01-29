package com.eachserver.api;

import java.net.URI;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

@Getter
@Setter
public class TunnelHttpRequest {

    private String id;

    private HttpMethod method;

    private URI uri;

    private HttpHeaders headers;

    private String body;
}
