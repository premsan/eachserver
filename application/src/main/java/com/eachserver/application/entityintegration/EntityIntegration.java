package com.eachserver.application.entityintegration;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Getter
@Setter
@AllArgsConstructor
public class EntityIntegration {

    private EntityIntegrationMapping entityIntegrationMapping;

    private String path;

    public int priority() {

        return entityIntegrationMapping.priority();
    }

    public String getPath(final Map<String, ?> uriVariables) {

        final UriComponents uriComponentsBuilder =
                UriComponentsBuilder.fromPath(path).buildAndExpand(uriVariables);
        return uriComponentsBuilder.getPath();
    }
}
