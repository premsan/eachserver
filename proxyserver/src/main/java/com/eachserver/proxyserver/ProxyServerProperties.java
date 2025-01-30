package com.eachserver.proxyserver;

import java.net.URI;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "com.eachserver.proxyserver")
public class ProxyServerProperties {

    private List<URI> hostUris;
}
