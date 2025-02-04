package faang.school.projectservice.service;

import faang.school.projectservice.adapter.VacancyRepositoryAdapter;
import faang.school.projectservice.file.FileMultipartFile;
import faang.school.projectservice.model.ImageType;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.s3.S3Service;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.shaded.org.yaml.snakeyaml.events.Event;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceImplTest {

    @InjectMocks
    private VacancyServiceImpl vacancyService;

    @Mock
    private VacancyRepositoryAdapter vacancyRepositoryAdapter;
    @Mock
    private S3Service s3Service;
    @Captor
    private ArgumentCaptor<MultipartFile> fileCaptor;
//    @Mock
//    private MultipartFile multipartFile;

    private static final Long VACANCY_ID = 1L;
    private static final int NUMBER_INVOCATION = 1;
    private static final int WIDTH = 1024;
    private static final int HEIGHT = 512;
    private static final int IMAGE_TYPE = 1;

    private Vacancy vacancy;
    private MultipartFile file;
    private BufferedImage bufferedImage;
    @BeforeEach
    void setUp() throws IOException {
        vacancy = Vacancy.builder()
                .id(VACANCY_ID)
                .build();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bufferedImage = new BufferedImage(WIDTH,HEIGHT, IMAGE_TYPE);
        ImageIO.write(bufferedImage, "jpg", outputStream);
        bufferedImage.flush();
        file = new FileMultipartFile("Image",
                "Image",
                "image/png",
                outputStream.toByteArray(),
                outputStream.toByteArray().length);
    }

    @Test
    public void testAddCover() throws IOException {
        String folder = String.format("%s/%d", "vacancy", VACANCY_ID);
        when(vacancyRepositoryAdapter.findById(VACANCY_ID)).thenReturn(vacancy);
        vacancyService.addCover(VACANCY_ID, file);
        Mockito.verify(s3Service, times(NUMBER_INVOCATION)).uploadFile(fileCaptor.capture(),eq(folder));
        Mockito.verify(vacancyRepositoryAdapter,times(NUMBER_INVOCATION)).save(vacancy);
        BufferedImage bufferedImage = ImageIO.read(fileCaptor.getValue().getInputStream());
        assertEquals(512, bufferedImage.getWidth());
        assertEquals(256, bufferedImage.getHeight());
    }

    @Test
    public void testAddCoverFailed() throws IOException {
        when(vacancyRepositoryAdapter.findById(VACANCY_ID)).thenReturn(vacancy);
        Assert.assertThrows(
                NullPointerException.class,
                () -> vacancyService.addCover(VACANCY_ID, null));
    }

    @Test
    public void testGetVacancyCover() {

    }

    @Test
    public void testDeleteVacancyCover() {

    }
}
