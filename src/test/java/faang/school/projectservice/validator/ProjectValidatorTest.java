package faang.school.projectservice.validator;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.ValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProjectValidatorTest {

    @Mock
    ProjectRepository projectRepository;

    @InjectMocks
    ProjectValidator projectValidator;

    Long userId = 1L;
    Long projectId = 1L;
    Project project = Project.builder()
            .id(1L)
            .name("Test")
            .status(ProjectStatus.COMPLETED)
            .build();
    ProjectDto projectDto = ProjectDto.builder()
            .name("Test")
            .status(ProjectStatus.COMPLETED)
            .build();
    ProjectFilterDto projectFilterDto = ProjectFilterDto.builder().build();

    @BeforeEach
    void setUp() {

    }

    @Test
    void validateCreationWhenProjectNameAlreadyExistsShouldThrowsException() {
        doThrow(ValidationException.class).when(projectRepository).findAll();
        assertThrows(ValidationException.class, () -> projectValidator.validateCreation(userId, projectDto));
    }

    @Test
    void validateUpdatingWhenProjectIdNotExistInProjectDtoShouldThrowsException() {
        Exception exception = assertThrows(ValidationException.class,
                () -> projectValidator.validateUpdating(projectDto));
        assertEquals("Field id is null", exception.getMessage());
    }

    @Test
    void validateUpdatingWhenProjectIdExistsInProjectDtoAndNotExistInRepoShouldThrowsException() {
        projectDto.setId(1L);
        Exception exception = assertThrows(ValidationException.class,
                () -> projectValidator.validateUpdating(projectDto));
        assertEquals("Project with id " + projectDto.getId() + " does not exist.", exception.getMessage());
    }

    @Test
    void validateUpdatingWhenProjectIdExistsInProjectDtoAndStatusCompletedOrCancelledShouldThrowsException() {
        projectDto.setId(projectId);
        when(projectRepository.findById(projectDto.getId())).thenReturn(Optional.of(project));
        Exception exception = assertThrows(ValidationException.class,
                () -> projectValidator.validateUpdating(projectDto));
        assertEquals("Project with id " + projectDto.getId() + " completed or cancelled.", exception.getMessage());
    }

    @Test
    void validateProjectFilterDtoForFindByIdWhenIdPatternNotExistShouldThrowsException() {
        Exception exception = assertThrows(ValidationException.class,
                () -> projectValidator.validateProjectFilterDtoForFindById(projectFilterDto));
        assertEquals("Field id pattern is null", exception.getMessage());
    }
}