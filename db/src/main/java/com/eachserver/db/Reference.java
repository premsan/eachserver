package com.eachserver.db;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reference {

    private String host;

    private String type;

    private String id;

    public Reference(final String reference) {
        final String[] parts = reference.split("::");

        this.host = parts[0];
        this.type = parts[1];
        this.id = parts[2];
    }

    @Override
    public String toString() {

        return host + "::" + type + "::" + id;
    }
}
