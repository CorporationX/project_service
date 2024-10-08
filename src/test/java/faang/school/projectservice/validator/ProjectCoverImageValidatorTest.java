package faang.school.projectservice.validator;

import faang.school.projectservice.exception.ApiException;
import faang.school.projectservice.service.image.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.util.List;

import static faang.school.projectservice.util.project.ProjectFabric.buildMultiPartFile;
import static faang.school.projectservice.validator.util.ProjectValidatorErrorMessages.COVER_IMAGE_FILE_SIZE;
import static faang.school.projectservice.validator.util.ProjectValidatorErrorMessages.COVER_IMAGE_FORMAT;
import static faang.school.projectservice.validator.util.ProjectValidatorErrorMessages.COVER_IMAGE_FORMAT_ORIENTATION;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectCoverImageValidatorTest {
    private static final int HORIZONTAL_MAX_WIDTH = 1080;
    private static final int HORIZONTAL_MAX_HEIGHT = 566;
    private static final int SQUARE_MAX_WIDTH = 1080;
    private static final int SQUARE_MAX_HEIGHT = 566;
    private static final long MAX_COVER_IMAGE_SIZE = 5_000_000;
    private static final List<String> COVER_IMAGE_TYPES = List.of("jpeg", "png");

    private static final long EXCEEDED_LIMIT_SIZE = 5_000_001;
    private static final long NEW_FILE_SIZE = 1;
    private static final String WRONG_CONTENT_TYPE = "image/wrong";
    private static final String IMAGE_JPEG = "image/jpeg";
    private static final String CONTENT_TYPE = IMAGE_JPEG;
    private static final int CONTENT_TYPE_DIGIT = 6;
    private static final int VERTICAL_WIDTH = 566;
    private static final int VERTICAL_HEIGHT = 1080;
    private static final int HORIZONTAL_WIDTH = 1080;
    private static final int HORIZONTAL_HEIGHT = 566;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private ProjectCoverImageValidator projectCoverImageValidator;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(projectCoverImageValidator, "horizontalMaxWidth", HORIZONTAL_MAX_WIDTH);
        ReflectionTestUtils.setField(projectCoverImageValidator, "horizontalMaxHeight", HORIZONTAL_MAX_HEIGHT);
        ReflectionTestUtils.setField(projectCoverImageValidator, "squareMaxWidth", SQUARE_MAX_WIDTH);
        ReflectionTestUtils.setField(projectCoverImageValidator, "squareMaxHeight", SQUARE_MAX_HEIGHT);
        ReflectionTestUtils.setField(projectCoverImageValidator, "maxCoverImageSize", MAX_COVER_IMAGE_SIZE);
        ReflectionTestUtils.setField(projectCoverImageValidator, "coverImageTypes", COVER_IMAGE_TYPES);
    }

    @Test
    @DisplayName("Given un valid size image file when check then throw exception ")
    void testValidateImageSizeMoreThanLimit() {
        MultipartFile multipartFile = buildMultiPartFile(EXCEEDED_LIMIT_SIZE);

        assertThatThrownBy(() -> projectCoverImageValidator.validate(multipartFile))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(COVER_IMAGE_FILE_SIZE, MAX_COVER_IMAGE_SIZE);
    }

    @Test
    @DisplayName("Given un valid type image file when check then throw exception")
    void testValidateWrongImageType() {
        MultipartFile multipartFile = buildMultiPartFile(NEW_FILE_SIZE, WRONG_CONTENT_TYPE);

        assertThatThrownBy(() -> projectCoverImageValidator.validate(multipartFile))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(COVER_IMAGE_FORMAT, COVER_IMAGE_TYPES);
    }

    @Test
    @DisplayName("Given un valid format image file when check then throw exception")
    void testValidateWrongImageFormat() {
        MultipartFile multipartFile = buildMultiPartFile(NEW_FILE_SIZE, CONTENT_TYPE);
        BufferedImage image = new BufferedImage(VERTICAL_WIDTH, VERTICAL_HEIGHT, CONTENT_TYPE_DIGIT);
        when(imageService.getImage(multipartFile)).thenReturn(image);

        assertThatThrownBy(() -> projectCoverImageValidator.validate(multipartFile))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(COVER_IMAGE_FORMAT_ORIENTATION);
    }

    @Test
    @DisplayName("Valid horizontal image successful")
    void testValidateHorizontalImageSuccessReformat() {
        MultipartFile multipartFile = buildMultiPartFile(NEW_FILE_SIZE, CONTENT_TYPE);
        BufferedImage image = new BufferedImage(HORIZONTAL_WIDTH, HORIZONTAL_HEIGHT, CONTENT_TYPE_DIGIT);
        when(imageService.getImage(multipartFile)).thenReturn(image);
        projectCoverImageValidator.validate(multipartFile);

        verify(imageService).resizeImage(image, HORIZONTAL_MAX_WIDTH, HORIZONTAL_HEIGHT);
    }

    @Test
    @DisplayName("Valid square image successful ")
    void testValidateSquareImageSuccessReformat() {
        MultipartFile multipartFile = buildMultiPartFile(NEW_FILE_SIZE, CONTENT_TYPE);
        BufferedImage image = new BufferedImage(SQUARE_MAX_WIDTH, SQUARE_MAX_HEIGHT, CONTENT_TYPE_DIGIT);
        when(imageService.getImage(multipartFile)).thenReturn(image);
        projectCoverImageValidator.validate(multipartFile);

        verify(imageService).resizeImage(image, SQUARE_MAX_WIDTH, SQUARE_MAX_HEIGHT);
    }
}