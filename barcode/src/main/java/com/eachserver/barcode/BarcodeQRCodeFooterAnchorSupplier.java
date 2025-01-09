package com.eachserver.barcode;

import com.eachserver.ui.FooterAnchorSupplier;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BarcodeQRCodeFooterAnchorSupplier implements FooterAnchorSupplier {

    private final HttpServletRequest httpServletRequest;

    @Override
    public Anchor get() {
        return new Anchor(
                "/barcode/qr-code-view?contents="
                        + new ServletServerHttpRequest(httpServletRequest).getURI(),
                "Generate QR Code");
    }
}
