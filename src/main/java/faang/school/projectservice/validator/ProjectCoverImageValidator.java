package faang.school.projectservice.validator;

import faang.school.projectservice.exception.ApiException;
import faang.school.projectservice.validator.util.MultipartImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import static faang.school.projectservice.validator.util.ProjectValidatorErrorMessages.COVER_IMAGE_FILE_SIZE;
import static faang.school.projectservice.validator.util.ProjectValidatorErrorMessages.COVER_IMAGE_FORMAT;
import static faang.school.projectservice.validator.util.ProjectValidatorErrorMessages.COVER_IMAGE_FORMAT_ORIENTATION;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectCoverImageValidator {
    @Value("${app.project_service.cover_image_max_height}")
    private int maxHeight;

    @Value("${app.project_service.cover_image_max_width}")
    private int maxWidth;

    @Value("${app.project_service.max_cover_image_size}")
    private long maxCoverImageSize;

    @Value("#{'${app.project_service.cover_image_types}'.split('/')}")
    private List<String> coverImageTypes;

    public MultipartImage validate(MultipartFile file) {
        System.out.println("TYPE: " + file.getContentType());
        checkSize(file);
        checkType(file);
        BufferedImage image = getImage(file);
        image = checkAndResize(image);
        try {
            return new MultipartImage(image, file.getName(), file.getOriginalFilename(), file.getContentType());
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new ApiException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void checkSize(MultipartFile file) {
        if (file.getSize() > maxCoverImageSize) {
            log.error(String.format(COVER_IMAGE_FILE_SIZE, maxCoverImageSize));
            throw new ApiException(COVER_IMAGE_FILE_SIZE, HttpStatus.BAD_REQUEST, maxCoverImageSize);
        }
    }

    private void checkType(MultipartFile file) {
        String imageType = StringUtils.substringAfter(file.getContentType(), "/");
        if (!coverImageTypes.contains(imageType)) {
            log.error(String.format(COVER_IMAGE_FORMAT, coverImageTypes));
            throw new ApiException(COVER_IMAGE_FORMAT, HttpStatus.BAD_REQUEST, coverImageTypes);
        }
    }

    private BufferedImage getImage(MultipartFile file) {
        try {
            return ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new ApiException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private BufferedImage checkAndResize(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        try {
            if (((width > height) && (width > maxWidth || height > maxHeight)) ||
                    ((width == height) && height > maxHeight)) {
                return Thumbnails.of(image)
                        .size(maxWidth, width == height ? maxWidth : maxHeight)
                        .asBufferedImage();
            } else if (height > width) {
                log.error(COVER_IMAGE_FORMAT_ORIENTATION);
                throw new ApiException(COVER_IMAGE_FORMAT_ORIENTATION, HttpStatus.BAD_REQUEST);
            } else {
                return image;
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new ApiException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
