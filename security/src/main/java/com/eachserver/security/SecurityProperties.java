package com.eachserver.security;

import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.util.CollectionUtils;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "com.eachserver.security")
public class SecurityProperties {

    private Set<String> admins;

    private Set<String> authenticatedAuthorities;

    private Set<String> anonymousAuthorities;

    public List<GrantedAuthority> getAuthenticatedGrantedAuthorities() {

        if (CollectionUtils.isEmpty(authenticatedAuthorities)) {

            return AuthorityUtils.NO_AUTHORITIES;
        }

        return AuthorityUtils.createAuthorityList(anonymousAuthorities);
    }

    public List<GrantedAuthority> getAnonymousGrantedAuthorities() {

        final List<GrantedAuthority> anonymousGrantedAuthorities =
                AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS");

        if (CollectionUtils.isEmpty(anonymousAuthorities)) {

            return anonymousGrantedAuthorities;
        }

        anonymousGrantedAuthorities.addAll(
                AuthorityUtils.createAuthorityList(anonymousAuthorities));
        return anonymousGrantedAuthorities;
    }
}
