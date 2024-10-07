package faang.school.projectservice.validator;

import faang.school.projectservice.exception.ApiException;
import faang.school.projectservice.service.image.ImageService;
import faang.school.projectservice.validator.util.image.ImageFormat;
import faang.school.projectservice.validator.util.image.MultipartImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.util.List;

import static faang.school.projectservice.validator.util.ProjectValidatorErrorMessages.COVER_IMAGE_FILE_SIZE;
import static faang.school.projectservice.validator.util.ProjectValidatorErrorMessages.COVER_IMAGE_FORMAT;
import static faang.school.projectservice.validator.util.ProjectValidatorErrorMessages.COVER_IMAGE_FORMAT_ORIENTATION;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectCoverImageValidator {
    @Value("${app.project_service.cover_image.cover_image_horizontal_max_width}")
    private int horizontalMaxWidth;

    @Value("${app.project_service.cover_image.cover_image_horizontal_max_height}")
    private int horizontalMaxHeight;

    @Value("${app.project_service.cover_image.cover_image_square_max_width}")
    private int squareMaxWidth;

    @Value("${app.project_service.cover_image.cover_image_square_max_height}")
    private int squareMaxHeight;

    @Value("${app.project_service.cover_image.max_cover_image_size}")
    private long maxCoverImageSize;

    @Value("#{'${app.project_service.cover_image.cover_image_types}'.split('/')}")
    private List<String> coverImageTypes;

    private final ImageService imageService;

    public MultipartImage validate(MultipartFile file) {
        checkSize(file);
        checkType(file);
        BufferedImage image = imageService.getImage(file);
        ImageFormat imageFormat = checkFormat(image);
        image = resizeImage(image, imageFormat);

        return imageService.toMultipartImage(image, file);
    }

    private void checkSize(MultipartFile file) {
        if (file.getSize() > maxCoverImageSize) {
            String logMessage = String.format(COVER_IMAGE_FILE_SIZE, maxCoverImageSize);
            log.error(logMessage);
            throw new ApiException(COVER_IMAGE_FILE_SIZE, HttpStatus.BAD_REQUEST, maxCoverImageSize);
        }
    }

    private void checkType(MultipartFile file) {
        String imageType = StringUtils.substringAfter(file.getContentType(), "/");

        if (!coverImageTypes.contains(imageType)) {
            String logMessage = String.format(COVER_IMAGE_FORMAT, coverImageTypes);
            log.error(logMessage);
            throw new ApiException(COVER_IMAGE_FORMAT, HttpStatus.BAD_REQUEST, coverImageTypes);
        }
    }

    private ImageFormat checkFormat(BufferedImage image) {
        ImageFormat imageFormat = ImageFormat.getFormat(image);

        return switch (imageFormat) {
            case SQUARE, HORIZONTAL -> imageFormat;
            default -> {
                log.error(COVER_IMAGE_FORMAT_ORIENTATION);
                throw new ApiException(COVER_IMAGE_FORMAT_ORIENTATION, HttpStatus.BAD_REQUEST);
            }
        };
    }

    private BufferedImage resizeImage(BufferedImage image, ImageFormat imageFormat) {
        return switch (imageFormat) {
            case SQUARE -> imageService.resizeImage(image, squareMaxWidth, squareMaxHeight);
            case HORIZONTAL -> imageService.resizeImage(image, horizontalMaxWidth, horizontalMaxHeight);
            default -> throw new RuntimeException("Wrong project cover image format for resize");
        };
    }
}
