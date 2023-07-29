package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SubProjectControllerTest {
    @InjectMocks
    private SubProjectController subProjectController;

    @Test
    void testValidateSubProject() {
        ProjectDto notNullName = new ProjectDto();
        ProjectDto notBlankName = ProjectDto.builder().name("").build();


        assertThrows(DataValidationException.class,
                () -> subProjectController.createSubProject(notNullName), "Enter project name, please");
        assertThrows(DataValidationException.class,
                () -> subProjectController.createSubProject(notBlankName), "Enter project name, please");
    }
}
