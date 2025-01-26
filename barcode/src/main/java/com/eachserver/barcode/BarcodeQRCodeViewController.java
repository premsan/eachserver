package com.eachserver.barcode;

import com.eachserver.application.feature.FeatureMapping;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.validation.constraints.NotBlank;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import javax.imageio.ImageIO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class BarcodeQRCodeViewController {

    private static final String PNG_FORMAT_NAME = "PNG";

    @FeatureMapping
    @GetMapping("/barcode/qr-code-view")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('BARCODE_QR_CODE_VIEW')")
    public ModelAndView barcodeQRCodeViewGet(final QRCodeViewGet qrCodeViewGet)
            throws WriterException, IOException {

        final QRCodeWriter barcodeWriter = new QRCodeWriter();

        final BitMatrix bitMatrix =
                barcodeWriter.encode(
                        qrCodeViewGet.getContents(),
                        BarcodeFormat.QR_CODE,
                        qrCodeViewGet.getWidth(),
                        qrCodeViewGet.getHeight());
        final BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, PNG_FORMAT_NAME, os);

        final ModelAndView modelAndView =
                new ModelAndView("com/eachserver/barcode/templates/qr-code-view");
        modelAndView.addObject("qrCode", Base64.getEncoder().encodeToString(os.toByteArray()));

        return modelAndView;
    }

    @Getter
    @Setter
    private static class QRCodeViewGet {

        @NotBlank private String contents;

        private Integer width = 256;

        private Integer height = 256;
    }
}
