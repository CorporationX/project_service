package faang.school.projectservice.controller.project;

import faang.school.projectservice.controller.ProjectController;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
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
    ArgumentCaptor<ProjectDto> projectCaptor;

    @Captor
    ArgumentCaptor<ProjectFilterDto> filterDtoCaptor;

    private ProjectDto projectDto;
    private long userId = 1L;
    private long projectId = 22L;
    private ProjectFilterDto filterDto;

    @BeforeEach
    public void setUp() {
        projectDto = ProjectDto.builder().build();
        filterDto = ProjectFilterDto.builder().build();
    }

    @Test
    public void testValidation_HaveNoName_InCreate() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> controller.create(userId, projectDto));

        assertEquals("Every project should have a name and a description", exception.getMessage());
    }

    @Test
    public void testValidation_EmptyDescription_InCreate() {
        projectDto = ProjectDto.builder()
                .name("Bakery")
                .description(" ")
                .build();
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
        when(projectService.create(any(ProjectDto.class))).thenReturn(projectDto);

        ProjectDto dto = controller.create(userId, projectDto);

        assertEquals(projectDto, dto);
    }

    @Test
    public void testValidation_FillsOwnerAndVisibility_InCreate() {
        projectDto.setName("Bakery");
        projectDto.setDescription("bla-bla");

        controller.create(userId, projectDto);
        verify(projectService, times(1)).create(projectCaptor.capture());
        ProjectDto dto = projectCaptor.getValue();

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
                controller.update(projectDto));

        assertEquals("Project for updating should be found by ID. Fill in this field.",
                exception.getMessage());
    }

    @Test
    public void testUpdate_DtoWithoutOwnerId() {
        projectDto.setId(projectId);
        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                controller.update(projectDto));

        assertEquals("Project for updating should be found by ID. Fill in this field.",
                exception.getMessage());
    }

    @Test
    public void testUpdate_RightDto() {
        projectDto.setId(projectId);
        projectDto.setOwnerId(userId);
        when(projectService.update(projectDto)).thenReturn(projectDto);

        ProjectDto updated = controller.update(projectDto);

        assertEquals(projectDto, updated);
    }

    @Test
    void testGetFilteredProjects_EmptyDto() {
        when(projectService.getFilteredProjects(userId, filterDto)).thenReturn(List.of(projectDto));

        List<ProjectDto> filteredProjects = controller.getFilteredProjects(userId, filterDto);
        assertEquals(List.of(projectDto), filteredProjects);
    }

    @Test
    void testGetFilteredProjects_CompletedDto() {
        filterDto.setName("Bakery");
        when(projectService.getFilteredProjects(userId, filterDto)).thenReturn(List.of(projectDto));

        List<ProjectDto> filteredProjects = controller.getFilteredProjects(userId, filterDto);

        assertEquals(List.of(projectDto), filteredProjects);
    }

    @Test
    void testGetAllProjects() {
        controller.getAllProjects(userId);

        verify(projectService, times(1))
                .getFilteredProjects(userId, filterDtoCaptor.capture());
        assertEquals(filterDto, filterDtoCaptor.getValue());
    }

    @Test
    void testGetProjectById() {
        projectDto.setId(projectId);
        when(projectService.getProjectById(userId, projectId)).thenReturn(projectDto);

        ProjectDto projectById = controller.getProjectById(userId, projectId);

        assertEquals(projectId, projectById.getId());
    }
}