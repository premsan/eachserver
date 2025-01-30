package com.eachserver.security;

import com.eachserver.security.authority.AuthorityRepository;
import com.eachserver.security.permissionevaluator.PermissionEvaluators;
import com.eachserver.security.user.User;
import com.eachserver.security.user.UserRepository;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;
import org.springframework.util.CollectionUtils;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final SecurityProperties securityProperties;

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {

        return http.oauth2Login(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .csrf(
                        httpSecurityCsrfConfigurer ->
                                httpSecurityCsrfConfigurer.csrfTokenRepository(
                                        new CookieCsrfTokenRepository()))
                .csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable())
                .rememberMe((rememberMe) -> rememberMe.rememberMeServices(rememberMeServices()))
                .anonymous(
                        httpSecurityAnonymousConfigurer ->
                                httpSecurityAnonymousConfigurer.authorities(
                                        securityProperties.getAnonymousGrantedAuthorities()))
                .build();
    }

    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService(
            final AuthorityRepository authorityRepository, final UserRepository userRepository) {
        final OidcUserService delegate = new OidcUserService();

        return (userRequest) -> {
            final OidcUser oidcUser = delegate.loadUser(userRequest);

            final ClientRegistration.ProviderDetails providerDetails =
                    userRequest.getClientRegistration().getProviderDetails();
            final String email =
                    oidcUser.getClaimAsString(
                            providerDetails.getUserInfoEndpoint().getUserNameAttributeName());

            User user = userRepository.findByEmailIgnoreCase(email);

            if (user != null) {

                if (Boolean.TRUE.equals(user.getDisabled())) {

                    throw new DisabledException("User is disabled");
                }
            }
            if (user == null) {

                final String userId = UUID.randomUUID().toString();
                user = new User();
                user.setId(userId);
                user.setEmail(email);
                user.setDisabled(Boolean.FALSE);
                user.setUpdatedAt(System.currentTimeMillis());
                user.setUpdatedBy(userId);

                user = userRepository.save(user);
            }

            final Set<String> admins = securityProperties.getAdmins();

            final Collection<GrantedAuthority> authorities =
                    authorityRepository.findByUserId(user.getId()).stream()
                            .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                            .collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(admins) && admins.contains(email)) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }

            authorities.addAll(securityProperties.getAuthenticatedGrantedAuthorities());

            return new DefaultUser(
                    user.getId(), authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
        };
    }

    @Bean
    public SpringSessionRememberMeServices rememberMeServices() {
        final SpringSessionRememberMeServices rememberMeServices =
                new SpringSessionRememberMeServices();
        // optionally customize
        rememberMeServices.setAlwaysRemember(true);
        return rememberMeServices;
    }

    @Bean
    public NamedParameterJdbcOperations namedParameterJdbcOperations(final DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    static MethodSecurityExpressionHandler expressionHandler(
            final PermissionEvaluators permissionEvaluators) {
        final DefaultMethodSecurityExpressionHandler expressionHandler =
                new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(permissionEvaluators);
        return expressionHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    static class DefaultUser extends DefaultOidcUser {

        private String id;

        public DefaultUser(
                final String id,
                final Collection<? extends GrantedAuthority> authorities,
                final OidcIdToken idToken,
                final OidcUserInfo userInfo) {
            super(authorities, idToken, userInfo);

            this.id = id;
        }

        @Override
        public String getName() {
            return id;
        }
    }

    @Bean
    public UserDetailsService userDetailsService(
            final SecurityProperties securityProperties,
            final UserRepository userRepository,
            final AuthorityRepository authorityRepository) {

        return username -> {
            Optional<User> userOptional = userRepository.findById(username);

            if (userOptional.isEmpty()) {

                throw new UsernameNotFoundException("userId " + username + " not found");
            }

            final Collection<GrantedAuthority> authorities =
                    authorityRepository.findByUserId(userOptional.get().getId()).stream()
                            .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                            .collect(Collectors.toList());

            authorities.addAll(securityProperties.getAuthenticatedGrantedAuthorities());

            final org.springframework.security.core.userdetails.User user =
                    new org.springframework.security.core.userdetails.User(
                            userOptional.get().getId(),
                            userOptional.get().getPasswordHash(),
                            !userOptional.get().getDisabled(),
                            true,
                            true,
                            true,
                            authorities);

            return user;
        };
    }
}
