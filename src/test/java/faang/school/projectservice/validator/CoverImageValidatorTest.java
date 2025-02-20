package faang.school.projectservice.validator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.validator.CoverImageValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CoverImageValidatorTest {

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private CoverImageValidator coverImageValidator;

    @Test
    public void testValidateUploadCover_Success() {
        Long currentUserId = 1L;
        Long vacancyId = 1L;
        Vacancy vacancy = new Vacancy();
        Project project = new Project();
        project.setOwnerId(currentUserId);
        vacancy.setProject(project);

        when(vacancyRepository.existsById(vacancyId)).thenReturn(true);
        when(vacancyRepository.findById(vacancyId)).thenReturn(java.util.Optional.of(vacancy));

        Vacancy result = coverImageValidator.validateUploadCover(currentUserId, vacancyId);

        assertNotNull(result);
        assertEquals(vacancy, result);

        verify(vacancyRepository, times(1)).existsById(vacancyId);
        verify(vacancyRepository, times(1)).findById(vacancyId);
    }

    @Test
    public void testValidateUploadCover_VacancyNotFound() {

        Long currentUserId = 1L;
        Long vacancyId = 1L;

        when(vacancyRepository.existsById(vacancyId)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            coverImageValidator.validateUploadCover(currentUserId, vacancyId);
        });

        assertEquals("Вакансии с id " + vacancyId + " не существует", exception.getMessage());

        verify(vacancyRepository, times(1)).existsById(vacancyId);
        verify(vacancyRepository, never()).findById(vacancyId);
    }

    @Test
    public void testValidateUploadCover_NoPermission() {
        Long currentUserId = 1L;
        Long vacancyId = 1L;
        Vacancy vacancy = new Vacancy();
        Project project = new Project();
        project.setOwnerId(2L);
        vacancy.setProject(project);

        when(vacancyRepository.existsById(vacancyId)).thenReturn(true);
        when(vacancyRepository.findById(vacancyId)).thenReturn(java.util.Optional.of(vacancy));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            coverImageValidator.validateUploadCover(currentUserId, vacancyId);
        });

        assertEquals("У вас нет прав загрузить обложку в данную вакансию", exception.getMessage());

        verify(vacancyRepository, times(1)).existsById(vacancyId);
        verify(vacancyRepository, times(1)).findById(vacancyId);
    }

    @Test
    public void testValidateDeleteCover_Success() {
        Long currentUserId = 1L;
        Long resourceId = 1L;
        Resource resource = new Resource();
        Project project = new Project();
        project.setOwnerId(currentUserId);
        resource.setProject(project);

        when(resourceRepository.existsById(resourceId)).thenReturn(true);
        when(resourceRepository.findById(resourceId)).thenReturn(java.util.Optional.of(resource));

        Resource result = coverImageValidator.validateDeleteCover(currentUserId, resourceId);

        assertNotNull(result);
        assertEquals(resource, result);

        verify(resourceRepository, times(1)).existsById(resourceId);
        verify(resourceRepository, times(1)).findById(resourceId);
    }

    @Test
    public void testValidateDeleteCover_ResourceNotFound() {
        Long currentUserId = 1L;
        Long resourceId = 1L;

        when(resourceRepository.existsById(resourceId)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            coverImageValidator.validateDeleteCover(currentUserId, resourceId);
        });

        assertEquals("Обложка не найдена", exception.getMessage());

        verify(resourceRepository, times(1)).existsById(resourceId);
        verify(resourceRepository, never()).findById(resourceId);
    }

    @Test
    public void testValidateDeleteCover_NoPermission() {
        Long currentUserId = 1L;
        Long resourceId = 1L;
        Resource resource = new Resource();
        Project project = new Project();
        project.setOwnerId(2L);
        resource.setProject(project);

        when(resourceRepository.existsById(resourceId)).thenReturn(true);
        when(resourceRepository.findById(resourceId)).thenReturn(java.util.Optional.of(resource));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            coverImageValidator.validateDeleteCover(currentUserId, resourceId);
        });

        assertEquals("У вас нет прав удалять обложку из данной вакансии", exception.getMessage());

        verify(resourceRepository, times(1)).existsById(resourceId);
        verify(resourceRepository, times(1)).findById(resourceId);
    }

    @Test
    public void testValidateResource_Success() {
        Long resourceId = 1L;

        when(resourceRepository.existsById(resourceId)).thenReturn(true);

        coverImageValidator.validateResource(resourceId);

        verify(resourceRepository, times(1)).existsById(resourceId);
    }

    @Test
    public void testValidateResource_NotFound() {
        Long resourceId = 1L;

        when(resourceRepository.existsById(resourceId)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            coverImageValidator.validateResource(resourceId);
        });

        assertEquals("Обложка не найдена", exception.getMessage());

        verify(resourceRepository, times(1)).existsById(resourceId);
    }
}

