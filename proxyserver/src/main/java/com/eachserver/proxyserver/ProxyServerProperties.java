package com.eachserver.proxyserver;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
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

    public List<String> getHosts() {

        return hostUris.stream().map(URI::getAuthority).collect(Collectors.toList());
    }
}
