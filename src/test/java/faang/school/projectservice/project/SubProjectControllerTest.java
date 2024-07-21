package faang.school.projectservice.project;

import faang.school.projectservice.controller.SubProjectController;
import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.service.project.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class SubProjectControllerTest {
    @Mock
    private ProjectService projectService;

    @InjectMocks
    private SubProjectController controller;

    @Test
    public void testCreateProject(){
        ProjectDto dto = ProjectDto.builder().
        name("Test").build();
        projectService.createProject(dto);
        verify(projectService,times(1)).createProject(dto);
    }

    @Test
    public void testValidateProjectName(){
        ProjectDto dto = ProjectDto.builder()
                .name("").build();
        CreateSubProjectDto subProjectDto = CreateSubProjectDto.builder()
                .name("").build();
        assertThrows(DataValidationException.class, () -> controller.createSubProject(1L,subProjectDto));
        assertThrows(DataValidationException.class, () -> controller.createProject(dto));
    }

    @Test
    public void testCreateSubProject(){
        CreateSubProjectDto subProjectDto = CreateSubProjectDto.builder()
                .name("Test").build();
        controller.createSubProject(1L,subProjectDto);
        verify(projectService,times(1)).createSubProject(1L,subProjectDto);
    }
}
