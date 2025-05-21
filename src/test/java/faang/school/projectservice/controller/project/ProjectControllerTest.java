package faang.school.projectservice.controller.project;

import faang.school.projectservice.controller.ProjectController;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.project.ProjectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    @Mock
    private ProjectServiceImpl projectService;

    @Spy
    private ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);

    @InjectMocks
    private ProjectController controller;

    @Captor
    ArgumentCaptor<ProjectDto> captor;

    private ProjectDto projectDto;
    private long userId = 1L;

    @BeforeEach
    public void setUp() {
        projectDto = ProjectDto.builder().build();
    }

    @Test
    public void testValidation_HaveNoName_InCreate() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> controller.create(userId, projectDto));

        assertEquals("Every project should have a name and a description", exception.getMessage());
    }

    @Test
    public void testCreate_PreparedDto() {
        projectDto.setName("Bakery");
        projectDto.setDescription("bla-bla");
        projectDto.setOwnerId(userId);
        projectDto.setVisibility(ProjectVisibility.PRIVATE);
        when(projectService.create(eq(userId), any(ProjectDto.class))).thenReturn(projectDto);

        ProjectDto dto = controller.create(userId, projectDto);

        assertEquals(projectDto, dto);
    }

    @Test
    public void testValidation_FillsOwnerAndVisibility_InCreate() {
        projectDto.setName("Bakery");
        projectDto.setDescription("bla-bla");

        controller.create(userId, projectDto);
        verify(projectService, times(1)).create(eq(userId), captor.capture());
        ProjectDto dto = captor.getValue();

        ProjectDto projectDto1 = ProjectDto.builder()
                .name("Bakery")
                .description("bla-bla")
                .ownerId(userId)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        assertEquals(projectDto1, dto);
    }

    @Test
    public void testUpdate_DtoWithoutId() {
        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                controller.update(userId, projectDto));

        assertEquals("Project for updating should be found bu ID. Fill in this field.",
                exception.getMessage());
    }

    @Test
    public void testUpdate_RightDto() {
        projectDto.setId(1L);
        when(projectService.update(userId, projectDto)).thenReturn(projectDto);

        ProjectDto updated = controller.update(userId, projectDto);

        assertEquals(projectDto, updated);
    }

    @Test
    void testGetFilteredProjects_EmptyDto() {
        controller.getFilteredProjects(userId, projectDto);

        verify(projectService, times(1)).getAllProjects(userId);
    }

    @Test
    void testGetFilteredProjects_CompletedDto() {
        projectDto.setName("Bakery");
        when(projectService.getFilteredProjects(userId, projectDto)).thenReturn(List.of(projectDto));

        List<ProjectDto> filteredProjects = controller.getFilteredProjects(userId, projectDto);

        assertEquals(List.of(projectDto), filteredProjects);
    }

    @Test
    void testGetAllProjects() {
        when(projectService.getAllProjects(userId)).thenReturn(List.of(projectDto));

        List<ProjectDto> allProjects = controller.getAllProjects(userId);

        assertEquals(List.of(projectDto), allProjects);
    }

    @Test
    void testGetProjectById() {
        long projectId = 22L;
        projectDto.setId(projectId);
        when(projectService.getProjectById(userId, projectId)).thenReturn(projectDto);

        ProjectDto projectById = controller.getProjectById(userId, projectId);

        assertEquals(projectId, projectById.getId());
    }
}