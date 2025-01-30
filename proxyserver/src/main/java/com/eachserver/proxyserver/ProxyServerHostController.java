package com.eachserver.proxyserver;

import ch.qos.logback.core.testUtil.RandomUtil;
import com.eachserver.api.ProxyServerHost;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ProxyServerHostController {

    private final ProxyServerProperties proxyServerProperties;

    @GetMapping("/proxyserver/host.json")
    public ResponseEntity<?> uri() {

        if (CollectionUtils.isEmpty(proxyServerProperties.getHostUris())) {

            return ResponseEntity.noContent().build();
        }

        final ProxyServerHost.ResponseBody.Host host = new ProxyServerHost.ResponseBody.Host();
        host.setUri(
                proxyServerProperties
                        .getHostUris()
                        .get(
                                RandomUtil.getPositiveInt()
                                        % proxyServerProperties.getHostUris().size()));

        final ProxyServerHost.ResponseBody responseBody = new ProxyServerHost.ResponseBody();
        responseBody.setHost(host);

        return ResponseEntity.ok(responseBody);
    }
}
