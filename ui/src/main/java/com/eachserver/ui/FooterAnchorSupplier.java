package com.eachserver.ui;

import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public interface FooterAnchorSupplier extends Supplier<FooterAnchorSupplier.Anchor> {

    @Getter
    @Setter
    @AllArgsConstructor
    class Anchor {

        private String href;

        private String text;
    }
}
