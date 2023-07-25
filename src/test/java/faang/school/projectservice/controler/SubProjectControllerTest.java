package faang.school.projectservice.controler;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SubProjectControllerTest {
    @Mock
    private ProjectService projectService;
    @InjectMocks
    private SubProjectController subProjectController;
    private ProjectDto projectDto;

    @BeforeEach
    void setUp() {
        this.projectDto = ProjectDto.builder()
                .name("Faang")
                .parentProjectId(1L)
                .build();
    }

    @Test
    void createSubProjectThrowExceptionWhenDtoIsNull() {
        DataValidationException exception = assertThrows(DataValidationException.class, () -> subProjectController.createSubProject(null));

        assertEquals("ProjectDto cannot be null", exception.getMessage());
    }

    @Test
    void createSubProjectThrowExceptionWhenNameIsNullOrBlank() {
        ProjectDto projectDto = ProjectDto.builder().build();

        DataValidationException exception = assertThrows(DataValidationException.class, () -> subProjectController.createSubProject(projectDto));

        assertEquals("Project can't be created with empty name", exception.getMessage());
    }

    @Test
    void createSubProjectThrowExceptionWhenParentProjectIdIsNull() {
        ProjectDto projectDto = ProjectDto.builder()
                .name("Faang")
                .build();

        DataValidationException exception = assertThrows(DataValidationException.class, () -> subProjectController.createSubProject(projectDto));

        assertEquals("SubProject must have parentProjectId", exception.getMessage());
    }

    @Test
    void createSubProjectInvokesCreateSubProject() {
        subProjectController.createSubProject(projectDto);

        Mockito.verify(projectService).createSubProject(projectDto);
    }

    @Test
    void createSubProjectsThrowExceptionWhenListAreNull() {
        DataValidationException exception = assertThrows(DataValidationException.class, () -> subProjectController.createSubProjects(null));

        assertEquals("List of projects is null", exception.getMessage());
    }

    @Test
    void createSubProjectsThrowExceptionWhenListIsEmpty() {
        DataValidationException exception = assertThrows(DataValidationException.class, () -> subProjectController.createSubProjects(Collections.emptyList()));

        assertEquals("List of project is empty", exception.getMessage());
    }

    @Test
    void createSubProjectsInvokesCreateSubProjects() {
        subProjectController.createSubProjects(List.of(projectDto));

        Mockito.verify(projectService).createSubProjects(List.of(projectDto));
    }
}