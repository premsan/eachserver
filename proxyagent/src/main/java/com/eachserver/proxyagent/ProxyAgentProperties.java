package com.eachserver.proxyagent;

import java.net.URI;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "com.eachserver.proxyagent")
public class ProxyAgentProperties {

    private URI server;

    private String username;

    private String password;
}
