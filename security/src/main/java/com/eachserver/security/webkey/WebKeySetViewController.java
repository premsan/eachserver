package com.eachserver.security.webkey;

import com.nimbusds.jose.jwk.JWKSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WebKeySetViewController {

    private final WebKeyProvider webKeyProvider;

    @GetMapping("/.well-known/jwks.json")
    public ResponseEntity<?> jwksViewGet() {

        return ResponseEntity.ok(
                new JWKSet(webKeyProvider.getOrCreate().toRSAKey()).toJSONObject());
    }
}
