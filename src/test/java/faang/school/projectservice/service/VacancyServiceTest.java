package faang.school.projectservice.service;

import faang.school.projectservice.config.cover.VacancyCoverConfiguration;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.utils.ImageResizer;
import faang.school.projectservice.validation.CoverValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceTest {
    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private S3Service s3Service;
    @Mock
    private CoverValidator coverValidator;
    @Mock
    private ImageResizer<VacancyCoverConfiguration> imageResizer;
    @Mock
    private MultipartFile image;
    @Mock
    private Resource coverResource;
    @Mock
    private VacancyCoverConfiguration config;

    @InjectMocks
    private VacancyService vacancyService;

    private static final String KEY = "key";
    private static final String OLD_KEY = "oldKey";
    private final Vacancy vacancy = new Vacancy();
    private final Long vacancyId = 1L;
    private String folder;

    @BeforeEach
    public void setUp() {
        vacancy.setId(vacancyId);
        folder = String.format("vacancies/%d/cover", vacancyId);
    }

    @DisplayName("Успешная загрузка новой обложки вакансии")
    @Test
    public void uploadCover_WhenValidImage_ThenUploadsAndUpdatesProject() {
        when(coverValidator.isImageOversize(image, config))
                .thenReturn(false);
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));
        when(s3Service.uploadImage(folder, image))
                .thenReturn(KEY);

        vacancyService.uploadCover(vacancyId, image);

        verify(coverValidator, times(1)).validateBasics(image, config);
        verify(coverValidator, times(1)).isImageOversize(image, config);
        verify(vacancyRepository, times(1)).findById(vacancyId);
        verify(s3Service, times(1)).uploadImage(folder, image);
    }

    @DisplayName("Сжатие и загрузка слишком большой обложки")
    @Test
    public void uploadCover_WhenOversizeImage_ThenResizesAndUploads() {
        when(coverValidator.isImageOversize(image, config))
                .thenReturn(true);
        when(imageResizer.resizeImage(image, config)).thenReturn(image);
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));
        when(s3Service.uploadImage(folder, image))
                .thenReturn(KEY);

        vacancyService.uploadCover(vacancyId, image);

        verify(coverValidator, times(1)).validateBasics(image, config);
        verify(coverValidator, times(1)).isImageOversize(image, config);
        verify(imageResizer, times(1)).resizeImage(image, config);
        verify(vacancyRepository, times(1)).findById(vacancyId);
        verify(s3Service, times(1)).uploadImage(folder, image);
    }

    @DisplayName("Удаление старой обложки при загрузке новой")
    @Test
    public void uploadCover_WhenExistingCover_ThenDeletesOldImage() {
        vacancy.setCoverImageKey(OLD_KEY);

        when(coverValidator.isImageOversize(image, config))
                .thenReturn(false);
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));
        when(s3Service.uploadImage(folder, image))
                .thenReturn(KEY);

        vacancyService.uploadCover(vacancyId, image);

        verify(coverValidator, times(1)).validateBasics(image, config);
        verify(coverValidator, times(1)).isImageOversize(image, config);
        verify(vacancyRepository, times(1)).findById(vacancyId);
        verify(s3Service, times(1)).uploadImage(folder, image);
        verify(s3Service, times(1)).deleteImage(OLD_KEY);
    }

    @DisplayName("Ошибка при загрузке: проект не найден")
    @Test
    public void uploadCover_WhenProjectNotFound_ThenThrowsException() {
        when(coverValidator.isImageOversize(image, config))
                .thenReturn(false);
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> vacancyService.uploadCover(vacancyId, image));
        assertEquals("Vacancy not found", exception.getMessage());

        verify(coverValidator, times(1)).validateBasics(image, config);
        verify(coverValidator, times(1)).isImageOversize(image, config);
        verify(vacancyRepository, times(1)).findById(vacancyId);
    }

    @DisplayName("Успешное удаление обложки")
    @Test
    public void deleteCover_WhenKeyExists_DeletesImageAndUpdatesProject() {
        vacancy.setCoverImageKey(OLD_KEY);
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));

        vacancyService.deleteCover(vacancyId);

        verify(vacancyRepository, times(1)).findById(vacancyId);
        verify(s3Service, times(1)).deleteImage(OLD_KEY);
    }

    @DisplayName("Ошибка при удалении: вакансия не найдена")
    @Test
    public void deleteCover_WhenProjectNotFound_ThrowsException() {
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> vacancyService.deleteCover(vacancyId));
        assertEquals("Vacancy not found", exception.getMessage());

        verify(vacancyRepository, times(1)).findById(vacancyId);
    }

    @DisplayName("Ошибка при удалении: обложка не найдена")
    @Test
    public void deleteCover_WhenKeyIsNull_ThrowsException() {
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));

        Exception exception = assertThrows(DataValidationException.class,
                () -> vacancyService.deleteCover(vacancyId));
        assertEquals("Cover image id is null", exception.getMessage());

        verify(vacancyRepository, times(1)).findById(vacancyId);
    }

    @Test
    @DisplayName("Успешное получение обложки")
    public void givenValidData_WhenGetVacancyCover_ThenSuccess() {
        vacancy.setCoverImageKey(KEY);
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));
        when(s3Service.getImage(KEY)).thenReturn(coverResource);

        Resource result = vacancyService.getCover(vacancyId);

        assertEquals(coverResource, result);
        verify(vacancyRepository).findById(vacancyId);
        verify(s3Service).getImage(KEY);
    }
}
