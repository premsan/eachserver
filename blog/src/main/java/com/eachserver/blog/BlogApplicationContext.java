package com.eachserver.blog;

import static org.asciidoctor.Asciidoctor.Factory.create;

import lombok.Getter;
import org.asciidoctor.Asciidoctor;
import org.springframework.stereotype.Component;

@Getter
@Component
public class BlogApplicationContext {

    private final Asciidoctor asciidoctor = create();
}
