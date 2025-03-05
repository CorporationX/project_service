package faang.school.projectservice.service;

import faang.school.projectservice.dto.CoverImageVacancyReadDto.ResourceDto;
import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.s3.S3ServiceCover;
import faang.school.projectservice.service.validator.CoverImageValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static faang.school.projectservice.model.ResourceStatus.ACTIVE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CoverImageServiceTest {

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private S3ServiceCover s3ServiceCover;

    @Mock
    private CoverImageValidator coverImageValidator;

    @InjectMocks
    private CoverImageService coverImageService;
    private MultipartFile mockFile;
    private Vacancy mockVacancy;
    private Resource mockResource;
    private ResourceDto mockResourceDto;

    @BeforeEach
    void setUp() {
        mockFile = new MockMultipartFile("file", "test.jpg", "image/jpeg",
                "test image".getBytes());
        mockVacancy = new Vacancy();
        mockVacancy.setId(1L);
        mockVacancy.setName("Test Vacancy");

        mockResource = new Resource();
        mockResource.setId(1L);
        mockResource.setKey("test-key");

        mockResourceDto = new ResourceDto(1L, ACTIVE, null);
    }

    @Test
    void testDeleteCover_Success() {
        when(coverImageValidator.validateDeleteCover(anyLong(), anyLong())).thenReturn(mockResource);
        Project mockProject = new Project();
        List<Vacancy> vacancies = List.of(mockVacancy);
        mockVacancy.setCoverImageKey("test-key");
        mockProject.setVacancies(vacancies);
        mockResource.setProject(mockProject);

        coverImageService.deleteCover(1L, 1L);

        assertNull(mockVacancy.getCoverImageKey());

        verify(coverImageValidator, times(1)).validateDeleteCover(1L, 1L);
        verify(s3ServiceCover, times(1)).deleteResource(mockResource);
        verify(resourceRepository, times(1)).delete(mockResource);
        verify(vacancyRepository, times(1))
                .save(argThat(v -> v.getCoverImageKey() == null));
    }

    @Test
    void testGetCoverImage_Success() {
        Long currentUserId = 1L;
        Long resourceId = 1L;
        Project mockProject = new Project();
        List<Vacancy> vacancies = List.of(mockVacancy);
        mockVacancy.setCoverImageKey("test-key");
        mockProject.setVacancies(vacancies);
        mockResource.setProject(mockProject);

        when(coverImageValidator.validateDeleteCover(currentUserId, resourceId)).thenReturn(mockResource);

        coverImageService.deleteCover(currentUserId, resourceId);

        assertNull(mockVacancy.getCoverImageKey());

        verify(coverImageValidator, times(1)).validateDeleteCover(currentUserId, resourceId);
        verify(s3ServiceCover, times(1)).deleteResource(mockResource);
        verify(resourceRepository, times(1)).delete(mockResource);
        verify(vacancyRepository, times(1)).save(mockVacancy);
    }

    @Test
    void testGetCoverImage_ResourceNotFound() {
        Long resourceId = 1L;

        when(resourceRepository.findById(resourceId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> coverImageService.getCoverImage(resourceId));

        verify(resourceRepository, times(1)).findById(resourceId);
        verify(s3ServiceCover, never()).getCoverImage(any());
    }

    @Test
    void testCompressImage_ThrowsBusinessException() throws IOException {

        long currentUserId = 1L;
        long vacancyId = 1L;
        MultipartFile invalidFile = new MockMultipartFile("file", "test.jpg",
                "image/jpeg", (InputStream) null);

        when(coverImageValidator.validateUploadCover(currentUserId, vacancyId)).thenReturn(mockVacancy);

        assertThrows(BusinessException.class, () -> coverImageService.uploadCover(currentUserId, vacancyId, invalidFile));
    }
}

