package com.eachserver.ui;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
@Service("FooterAnchors")
@RequiredArgsConstructor
public class FooterAnchors {

    private final List<FooterAnchorSupplier> anchorSuppliers;
}
