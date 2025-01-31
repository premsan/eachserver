package com.eachserver.proxyagent;

import com.eachserver.application.feature.FeatureMapping;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.socket.client.WebSocketConnectionManager;

@Controller
@RequiredArgsConstructor
public class ProxyAgentStatusController {

    private final WebSocketConnectionManager webSocketConnectionManager;

    @FeatureMapping
    @GetMapping("/proxyagent/proxy-agent-status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('PROXY_AGENT_STATUS')")
    public ModelAndView proxyAgentStatusGet() {

        final ModelAndView modelAndView =
                new ModelAndView("com/eachserver/proxyagent/templates/proxy-agent-status");

        final ProxyAgentStatus proxyAgentStatus = new ProxyAgentStatus();
        proxyAgentStatus.setStart(webSocketConnectionManager.isRunning());

        modelAndView.addObject("proxyAgentStatus", proxyAgentStatus);

        return modelAndView;
    }

    @FeatureMapping
    @PostMapping("/proxyagent/proxy-agent-status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('PROXY_AGENT_STATUS')")
    public ModelAndView proxyAgentStatusPost(
            final @Valid @ModelAttribute("proxyAgentStatus") ProxyAgentStatus proxyAgentStatus) {

        final ModelAndView modelAndView =
                new ModelAndView("com/eachserver/proxyagent/templates/proxy-agent-status");

        if (proxyAgentStatus.isStart()) {

            webSocketConnectionManager.start();
        } else {
            webSocketConnectionManager.stop();
        }

        return modelAndView;
    }

    @Getter
    @Setter
    private class ProxyAgentStatus {

        private boolean start;
    }
}
