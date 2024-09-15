package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.project.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProjectControllerTest {

    @InjectMocks
    private ProjectController projectController;

    @Mock
    private ProjectService projectService;

    @Mock
    private ProjectFilterDto filters;

    ProjectDto projectDto = new ProjectDto();
    private Long id;

    @Test
    void create_emptyName_throwsException() {
        projectDto.setName("");
        projectDto.setDescription("description");
        assertThrows(DataValidationException.class,
                () -> projectController.create(projectDto));
    }

    @Test
    void create_emptyDescription_throwsException() {
        projectDto.setName("name");
        projectDto.setDescription("");
        assertThrows(DataValidationException.class,
                () -> projectController.create(projectDto));
    }

    @Test
    void create_validRequest_serviceCreateCalled() {
        projectDto.setName("name");
        projectDto.setDescription("description");
        projectController.create(projectDto);
        verify(projectService, times(1)).create(projectDto);
    }

    @Test
    void update_emptyDescription_throwsException() {
        projectDto.setName("name");
        projectDto.setDescription("");
        assertThrows(DataValidationException.class,
                () -> projectController.update(projectDto));
    }

    @Test
    void update_validRequest_serviceUpdateCalled() {
        projectDto.setName("name");
        projectDto.setDescription("description");
        projectController.update(projectDto);
        verify(projectService, times(1)).update(projectDto);
    }

    @Test
    void getFiltered_validRequest_serviceGetFilteredCalled() {
        projectController.getFiltered(filters);
        verify(projectService, times(1)).getFiltered(filters);
    }

    @Test
    void getAll_validRequest_serviceGetAllCalled() {
        projectController.getAll();
        verify(projectService, times(1)).getAll();
    }

    @Test
    void getById_validRequest_serviceGetByIdCalled() {
        id = 1L;
        projectController.getById(id);
        verify(projectService, times(1)).getById(id);
    }
}