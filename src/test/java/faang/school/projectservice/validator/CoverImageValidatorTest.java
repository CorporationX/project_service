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

import java.util.Optional;

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

        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));

        Vacancy result = coverImageValidator.validateUploadCover(currentUserId, vacancyId);

        assertNotNull(result);
        assertEquals(vacancy, result);

        verify(vacancyRepository, times(1)).findById(vacancyId);
    }

    @Test
    public void testValidateUploadCover_VacancyNotFound() {
        Long currentUserId = 1L;
        Long vacancyId = 1L;

        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            coverImageValidator.validateUploadCover(currentUserId, vacancyId);
        });

        assertEquals("Вакансии с id " + vacancyId + " не существует", exception.getMessage());

        verify(vacancyRepository, times(1)).findById(vacancyId);
    }

    @Test
    public void testValidateUploadCover_NoPermission() {
        Long currentUserId = 1L;
        Long vacancyId = 1L;
        Vacancy vacancy = new Vacancy();
        Project project = new Project();
        project.setOwnerId(2L);
        vacancy.setProject(project);

        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            coverImageValidator.validateUploadCover(currentUserId, vacancyId);
        });

        assertEquals("У вас нет прав загрузить обложку в данную вакансию", exception.getMessage());

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

        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));

        Resource result = coverImageValidator.validateDeleteCover(currentUserId, resourceId);

        assertNotNull(result);
        assertEquals(resource, result);

        verify(resourceRepository, times(1)).findById(resourceId);
    }

    @Test
    public void testValidateDeleteCover_ResourceNotFound() {
        Long currentUserId = 1L;
        Long resourceId = 1L;

        when(resourceRepository.findById(resourceId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            coverImageValidator.validateDeleteCover(currentUserId, resourceId);
        });

        assertEquals("Обложка c Id" + resourceId + " не найдена", exception.getMessage());

        verify(resourceRepository, times(1)).findById(resourceId);
    }

    @Test
    public void testValidateDeleteCover_NoPermission() {
        Long currentUserId = 1L;
        Long resourceId = 1L;
        Resource resource = new Resource();
        Project project = new Project();
        project.setOwnerId(2L);
        resource.setProject(project);

        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            coverImageValidator.validateDeleteCover(currentUserId, resourceId);
        });

        assertEquals("У вас нет прав удалять обложку с id " + resourceId + " из данной вакансии", exception.getMessage());

        verify(resourceRepository, times(1)).findById(resourceId);
    }

}

