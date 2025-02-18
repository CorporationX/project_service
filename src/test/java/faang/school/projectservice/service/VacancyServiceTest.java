package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.vacancy.VacancyMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.s3.AmazonS3Service;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {
    @InjectMocks
    private VacancyService vacancyService;

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private AmazonS3Service amazonS3Service;

    @Mock
    private UserService userService;

    @Spy
    private VacancyMapperImpl vacancyMapper;

    @Captor
    private ArgumentCaptor<MultipartFile> multipartFileCaptor;

    private static final Long ID = 1L;
    private static final int MAX_IMAGE_SIDE_SIZE = 512;

    private Vacancy vacancy;
    private UserDto userDto;
    private Project project;
    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() throws IOException {
        project = Project.builder()
                .id(ID)
                .ownerId(ID)
                .build();

        vacancy = Vacancy.builder()
                .id(ID)
                .project(project)
                .build();

        userDto = UserDto.builder()
                .id(ID)
                .email("email")
                .username("lehaps")
                .active(true)
                .build();

        String imageName = "test-image.jpg";
        String contentType = "image/jpeg";

        var bufferedImage = new BufferedImage(1024, 500, BufferedImage.TYPE_INT_RGB);
        var outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", outputStream);
        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        mockFile = new MockMultipartFile(imageName, imageName, contentType, inputStream);
    }

    @Test
    void shouldThrowFindById() {
        Mockito.when(vacancyRepository.findById(ID)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> vacancyService.addVacancyCover(ID, Mockito.any()));
        Mockito.verify(vacancyRepository).findById(ID);
    }

    @Test
    void shouldAddVacancyCover() {
        Mockito.when(vacancyRepository.findById(ID)).thenReturn(Optional.of(vacancy));
        Mockito.when(userService.getUser(vacancy.getProject().getOwnerId())).thenReturn(userDto);
        Mockito.doNothing().when(userService).checkUser(userDto.id());
        Mockito.when(amazonS3Service.uploadFile(Mockito.any(MultipartFile.class), Mockito.anyString()))
                .thenReturn("test-key");
        Mockito.when(vacancyRepository.save(vacancy)).thenReturn(vacancy);

        vacancyService.addVacancyCover(vacancy.getId(), mockFile);

        Assertions.assertEquals("test-key", vacancy.getCoverImageKey());
        Mockito.verify(amazonS3Service).uploadFile(Mockito.any(MultipartFile.class), Mockito.anyString());

        Mockito.verify(userService).getUser(vacancy.getProject().getOwnerId());
        Mockito.verify(vacancyRepository).save(vacancy);
        Mockito.verify(vacancyRepository).findById(ID);
    }

    @Test
    void shouldResizeImage() throws IOException {
        Mockito.when(vacancyRepository.findById(ID)).thenReturn(Optional.of(vacancy));
        Mockito.when(userService.getUser(vacancy.getProject().getOwnerId())).thenReturn(userDto);
        Mockito.doNothing().when(userService).checkUser(userDto.id());
        Mockito.when(amazonS3Service.uploadFile(Mockito.any(MultipartFile.class), Mockito.anyString()))
                .thenReturn("test-key");
        Mockito.when(vacancyRepository.save(vacancy)).thenReturn(vacancy);

        vacancyService.addVacancyCover(vacancy.getId(), mockFile);


        Mockito.verify(amazonS3Service).uploadFile(multipartFileCaptor.capture(), Mockito.anyString());

        MultipartFile file = multipartFileCaptor.getValue();

        BufferedImage image = ImageIO.read(file.getInputStream());

        Assertions.assertTrue(image.getWidth() <= MAX_IMAGE_SIDE_SIZE
                || image.getHeight() <= MAX_IMAGE_SIDE_SIZE);

        Mockito.verify(userService).getUser(vacancy.getProject().getOwnerId());
        Mockito.verify(vacancyRepository).save(vacancy);
        Mockito.verify(vacancyRepository).findById(ID);
    }

    @Test
    void shouldDeleteVacancyCover(){
        vacancy.setCoverImageKey("random");

        Mockito.when(vacancyRepository.findById(ID)).thenReturn(Optional.of(vacancy));
        Mockito.when(userService.getUser(vacancy.getProject().getOwnerId())).thenReturn(userDto);
        Mockito.doNothing().when(userService).checkUser(userDto.id());

        Assertions.assertNull(vacancyService.deleteVacancyCover(ID).getCoverImageKey());

        Mockito.verify(userService).getUser(vacancy.getProject().getOwnerId());
        Mockito.verify(vacancyRepository).findById(ID);
    }
}
