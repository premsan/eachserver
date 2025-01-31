package com.eachserver.api;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProxyHttpResponse {

    private String id;

    private Map<String, List<String>> headers;

    private int statusCode;

    private String body;
}
