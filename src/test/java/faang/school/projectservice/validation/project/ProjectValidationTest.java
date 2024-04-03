package faang.school.projectservice.validation.project;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.handler.exceptions.DataValidationException;
import faang.school.projectservice.handler.exceptions.EntityNotFoundException;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ProjectValidationTest {
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @InjectMocks
    private ProjectValidation projectValidation;

    @Test
    void testValidationCreateNameIsNull() {
        ProjectDto projectDto = ProjectDto.builder().name(null).build();

        Assert.assertThrows(DataValidationException.class, () -> projectValidation.validationCreate(projectDto));
    }

    @Test
    void testValidationCreateNameIsBlank() {
        ProjectDto projectDto = ProjectDto.builder().name("").build();

        Assert.assertThrows(DataValidationException.class, () -> projectValidation.validationCreate(projectDto));
    }

    @Test
    void testValidationCreateDescriptionIsNull() {
        ProjectDto projectDto = ProjectDto.builder().name("Бизнес Фича").description(null).build();

        Assert.assertThrows(DataValidationException.class, () -> projectValidation.validationCreate(projectDto));
    }

    @Test
    void testValidationCreateDescriptionIsBlank() {
        ProjectDto projectDto = ProjectDto.builder().name("Бизнес Фича").description(null).build();

        Assert.assertThrows(DataValidationException.class, () -> projectValidation.validationCreate(projectDto));
    }

    @Test
    void testValidationCreateVisibilityIsNull() {
        ProjectDto projectDto = ProjectDto.builder().name("Бизнес Фича").description("Дом.рф").visibility(null).build();

        Assert.assertThrows(DataValidationException.class, () -> projectValidation.validationCreate(projectDto));
    }

    @Test
    void testValidationCreateNameIsNotUnique() {
        ProjectDto projectDto = ProjectDto.builder().name("Бизнес Фича").description("Дом.рф").visibility(ProjectVisibility.PUBLIC).build();

        when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())).thenReturn(true);

        assertThrows(DataValidationException.class, () -> projectValidation.validationCreate(projectDto));

        verify(projectRepository, times(1)).existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName());
    }

    @Test
    void testValidationCreateDontThrowException() {
        ProjectDto projectDto = ProjectDto.builder().name("Бизнес Фича").description("Дом.рф").ownerId(1L).visibility(ProjectVisibility.PUBLIC).build();

        when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())).thenReturn(false);
        when(userServiceClient.getUser(projectDto.getOwnerId())).thenReturn(new UserDto());

        assertDoesNotThrow(() -> projectValidation.validationCreate(projectDto));

        verify(projectRepository, times(1)).existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName());
        verify(userServiceClient).getUser(projectDto.getOwnerId());
    }

    @Test
    void testUpdateValidationProjectNotFound() {
        ProjectDto projectDto = ProjectDto.builder().id(1L).build();

        when(projectRepository.existsById(projectDto.getId())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> projectValidation.validationUpdate(projectDto));

        verify(projectRepository, times(1)).existsById(projectDto.getId());
    }

    @Test
    void testUpdateProjectNameIsNull() {
        ProjectDto projectDto = ProjectDto.builder().id(1L).name(null).build();

        when(projectRepository.existsById(projectDto.getId())).thenReturn(true);

        assertThrows(DataValidationException.class, () -> projectValidation.validationUpdate(projectDto));

        verify(projectRepository, times(1)).existsById(projectDto.getId());
    }

    @Test
    void testUpdateProjectNameIsBlank() {
        ProjectDto projectDto = ProjectDto.builder().id(1L).name("").build();

        when(projectRepository.existsById(projectDto.getId())).thenReturn(true);

        assertThrows(DataValidationException.class, () -> projectValidation.validationUpdate(projectDto));

        verify(projectRepository, times(1)).existsById(projectDto.getId());
    }

    @Test
    void testUpdateProjectDescriptionNull() {
        ProjectDto projectDto = ProjectDto.builder().id(1L).name("Бизнес Фича").description(null).build();

        when(projectRepository.existsById(projectDto.getId())).thenReturn(true);

        assertThrows(DataValidationException.class, () -> projectValidation.validationUpdate(projectDto));

        verify(projectRepository, times(1)).existsById(projectDto.getId());
    }

    @Test
    void testUpdateProjectDescriptionBlank() {
        ProjectDto projectDto = ProjectDto.builder().id(1L).name("Бизнес Фича").description("").build();

        when(projectRepository.existsById(projectDto.getId())).thenReturn(true);

        assertThrows(DataValidationException.class, () -> projectValidation.validationUpdate(projectDto));

        verify(projectRepository, times(1)).existsById(projectDto.getId());
    }

    @Test
    void testUpdateProjectVisibilityIsNull() {
        ProjectDto projectDto = ProjectDto.builder().id(1L).name("Бизнес Фича").description("Дом.рф").visibility(null).build();

        when(projectRepository.existsById(projectDto.getId())).thenReturn(true);

        assertThrows(DataValidationException.class, () -> projectValidation.validationUpdate(projectDto));

        verify(projectRepository, times(1)).existsById(projectDto.getId());
    }

    @Test
    void testUpdateDontThrowException() {
        ProjectDto projectDto = ProjectDto.builder().id(1L).name("Бизнес Фича").description("Дом.рф").visibility(ProjectVisibility.PUBLIC).build();

        when(projectRepository.existsById(projectDto.getId())).thenReturn(true);

        assertDoesNotThrow(() -> projectValidation.validationUpdate(projectDto));

        verify(projectRepository, times(1)).existsById(projectDto.getId());
    }
}
