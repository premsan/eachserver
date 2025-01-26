package com.eachserver.application.entityaction;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Getter
@Setter
@AllArgsConstructor
public class EntityAction {

    private EntityActionMapping entityActionMapping;

    private String path;

    public int priority() {

        return entityActionMapping.priority();
    }

    public String getPath(final Map<String, ?> uriVariables) {

        final UriComponents uriComponentsBuilder =
                UriComponentsBuilder.fromPath(path).buildAndExpand(uriVariables);
        return uriComponentsBuilder.getPath();
    }
}
