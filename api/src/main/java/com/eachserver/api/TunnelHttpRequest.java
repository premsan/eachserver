package com.eachserver.api;

import java.net.URI;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TunnelHttpRequest {

    private String id;

    private String method;

    private URI uri;

    private Map<String, List<String>> headers;

    private String body;
}
