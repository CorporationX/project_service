package faang.school.projectservice.service;

import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.exception.ExceptionMessage;
import faang.school.projectservice.exception.InvalidFileException;
import faang.school.projectservice.exception.ProjectNotFoundException;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.repository.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    @Mock
    ProjectService projectService;

    @Mock
    S3Service s3Service;

    @Mock
    ResourceRepository resourceRepository;

    @Mock
    ResourceMapper resourceMapper;

    @Mock
    MultipartFile multipartFile;

    @InjectMocks
    ImageService imageService;

    private BufferedImage image;
    private ByteArrayInputStream imageInputStream;
    private int width;
    private int height;
    private long idForSearch;

    @BeforeEach
    public void setUp() throws IOException {
        idForSearch = 1L;
        width = 1200;
        height = 700;
        image = createImage(width, height);
        imageInputStream = new ByteArrayInputStream(toByteArray(image));
    }

    @Test
    @DisplayName("Negative: error when project not found")
    void testSaveProjectCoverNegativeNoProject() {
        var idForSearch = 1L;
        when(projectService.findById(idForSearch)).thenReturn(Optional.empty());

        asserException(() -> imageService.saveProjectCover(idForSearch, multipartFile), ProjectNotFoundException.class,
                ExceptionMessage.PROJECT_NOT_FOUND.formatMessage(idForSearch));
    }

    @Test
    @DisplayName("Negative: error when multipartFile is null")
    void testValidateImageNegativeNoFile() {
        asserException(() -> imageService.saveProjectCover(idForSearch, null), InvalidFileException.class,
                ExceptionMessage.FILE_NOT_SENT.getMessage());
    }

    @Test
    @DisplayName("Negative: error reading file")
    void testValidateImageNegativeReadFile() throws Exception {
        var idForSearch = 1L;
        when(projectService.findById(idForSearch)).thenReturn(Optional.of(createProject(idForSearch)));
        when(multipartFile.getInputStream()).thenThrow(new IOException());

        asserException(() -> imageService.saveProjectCover(idForSearch, multipartFile), InvalidFileException.class,
                ExceptionMessage.IMAGE_PROCESSING_READ.getMessage());
    }

    @Test
    @DisplayName("Negative: error when file is not valid")
    void testValidateImageNegativeFileNotValid() throws IOException {
        try (MockedStatic<ImageIO> mockedStatic = mockStatic(ImageIO.class)) {
            when(projectService.findById(idForSearch)).thenReturn(Optional.of(createProject(idForSearch)));
            when(multipartFile.getInputStream()).thenReturn(imageInputStream);
            mockedStatic.when(() -> ImageIO.read(imageInputStream)).thenReturn(null);

            asserException(() -> imageService.saveProjectCover(idForSearch, multipartFile), InvalidFileException.class,
                    ExceptionMessage.FILE_NOT_VALID.getMessage());
        }
    }

    @Test
    @DisplayName("Negative: error reading file")
    void testResizeImageNegativeUnableReadFile() throws Exception {
        width = 500;
        height = 500;
        image = createImage(width, height);
        imageInputStream = new ByteArrayInputStream(toByteArray(image));

        when(projectService.findById(idForSearch)).thenReturn(Optional.of(createProject(idForSearch)));
        when(multipartFile.getInputStream()).thenReturn(imageInputStream);
        when(multipartFile.getBytes()).thenThrow(new IOException());

        asserException(() -> imageService.saveProjectCover(idForSearch, multipartFile), InvalidFileException.class,
                ExceptionMessage.UNABLE_READ_FILE.getMessage());
    }

    @Test
    @DisplayName("Negative: error when file content type is missing")
    void testResizeImageNegativeNoContentType() throws Exception {
        when(projectService.findById(idForSearch)).thenReturn(Optional.of(createProject(idForSearch)));
        when(multipartFile.getInputStream()).thenReturn(imageInputStream);
        when(multipartFile.getContentType()).thenReturn(null);

        assertThrows(
                NullPointerException.class,
                () -> imageService.saveProjectCover(idForSearch, multipartFile)
        );
    }

    @Test
    @DisplayName("Negative: error writing resized image")
    void testResizeImageNegativeWriteImage() throws Exception {
        try (MockedStatic<ImageIO> mockedStatic = mockStatic(ImageIO.class)) {
            when(projectService.findById(idForSearch)).thenReturn(Optional.of(createProject(idForSearch)));
            when(multipartFile.getInputStream()).thenReturn(imageInputStream);
            mockedStatic.when(() -> ImageIO.read(imageInputStream)).thenReturn(image);
            when(multipartFile.getContentType()).thenReturn("image/jpeg");
            mockedStatic.when(() -> ImageIO.write(
                    any(BufferedImage.class),
                    any(String.class),
                    any(ByteArrayOutputStream.class))).thenThrow(IOException.class);
            asserException(() -> imageService.saveProjectCover(idForSearch, multipartFile), InvalidFileException.class,
                    ExceptionMessage.IMAGE_PROCESSING_WRITE.getMessage());
        }
    }

    @Test
    @DisplayName("Positive: successful creation of a cover for the project")
    void tesSaveProjectSuccess() throws Exception {
        Resource resource = createResource();

        when(projectService.findById(idForSearch)).thenReturn(Optional.of(createProject(idForSearch)));
        when(multipartFile.getInputStream()).thenReturn(imageInputStream);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(s3Service.uploadFromBytes(any(), any(), any(), any())).thenReturn(resource);
        when(resourceRepository.save(resource)).thenReturn(resource);
        when(resourceMapper.toDto(resource)).thenReturn(createResourceDto(resource));

        ResourceDto result = imageService.saveProjectCover(idForSearch, multipartFile);

        assertEquals(idForSearch, resource.getProject().getId());
        assertNotNull(result);
        verify(resourceRepository, times(1)).save(resource);
    }

    private void asserException(Executable executable, Class<? extends Exception> expectedException, String expectedMessage) {
        var exception = assertThrows(expectedException, executable);

        assertEquals(exception.getMessage(), expectedMessage);
    }

    private Project createProject(long id) {
        Project project = new Project();
        project.setId(id);
        return project;
    }

    private BufferedImage createImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    private byte[] toByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        return baos.toByteArray();
    }

    private Resource createResource() {
        return Resource.builder()
                .key("testKey")
                .size(BigInteger.valueOf(512))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(ResourceStatus.ACTIVE)
                .type(ResourceType.getResourceType("jpg"))
                .name("testName")
                .build();
    }

    private ResourceDto createResourceDto(Resource resource) {
        return new ResourceDto(1L, resource.getName(), resource.getKey(), resource.getSize(), resource.getStatus(), 1L);
    }

}
