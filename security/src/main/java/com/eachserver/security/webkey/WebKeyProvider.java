package com.eachserver.security.webkey;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebKeyProvider {

    private final WebKeyRepository webKeyRepository;

    public WebKey getOrCreate() {

        List<WebKey> webKeys = webKeyRepository.findAll();

        if (webKeys.size() > 0) {

            return webKeys.get(0);
        }

        final RSAKey rsaKey;
        try {
            rsaKey =
                    new RSAKeyGenerator(2048)
                            .keyUse(KeyUse.SIGNATURE)
                            .keyID(UUID.randomUUID().toString())
                            .issueTime(new Date())
                            .generate();

        } catch (final JOSEException e) {

            return null;
        }

        final WebKey webKey =
                webKeyRepository.save(
                        new WebKey(
                                UUID.randomUUID().toString(),
                                null,
                                rsaKey.getKeyID(),
                                rsaKey.toJSONString(),
                                System.currentTimeMillis()));

        return webKey;
    }
}
