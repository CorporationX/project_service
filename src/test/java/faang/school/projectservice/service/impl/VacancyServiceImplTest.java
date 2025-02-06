package faang.school.projectservice.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import faang.school.projectservice.adapter.VacancyRepositoryAdapter;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.file.FileMultipartFile;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.S3Service;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

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
    @Mock
    private ImageProcessorImpl imageProcessor;
    @Mock
    private AmazonS3 s3Client;

    private static final Long VACANCY_ID = 1L;
    private static final Long VACANCY_OWNER = 1L;
    private static final Long VACANCY_PROJECT_TESTER = 5L;
    private static final Long PROJECT_ID = 5L;
    private static final int NUMBER_INVOCATION = 1;
    private static final int WIDTH = 1024;
    private static final int HEIGHT = 512;
    private static final int IMAGE_TYPE = 1;
    private static final int COVER_MAX_SIZE = 512;
    private static final String IMAGE_NAME = "Image";
    private static final String COVER_KEY = "Image";
    private static final String CONTENT_TYPE = "image/jpg";
    private static final String FORMAT_NAME = "jpg";

    private Vacancy vacancy;
    private MultipartFile file;
    private BufferedImage bufferedImage;
    private ByteArrayOutputStream outputStream;
    private Project project;

    @BeforeEach
    void setUp() throws IOException {
        project = Project.builder()
                .id(PROJECT_ID)
                .teams(new ArrayList<>())
                .build();
        vacancy = Vacancy.builder()
                .id(VACANCY_ID)
                .coverImageKey(COVER_KEY)
                .createdBy(VACANCY_OWNER)
                .project(project)
                .build();
        outputStream = new ByteArrayOutputStream();
        bufferedImage = new BufferedImage(WIDTH, HEIGHT, IMAGE_TYPE);
        ImageIO.write(bufferedImage, FORMAT_NAME, outputStream);
        bufferedImage.flush();
        file = new FileMultipartFile(IMAGE_NAME,
                IMAGE_NAME,
                CONTENT_TYPE,
                outputStream.toByteArray(),
                outputStream.toByteArray().length);
        imageProcessor.setCoverMaxSize(COVER_MAX_SIZE);
    }

    @Test
    public void testAddCover() throws IOException {
        String folder = String.format("%s/%d", "vacancy", VACANCY_ID);
        when(vacancyRepositoryAdapter.findById(VACANCY_ID)).thenReturn(vacancy);
        when(imageProcessor.resizeImage(file)).thenReturn(bufferedImage);
        when(imageProcessor.convertImageToMultipartFile(bufferedImage, file.getName(), file.getOriginalFilename(),
                file.getContentType())).thenReturn(file);
        vacancyService.addCover(VACANCY_ID, file);
        Mockito.verify(vacancyRepositoryAdapter, times(NUMBER_INVOCATION)).save(vacancy);
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
        when(vacancyRepositoryAdapter.findById(VACANCY_ID)).thenReturn(vacancy);
        vacancyService.getVacancyCover(VACANCY_ID);
        Mockito.verify(s3Service, times(NUMBER_INVOCATION)).downloadFile(vacancy.getCoverImageKey());
    }

    @Test
    public void testDeleteVacancyCover() {
        when(vacancyRepositoryAdapter.findById(VACANCY_ID)).thenReturn(vacancy);
        vacancyService.deleteVacancyCover(VACANCY_ID, VACANCY_OWNER);
        Mockito.verify(vacancyRepositoryAdapter, times(NUMBER_INVOCATION)).save(vacancy);
    }

    @Test
    public void testDeleteVacancyCoverFailed() {
        when(vacancyRepositoryAdapter.findById(VACANCY_ID)).thenReturn(vacancy);
        Assert.assertThrows(
                DataValidationException.class,
                () -> vacancyService.deleteVacancyCover(VACANCY_ID, VACANCY_PROJECT_TESTER));
    }
}
