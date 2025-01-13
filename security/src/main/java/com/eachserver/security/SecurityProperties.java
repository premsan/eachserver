package com.eachserver.security;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "com.eachserver.security")
public class SecurityProperties {

    private Set<String> admins;

    private Set<String> authenticatedAuthorities;

    private Set<String> anonymousAuthorities;
}
