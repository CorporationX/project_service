package faang.school.projectservice.validator;

import faang.school.projectservice.exception.ApiException;
import faang.school.projectservice.validator.util.MultipartImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static faang.school.projectservice.util.project.ProjectFabric.buildMultiPartFile;
import static faang.school.projectservice.validator.util.ProjectValidatorErrorMessages.COVER_IMAGE_FILE_SIZE;
import static faang.school.projectservice.validator.util.ProjectValidatorErrorMessages.COVER_IMAGE_FORMAT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class ProjectCoverImageValidatorTest {
    private static final long EXCEEDED_LIMIT_SIZE = 5_000_001;
    private static final long NEW_FILE_SIZE = 1;
    private static final String WRONG_CONTENT_TYPE = "image/wrong";
    private static final String IMAGE_JPEG = "image/jpeg";
    private static final String FILE_NAME = "file";

    private static final ClassPathResource VERTICAL_IMAGE_RESOURCE =
            new ClassPathResource("test-project-cover-images/vertical-image.jpg");
    private static final ClassPathResource HORIZONTAL_IMAGE_RESOURCE =
            new ClassPathResource("test-project-cover-images/horizontal-image-2400x1351.jpg");
    private static final ClassPathResource SQUARE_IMAGE_RESOURCE =
            new ClassPathResource("test-project-cover-images/square-image-2000x2000.jpg");

    private static final int MAX_HEIGHT = 566;
    private static final int MAX_WIDTH = 1080;
    private static final long MAX_COVER_IMAGE_SIZE = 5_000_000;
    private static final List<String> COVER_IMAGE_TYPES = List.of("jpeg", "png");

    private final ProjectCoverImageValidator projectCoverImageValidator = new ProjectCoverImageValidator();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(projectCoverImageValidator, "maxHeight", MAX_HEIGHT);
        ReflectionTestUtils.setField(projectCoverImageValidator, "maxWidth", MAX_WIDTH);
        ReflectionTestUtils.setField(projectCoverImageValidator, "maxCoverImageSize", MAX_COVER_IMAGE_SIZE);
        ReflectionTestUtils.setField(projectCoverImageValidator, "coverImageTypes", COVER_IMAGE_TYPES);
    }

    @Test
    @DisplayName("Given ")
    void testValidateImageSizeMoreThanLimit() {
        MultipartFile multipartFile = buildMultiPartFile(EXCEEDED_LIMIT_SIZE);

        assertThatThrownBy(() -> projectCoverImageValidator.validate(multipartFile))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(COVER_IMAGE_FILE_SIZE, MAX_COVER_IMAGE_SIZE);
    }

    @Test
    @DisplayName("Given ")
    void testValidateWrongImageType() {
        MultipartFile multipartFile = buildMultiPartFile(NEW_FILE_SIZE, WRONG_CONTENT_TYPE);

        assertThatThrownBy(() -> projectCoverImageValidator.validate(multipartFile))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(COVER_IMAGE_FORMAT, COVER_IMAGE_TYPES);
    }

    @Test
    @DisplayName("Given ")
    void testValidateWrongImageFormat() throws IOException {
        byte[] imageBytes = Files.readAllBytes(VERTICAL_IMAGE_RESOURCE.getFile().toPath());
        MultipartFile multipartFile =
                new MockMultipartFile(FILE_NAME, VERTICAL_IMAGE_RESOURCE.getFilename(), IMAGE_JPEG, imageBytes);

        assertThatThrownBy(() -> projectCoverImageValidator.validate(multipartFile))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("The project cover image should be horizontal or square");
    }

    @Test
    @DisplayName("Given ")
    void testValidateHorizontalImageSuccessReformat() throws IOException {
        byte[] imageBytes = Files.readAllBytes(HORIZONTAL_IMAGE_RESOURCE.getFile().toPath());
        MultipartFile multipartFile =
                new MockMultipartFile(FILE_NAME, HORIZONTAL_IMAGE_RESOURCE.getFilename(), IMAGE_JPEG, imageBytes);
        MultipartImage result = projectCoverImageValidator.validate(multipartFile);
        BufferedImage resizeImage = result.getImage();

        assertThat(resizeImage.getHeight() <= MAX_HEIGHT).isTrue();
        assertThat(resizeImage.getWidth() <= MAX_WIDTH).isTrue();
    }

    @Test
    @DisplayName("Given ")
    void testValidateSquareImageSuccessReformat() throws IOException {
        byte[] imageBytes = Files.readAllBytes(SQUARE_IMAGE_RESOURCE.getFile().toPath());
        MultipartFile multipartFile =
                new MockMultipartFile(FILE_NAME, SQUARE_IMAGE_RESOURCE.getFilename(), IMAGE_JPEG, imageBytes);
        MultipartImage result = projectCoverImageValidator.validate(multipartFile);
        BufferedImage resizeImage = result.getImage();

        assertThat(resizeImage.getHeight() <= MAX_WIDTH).isTrue();
        assertThat(resizeImage.getWidth() <= MAX_WIDTH).isTrue();
    }
}