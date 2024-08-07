package faang.school.projectservice.validator;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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
        doThrow(DataValidationException.class).when(projectRepository).findAll();
        assertThrows(DataValidationException.class, () -> projectValidator.validateCreation(userId, projectDto));
    }

    @Test
    void validateUpdatingWhenProjectIdNotExistInProjectDtoShouldThrowsException() {
        Exception exception = assertThrows(DataValidationException.class,
                () -> projectValidator.validateUpdating(projectDto));
        assertEquals("Field id is null", exception.getMessage());
    }

    @Test
    void validateUpdatingWhenProjectIdExistsInProjectDtoAndNotExistInRepoShouldThrowsException() {
        projectDto.setId(1L);
        Exception exception = assertThrows(DataValidationException.class,
                () -> projectValidator.validateUpdating(projectDto));
        assertEquals("Project with id " + projectDto.getId() + " does not exist.", exception.getMessage());
    }

    @Test
    void validateUpdatingWhenProjectIdExistsInProjectDtoAndStatusCompletedOrCancelledShouldThrowsException() {
        projectDto.setId(projectId);
        when(projectRepository.findById(projectDto.getId())).thenReturn(Optional.of(project));
        Exception exception = assertThrows(DataValidationException.class,
                () -> projectValidator.validateUpdating(projectDto));
        assertEquals("Project with id " + projectDto.getId() + " completed or cancelled.", exception.getMessage());
    }

    @Test
    void validateProjectFilterDtoForFindByIdWhenIdPatternNotExistShouldThrowsException() {
        Exception exception = assertThrows(DataValidationException.class,
                () -> projectValidator.validateProjectFilterDtoForFindById(projectFilterDto));
        assertEquals("Field id pattern is null", exception.getMessage());
    }
}