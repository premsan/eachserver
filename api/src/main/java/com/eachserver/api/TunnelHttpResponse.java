package com.eachserver.api;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

@Getter
@Setter
public class TunnelHttpResponse {

    private String id;

    private HttpHeaders headers;

    private HttpStatusCode statusCode;

    private String body;
}
