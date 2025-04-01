package faang.school.projectservice.imageUtils;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtils {
    public static MultipartFile compressImage(MultipartFile file) {
        BufferedImage originalImage = null;
        try {
            originalImage = ImageIO.read(file.getInputStream());

            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            if (width > 512 || height > 512) {
                double aspectRation = (double) width / height;
                if (aspectRation > 1) {
                    width = 512;
                    height = (int) (height / aspectRation);
                } else {
                    height = 512;
                    width = (int) (width * aspectRation);
                }
            }

            Image resizedImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            outputImage.getGraphics().drawImage(resizedImage, 0, 0, null);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(outputImage, "png", baos);
            baos.flush();
            byte[] imageBytes = baos.toByteArray();
            baos.close();

            return new MultipartFile() {
                @Override
                public String getName() {
                    return file.getName();
                }

                @Override
                public String getOriginalFilename() {
                    return file.getOriginalFilename();
                }

                @Override
                public String getContentType() {
                    return file.getContentType();
                }

                @Override
                public boolean isEmpty() {
                    return false;
                }

                @Override
                public long getSize() {
                    return imageBytes.length;
                }

                @Override
                public byte[] getBytes() {
                    return imageBytes;
                }

                @Override
                public ByteArrayInputStream getInputStream() {
                    return new ByteArrayInputStream(imageBytes);
                }

                @Override
                public void transferTo(java.io.File dest) throws IOException {
                    java.nio.file.Files.write(dest.toPath(), imageBytes);
                }
            };
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
