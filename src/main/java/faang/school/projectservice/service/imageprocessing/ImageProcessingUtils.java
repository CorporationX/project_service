package faang.school.projectservice.service.imageprocessing;

import faang.school.projectservice.config.amazon.ResourceConfig;
import faang.school.projectservice.exception.FileManagementException;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ImageProcessingUtils {
    private final ResourceConfig resourceConfig;

    public byte[] resizeImage(MultipartFile file) {
        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();
            int targetWidth = originalWidth;
            int targetHeight = originalHeight;

            if (originalWidth > originalHeight) {
                int maxRectangleWidth = resourceConfig.getImage().getMaxRectangleWidth();
                int maxRectangleHeight = resourceConfig.getImage().getMaxRectangleHeight();
                if (originalWidth > maxRectangleWidth
                        || originalHeight > maxRectangleHeight) {
                    targetWidth = maxRectangleWidth;
                    targetHeight = maxRectangleHeight;
                }
            } else if (originalWidth == originalHeight) {
                int maxSquareDimension = resourceConfig.getImage().getMaxSquareDimension();
                if (originalWidth > maxSquareDimension) {
                    targetWidth = maxSquareDimension;
                    targetHeight = maxSquareDimension;
                }
            }
            if (targetWidth == originalWidth && targetHeight == originalHeight) {
                return file.getBytes();
            }
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Thumbnails.of(file.getInputStream())
                    .size(targetWidth, targetHeight)
                    .outputFormat("jpg")
                    .toOutputStream(os);

            return os.toByteArray();
        } catch (IOException e) {
            throw new FileManagementException("Ошибка при обработке изображения");
        }
    }

    public MultipartFile convertByteToMultipartFile(byte[] fileBytes, String fileName, String contentType) {
        ByteArrayResource resource = new ByteArrayResource(fileBytes);
        try {
            return new MockMultipartFile(
                    fileName,
                    fileName,
                    contentType,
                    resource.getInputStream()
            );
        } catch (Exception e) {
            throw new FileManagementException(
                    String.format("Ошибка при преобразовании массива байтов в MultipartFile. " +
                                    "Имя файла: %s, тип контента: %s. Причина: %s",
                            fileName, contentType, e.getMessage())
            );
        }
    }

}
