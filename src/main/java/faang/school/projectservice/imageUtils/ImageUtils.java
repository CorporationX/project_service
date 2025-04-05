package faang.school.projectservice.imageUtils;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtils {
    public static MultipartFile compressImage(MultipartFile file) {
        try {
            BufferedImage compressedImage = Thumbnails.of(file.getInputStream())
                    .size(512, 512)
                    .asBufferedImage();

            // Преобразуем BufferedImage в byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            javax.imageio.ImageIO.write(compressedImage, "png", baos);
            baos.flush();
            byte[] imageBytes = baos.toByteArray();
            baos.close();

            // Возвращаем MultipartFile
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
                    return "image/png";  // Установим MIME тип для PNG
                }

                @Override
                public boolean isEmpty() {
                    return imageBytes.length == 0;
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
