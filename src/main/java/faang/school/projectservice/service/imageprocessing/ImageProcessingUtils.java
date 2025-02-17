package faang.school.projectservice.service.imageprocessing;

import faang.school.projectservice.config.resource.ResourceConfig;
import faang.school.projectservice.config.resource.ResourceConfig.Image;
import faang.school.projectservice.exception.FileManagementException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ImageProcessingUtils {
    private final ResourceConfig resourceConfig;

    public byte[] resizeImage(MultipartFile file) {
        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            Dimension dimension = calculateTargetDimensions(width, height);

            if (dimension.width == originalImage.getWidth()
                    && dimension.height == originalImage.getHeight()) {
                return file.getBytes();
            }
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Thumbnails.of(file.getInputStream())
                    .size(dimension.width, dimension.height)
                    .outputFormat("jpg")
                    .toOutputStream(os);

            return os.toByteArray();
        } catch (IOException e) {
            throw new FileManagementException("Ошибка при обработке изображения");
        }
    }

    private Dimension calculateTargetDimensions(int width, int height) {
        @NotNull
        Image image = resourceConfig.getImage();

        if (width > height) {
            int maxRectangleWidth = image.getMaxRectangleWidth();
            int maxRectangleHeight = image.getMaxRectangleHeight();
            if (width > maxRectangleWidth
                    || height > maxRectangleHeight) {
                width = maxRectangleWidth;
                height = maxRectangleHeight;
            }
            return new Dimension(width, height);
        } else if (width == height) {
            int maxSquareDimension = image.getMaxSquareDimension();
            if (width > maxSquareDimension) {
                width = maxSquareDimension;
                height = maxSquareDimension;
            }
            return new Dimension(width, height);
        } else {
            return new Dimension(width, height);
        }
    }

    public MultipartFile convertByteToMultipartFile(byte[] fileBytes, String fileName, String contentType) {
        try {
            return new CustomMultipartFile(fileBytes, fileName, contentType);
        } catch (Exception e) {
            throw new FileManagementException(
                    String.format("Ошибка при преобразовании массива байтов в MultipartFile. " +
                                    "Имя файла: %s, тип контента: %s. Причина: %s",
                            fileName, contentType, e.getMessage())
            );
        }
    }
}
