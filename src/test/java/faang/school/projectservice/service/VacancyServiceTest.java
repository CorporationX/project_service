package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.FileData;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {

    @Mock
    private VacancyMapper vacancyMapper;

    @Mock
    private S3Service s3Service;

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private VacancyService vacancyService;

    private Vacancy testVacancy;
    private final Long VACANCY_ID = 1L;
    private final Long USER_ID = 100L;
    private final Long OWNER_ID = 200L;

    @BeforeEach
    void setUp() {
        Project project = Project.builder()
                .id(1L)
                .name("project")
                .ownerId(OWNER_ID)
                .build();

        testVacancy = Vacancy.builder()
                .id(VACANCY_ID)
                .createdBy(USER_ID)
                .project(project)
                .build();
    }

    @Test
    void testGetVacancyExistingId() {
        when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(testVacancy));
        when(vacancyMapper.toDto(testVacancy)).thenReturn(new VacancyDto(
                999L,
                "Test Vacancy",
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L,
                1L,
                "test_cover.jpg"
        ));

        VacancyDto result = vacancyService.getVacancy(VACANCY_ID);

        assertNotNull(result);
        verify(vacancyRepository).findById(VACANCY_ID);
    }

    @Test
    void testGetVacancyNonExistingId() {
        when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> vacancyService.getVacancy(VACANCY_ID));
    }

    @Test
    void testSaveCoverImageValidInput() {
        MultipartFile file = mock(MultipartFile.class);
        FileData fileData = new FileData("test-key", 1000);

        when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(testVacancy));
        when(s3Service.uploadFile(any(), anyString())).thenReturn(fileData);
        when(vacancyRepository.save(any())).thenReturn(testVacancy);

        String result = vacancyService.saveCoverImage(VACANCY_ID, file);

        assertEquals("test-key", result);
        verify(s3Service).uploadFile(file, "1project/vacancy/");
        verify(vacancyRepository).save(testVacancy);
    }

    @Test
    void testDeleteCoverImageValidOwner() {
        testVacancy.setCoverImageKey("test-key");
        when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(testVacancy));
        when(userContext.getUserId()).thenReturn(OWNER_ID);

        vacancyService.deleteCoverImageFromVacancy(VACANCY_ID);

        assertNull(testVacancy.getCoverImageKey());
        verify(vacancyRepository).save(testVacancy);
    }

    @Test
    void testDeleteCoverImageValidCreator() {
        testVacancy.setCoverImageKey("test-key");
        when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(testVacancy));
        when(userContext.getUserId()).thenReturn(USER_ID);

        vacancyService.deleteCoverImageFromVacancy(VACANCY_ID);

        assertNull(testVacancy.getCoverImageKey());
        verify(vacancyRepository).save(testVacancy);
    }

    @Test
    void testDeleteCoverImageUnauthorizedUser() {
        testVacancy.setCoverImageKey("test-key");
        when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(testVacancy));
        when(userContext.getUserId()).thenReturn(999L);

        assertThrows(DataValidationException.class,
                () -> vacancyService.deleteCoverImageFromVacancy(VACANCY_ID));
    }

    @Test
    void testCheckCreatorAndOwnerInvalidUser() {
        when(userContext.getUserId()).thenReturn(999L);
        when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(testVacancy));

        assertThrows(DataValidationException.class,
                () -> vacancyService.deleteCoverImageFromVacancy(VACANCY_ID));
    }
}