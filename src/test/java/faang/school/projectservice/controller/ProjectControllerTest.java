package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {
    @InjectMocks
    private ProjectController projectController;

    @Test
    public void testControllerThrowsException() {
        ProjectDto emptyProjectDto = ProjectDto.builder().build();
        assertThrows(DataValidationException.class , () ->
                projectController.createProject(null));
        assertThrows(DataValidationException.class , () ->
                projectController.createProject(emptyProjectDto));
    }
}