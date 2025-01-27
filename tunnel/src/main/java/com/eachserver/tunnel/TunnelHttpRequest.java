package com.eachserver.tunnel;

import java.net.URI;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

@Getter
@Setter
public class TunnelHttpRequest {

    private HttpMethod method;

    private URI uri;

    private HttpHeaders headers;

    private String body;
}
