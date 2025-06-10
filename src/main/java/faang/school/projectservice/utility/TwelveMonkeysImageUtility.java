package faang.school.projectservice.utility;

import faang.school.projectservice.excepcion.ImageProcessingException;
import faang.school.projectservice.model.internal.ProcessedImage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Locale;

@Component
@Slf4j
public class TwelveMonkeysImageUtility {
    public static final int MAX_DIMENSION = 512;
    private static final float JPEG_COMPRESSION_QUALITY = 0.80f;
    private static final String OUTPUT_CONTENT_TYPE_JPEG = "image/jpeg";
    private static final String OUTPUT_EXTENSION_JPEG = "jpg";

    public ProcessedImage processAndResizeImage(InputStream originalImageStream,
                                                String originalObjectKey,
                                                String originalContentType) {

        byte[] originalImageBytes;
        try {
            originalImageBytes = toByteArray(originalImageStream);
        } catch (IOException e) {
            log.error("Failed to read original image stream for key [{}]: {}", originalObjectKey, e.getMessage());
            throw new ImageProcessingException(
                    String.format("Failed to read original image stream for %s", originalObjectKey), e
            );
        }

        try (ByteArrayInputStream bais = new ByteArrayInputStream(originalImageBytes)) {
            BufferedImage image = ImageIO.read(bais);
            if (image == null) {
                log.error("Could not decode image for key [{}]. " +
                                "ImageIO.read returned null. Original content type: {}",
                        originalObjectKey, originalContentType);
                throw new ImageProcessingException(
                        String.format("Unsupported image format or invalid image data for %s",
                                originalObjectKey)
                );
            }
            int originalWidth = image.getWidth();
            int originalHeight = image.getHeight();
            log.debug("Original image dimensions for key [{}]: {}x{}",
                    originalObjectKey, originalWidth, originalHeight);

            if (originalWidth > MAX_DIMENSION || originalHeight > MAX_DIMENSION) {
                log.info("Image key [{}] ({}x{}) exceeds max dimension of {}px. " +
                                "Resizing and compressing to JPEG Q={}.",
                        originalObjectKey, originalWidth, originalHeight,
                        MAX_DIMENSION, (int) (JPEG_COMPRESSION_QUALITY * 100));

                int targetWidth = originalWidth;
                int targetHeight = originalHeight;

                if (originalWidth > MAX_DIMENSION) {
                    targetWidth = MAX_DIMENSION;
                    targetHeight = (targetWidth * originalHeight) / originalWidth;
                }

                if (targetHeight > MAX_DIMENSION) {
                    targetHeight = MAX_DIMENSION;
                    targetWidth = (targetHeight * originalWidth) / originalHeight;
                }

                targetWidth = Math.max(1, targetWidth);
                targetHeight = Math.max(1, targetHeight);

                BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_BGR);
                Graphics2D g = resizedImage.createGraphics();
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, targetWidth, targetHeight);
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.drawImage(image, 0, 0, targetWidth, targetHeight, null);
                g.dispose();

                return compressAndPackageAsJpeg(resizedImage, originalObjectKey, targetWidth, targetHeight);
            } else {
                log.info("Image key [{}] ({}x{}) is within max dimensions. " +
                                "No processing needed. Returning original.",
                        originalObjectKey, originalWidth, originalHeight);
                return new ProcessedImage(
                        new ByteArrayInputStream(originalImageBytes),
                        originalImageBytes.length,
                        originalContentType,
                        extractExtension(originalObjectKey)
                );
            }
        } catch (IOException e) {
            log.error("IOException during image processing for key [{}]: {}", originalObjectKey, e.getMessage());
            throw new ImageProcessingException(String.format("Failed to process image %s", originalObjectKey));
        }
    }

    private ProcessedImage compressAndPackageAsJpeg(BufferedImage imageToCompress,
                                                    String originalObjectKey,
                                                    int targetWidth,
                                                    int targetHeight) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageWriter jpegWriter = getJpegWriter();
            ImageWriteParam jpegParams = jpegWriter.getDefaultWriteParam();
            jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            jpegParams.setCompressionQuality(JPEG_COMPRESSION_QUALITY);

            try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
                jpegWriter.setOutput(ios);
                jpegWriter.write(null,
                        new IIOImage(imageToCompress, null, null), jpegParams);
            } finally {
                jpegWriter.dispose();
            }

            byte[] processedBytes = baos.toByteArray();
            log.info("Resized and compressed image key [{}] to {}x{}, {} bytes, content type: {}",
                    originalObjectKey, targetWidth, targetHeight,
                    processedBytes.length, OUTPUT_CONTENT_TYPE_JPEG);

            return new ProcessedImage(
                    new ByteArrayInputStream(processedBytes),
                    processedBytes.length,
                    OUTPUT_CONTENT_TYPE_JPEG,
                    OUTPUT_EXTENSION_JPEG
            );
        } catch (IOException e) {
            log.error("IOException during JPEG compression for key [{}]: {}",
                    originalObjectKey, e.getMessage());
            throw new ImageProcessingException(String.format("Failed to compress image %s",
                    originalObjectKey));
        }
    }


    private ImageWriter getJpegWriter() throws ImageProcessingException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
        if (!writers.hasNext()) {
            log.error("No JPEG ImageWriter found. This is unexpected with TwelveMonkeys on classpath.");
            throw new ImageProcessingException("No JPEG writer available. Check TwelveMonkeys integration.");
        }
        return writers.next();
    }

    private byte[] toByteArray(InputStream in) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            return out.toByteArray();
        }
    }

    private String extractExtension(String objectKey) {
        if (objectKey == null || objectKey.isEmpty()) {
            return "";
        }
        int lastDot = objectKey.lastIndexOf('.');
        if (lastDot > 0 && lastDot < objectKey.length() - 1) {
            int lastSlash = objectKey.lastIndexOf('/');
            if (lastDot > lastSlash) {
                return objectKey.substring(lastDot + 1).toLowerCase(Locale.ROOT);
            }
        }
        return "";
    }
}
