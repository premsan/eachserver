package com.eachserver.partner;

import com.eachserver.partner.api.PartnerAPI;
import com.eachserver.security.webkey.WebKeyProvider;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class PartnerTokenProvider {

    private static final String WELL_KNOWN_JWKS_PATH = "/.well-known/jwks.json";

    private final WebKeyProvider webKeyProvider;
    private final HttpServletRequest httpServletRequest;

    public SignedJWT createToken(
            final PartnerAPI partnerAPI, final JWTClaimsSet.Builder claimsSetBuilder)
            throws JOSEException {

        final RSAKey rsaKey = webKeyProvider.getOrCreate().toRSAKey();
        final RSASSASigner signer = new RSASSASigner(rsaKey);

        claimsSetBuilder.subject(partnerAPI.getPath());
        claimsSetBuilder.audience(partnerAPI.getHost());

        final SignedJWT signedJWT =
                new SignedJWT(
                        new JWSHeader.Builder(JWSAlgorithm.RS256)
                                .keyID(rsaKey.getKeyID())
                                .jwkURL(jwkURL())
                                .build(),
                        claimsSetBuilder.build());

        signedJWT.sign(signer);

        return signedJWT;
    }

    private URI jwkURL() {

        return UriComponentsBuilder.fromHttpUrl(httpServletRequest.getRequestURL().toString())
                .replacePath(WELL_KNOWN_JWKS_PATH)
                .build()
                .toUri();
    }
}
