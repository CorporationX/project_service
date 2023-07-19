package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.awaitility.core.DeadlockException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    @InjectMocks
    ProjectController projectController;
    ProjectDto projectDto;

    @Test
    void testCreateControllerThrowsException() {
        projectDto = ProjectDto.builder()
                .build();
        Assertions.assertThrows(DataValidationException.class,()-> projectController.create(projectDto));
    }

    @Test
    void testUpdateControllerThrowsException() {
        projectDto = ProjectDto.builder()
                .name("     ")
                .build();
        Assertions.assertThrows(DataValidationException.class,()-> projectController.update(projectDto,1L));
    }
}