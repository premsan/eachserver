package com.eachserver.security.sign;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.SignedJWT;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.util.UriComponentsBuilder;

public class SignedTokenHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String SIGNED_TOKEN_PARAM_NAME = "signedToken";
    private static final String WELL_KNOWN_JWKS_PATH = "/.well-known/jwks.json";

    private final Map<URL, JWKSource<SecurityContext>> urljwkSourceMap = new HashMap<>();

    private final ResourceRetriever resourceRetriever =
            new DefaultResourceRetriever() {

                @Override
                public int getReadTimeout() {

                    return 5 * 1000;
                }
            };

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {

        return parameter.getParameterAnnotation(SignedToken.class) != null
                && parameter.getParameterType().isAssignableFrom(SignedJWT.class);
    }

    @Override
    public Object resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory)
            throws Exception {

        final String signedToken = webRequest.getParameter(SIGNED_TOKEN_PARAM_NAME);

        if (signedToken == null) {

            return null;
        }

        final SignedJWT signedJWT = SignedJWT.parse(signedToken);

        final URL jwkURL = getJWKURL(signedJWT);

        if (jwkURL == null) {

            return null;
        }

        final String keyID = signedJWT.getHeader().getKeyID();

        final JWKMatcher jwkMatcher =
                new JWKMatcher.Builder()
                        .keyType(KeyType.RSA)
                        .keyUse(KeyUse.SIGNATURE)
                        .keyID(keyID)
                        .build();

        final JWKSource<SecurityContext> jwkSource = getJWKSource(jwkURL);
        final List<JWK> jwks = jwkSource.get(new JWKSelector(jwkMatcher), null);

        if (jwks.size() == 0) {

            return null;
        }

        final JWSVerifier verifier = new RSASSAVerifier(jwks.get(0).toRSAKey());

        return signedJWT.verify(verifier) ? signedJWT : null;
    }

    private URL getJWKURL(final SignedJWT signedJWT) {

        try {

            final URI jwkURL = signedJWT.getHeader().getJWKURL();

            if (jwkURL != null) {

                return jwkURL.toURL();
            }

            final String issuer = signedJWT.getJWTClaimsSet().getIssuer();

            return UriComponentsBuilder.fromHttpUrl(issuer)
                    .path(WELL_KNOWN_JWKS_PATH)
                    .build()
                    .toUri()
                    .toURL();

        } catch (final ParseException e) {

            return null;

        } catch (final MalformedURLException e) {

            return null;
        }
    }

    private JWKSource<SecurityContext> getJWKSource(final URL url) {

        JWKSource<SecurityContext> jwkSource = urljwkSourceMap.get(url);

        if (jwkSource == null) {

            jwkSource = JWKSourceBuilder.create(url, resourceRetriever).build();

            urljwkSourceMap.put(url, jwkSource);
        }

        return jwkSource;
    }
}
