package faang.school.projectservice.utils;

import faang.school.projectservice.exception.FileProcessingException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@UtilityClass
@Slf4j
public class ImageUtils {
    @Value("${app.project.image.max_width}")
    private int maxWidth;
    @Value("${app.project.image.max_height}")
    private int maxHeight;
    @Value("${app.project.image.quality}")
    private float quality;
    public static BufferedImage read(ImageInputStreamWrapper supplier) {
        try {
            return ImageIO.read(supplier.getInputStream());
        } catch (IOException e) {
            log.error("Failed to read uploaded image file.");
            throw new FileProcessingException("Failed to read uploaded image file", e);
        }
    }


    public static byte[] resizeToFit(BufferedImage src, int maxW, int maxH, String outputFormat, float quality) throws IOException {
        int w = src.getWidth();
        int h = src.getHeight();
        double scale = Math.min((double) maxW / w, (double) maxH / h);
        if (scale >= 1.0) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(src, outputFormat, baos);
            return baos.toByteArray();
        }
        int nw = (int) Math.round(w * scale);
        int nh = (int) Math.round(h * scale);
        BufferedImage resized = new BufferedImage(nw, nh, src.getType() == 0 ? BufferedImage.TYPE_INT_RGB : src.getType());
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(src, 0, 0, nw, nh, null);
        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resized, outputFormat, baos);
        return baos.toByteArray();
    }

    public static byte[]  zipImage(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        try {
            byte[] originalBytes = file.getBytes();
            ImageUtils.ImageInputStreamWrapper wrapper = () -> new ByteArrayInputStream(originalBytes);
            BufferedImage img = ImageUtils.read(wrapper);
            if (img == null) {
                throw new FileProcessingException("Cannot read image");
            }

            int w = img.getWidth();
            int h = img.getHeight();

            boolean isSquare = w == h;
            boolean isHorizontal = w > h;

            String outFormat = file.getContentType().equals("image/png") ? "png" : "jpg";

            if (isHorizontal) {
                if (w > maxWidth || h > maxHeight) {
                    log.debug("Resizing horizontal image. name={}, w={}, h={}", originalName, w, h);
                    return ImageUtils.resizeToFit(img, maxWidth, maxHeight, outFormat, quality);
                } else {
                    return originalBytes;
                }
            } else if (isSquare) {
                if (w > maxWidth) {
                    log.debug("Resizing square image. name={}, w={}, h={}", originalName, w, h);
                    return ImageUtils.resizeToFit(img, maxWidth, maxWidth, outFormat, quality);
                } else {
                    return originalBytes;
                }
            } else {
                return originalBytes;
            }
        } catch (IOException e) {
            log.error("Failed to read uploaded image file. name={}, contentType={}",
                    originalName, file.getContentType(), e);
            throw new FileProcessingException("Failed to read uploaded image file", e);
        }
    }

    public interface ImageInputStreamWrapper {
        InputStream getInputStream() throws IOException;
    }
}