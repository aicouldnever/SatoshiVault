package com.wallet.app.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Utility class for QR code generation and decoding using ZXing library.
 */
public class QRCodeUtil {
    
    private static final int QR_CODE_SIZE = 300;
    
    /**
     * Generate QR code image from text data
     * @param data The data to encode in the QR code (e.g., Bitcoin address)
     * @return JavaFX Image containing the QR code
     */
    public static Image generate(String data) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE);
            
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            return SwingFXUtils.toFXImage(bufferedImage, null);
            
        } catch (WriterException e) {
            System.err.println("Error generating QR code: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Decode QR code from JavaFX Image
     * @param image The JavaFX Image containing the QR code
     * @return Decoded string data from the QR code
     */
    public static String decode(Image image) {
        try {
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
            return decodeBufferedImage(bufferedImage);
            
        } catch (Exception e) {
            System.err.println("Error decoding QR code: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Decode QR code from file
     * @param file The image file containing the QR code
     * @return Decoded string data from the QR code
     */
    public static String decodeFromFile(File file) {
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            return decodeBufferedImage(bufferedImage);
            
        } catch (IOException e) {
            System.err.println("Error reading QR code file: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Decode QR code from BufferedImage
     */
    private static String decodeBufferedImage(BufferedImage bufferedImage) {
        try {
            BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
            
        } catch (NotFoundException e) {
            System.err.println("No QR code found in image");
            return null;
        }
    }
}

