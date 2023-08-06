package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.SubProjectDto;
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
    private SubProjectDto projectDto;

    @BeforeEach
    void setUp() {
        this.projectDto = SubProjectDto.builder()
                .name("Faang")
                .parentProjectId(1L)
                .build();
    }

    @Test
    void createSubProjectThrowExceptionWhenNameIsNullOrBlank() {
        SubProjectDto projectDto = SubProjectDto.builder().build();

        DataValidationException exception = assertThrows(DataValidationException.class, () -> subProjectController.createSubProject(projectDto));

        assertEquals("Project can't be created with empty name", exception.getMessage());
    }

    @Test
    void createSubProjectThrowExceptionWhenParentProjectIdIsNull() {
        SubProjectDto projectDto = SubProjectDto.builder()
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