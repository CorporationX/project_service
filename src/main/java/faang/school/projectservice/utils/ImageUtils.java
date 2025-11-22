package faang.school.projectservice.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class ImageUtils {
    public static BufferedImage read(ImageInputStreamWrapper supplier) throws IOException {
        return ImageIO.read(supplier.getInputStream());
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

    public interface ImageInputStreamWrapper {
        InputStream getInputStream() throws IOException;
    }
}