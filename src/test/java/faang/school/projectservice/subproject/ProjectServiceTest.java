package faang.school.projectservice.subproject;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.SubProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.project.subproject_filter.SubProjectFilter;
import faang.school.projectservice.service.project.subproject_filter.SubProjectNameFilter;
import faang.school.projectservice.service.project.subproject_filter.SubProjectStatusFilter;
import faang.school.projectservice.service.project.subproject_filter.SubProjectVisibilityFilter;
import faang.school.projectservice.service.project.update_subproject_param.UpdateSubProjectParam;
import faang.school.projectservice.service.project.update_subproject_param.UpdateSubProjectStatus;
import faang.school.projectservice.service.project.update_subproject_param.UpdateSubProjectVisibility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    private ProjectDto dto;
    private ProjectDto parentDto;
    private ProjectService service;
    @Mock
    private ProjectRepository repository;
    @Mock
    private MomentRepository momentRepository;
    private ProjectMapper mapper;
    @Mock
    private List<UpdateSubProjectParam> updateSubProjectParams;
    @Mock
    private List<SubProjectFilter> subProjectFilters;
    @Captor
    ArgumentCaptor<Project> captorCreate;

    @Mock
    private UpdateSubProjectStatus updateSubProjectStatus;
    @Mock
    private UpdateSubProjectVisibility updateSubProjectVisibility;

    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    public void setup() {
        mapper = new ProjectMapperImpl();
        service = new ProjectService(repository, mapper, updateSubProjectParams, subProjectFilters);
        parentDto = new ProjectDto();
        dto = new ProjectDto();
    }

    @Test
    public void testCreateSubProject_parentProjectNull() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> service.createSubProject(dto));
        Assertions.assertEquals("Подпроект должен иметь родительский проект", exception.getMessage());
    }

    @Test
    public void testCreateSubProject_parentProjectIdZero() {
        parentDto.setId(0L);
        dto.setParentProject(parentDto);
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> service.createSubProject(dto));
        Assertions.assertEquals("Подпроект должен иметь родительский проект", exception.getMessage());
    }

    @Test
    public void testCreateSubProject_parentProjectPublicProjectPrivate() {
        parentDto.setId(1L);
        parentDto.setVisibility(ProjectVisibility.PUBLIC);
        dto.setParentProject(parentDto);
        dto.setVisibility(ProjectVisibility.PRIVATE);
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> service.createSubProject(dto));
        Assertions.assertEquals("Нельзя создать приватный подпроект для публичного проекта", exception.getMessage());
    }

    @Test
    public void testCreateSubProject_parentProjectPrivateProjectPublic() {
        parentDto.setId(1L);
        parentDto.setVisibility(ProjectVisibility.PRIVATE);
        dto.setParentProject(parentDto);
        dto.setVisibility(ProjectVisibility.PUBLIC);
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> service.createSubProject(dto));
        Assertions.assertEquals("Нельзя создать публичный подпроект для приватного проекта", exception.getMessage());
    }

    @Test
    public void testCreateSubProject_saveArg() {
        parentDto.setId(1L);
        parentDto.setVisibility(ProjectVisibility.PUBLIC);
        dto.setParentProject(parentDto);
        dto.setVisibility(ProjectVisibility.PUBLIC);

        Project parentProject = new Project();
        parentProject.setId(1L);
        parentProject.setVisibility(ProjectVisibility.PUBLIC);
        Project project = new Project();
        project.setParentProject(parentProject);
        project.setVisibility(ProjectVisibility.PUBLIC);
        project.setStatus(ProjectStatus.CREATED);

        service.createSubProject(dto);
        verify(repository, times(1)).save(captorCreate.capture());
        Assertions.assertEquals(project, captorCreate.getValue());
    }

    @Test
    public void testCreateSubProject_return() {
        parentDto.setId(1L);
        parentDto.setVisibility(ProjectVisibility.PUBLIC);
        dto.setParentProject(parentDto);
        dto.setVisibility(ProjectVisibility.PUBLIC);
        ProjectDto expectedDto = new ProjectDto();
        expectedDto.setParentProject(parentDto);
        expectedDto.setVisibility(ProjectVisibility.PUBLIC);
        expectedDto.setStatus(ProjectStatus.CREATED);

        Project parentProject = new Project();
        parentProject.setId(1L);
        parentProject.setVisibility(ProjectVisibility.PUBLIC);
        Project project = new Project();
        project.setParentProject(parentProject);
        project.setVisibility(ProjectVisibility.PUBLIC);
        project.setStatus(ProjectStatus.CREATED);

        when(repository.save(any())).thenReturn(project);
        ProjectDto returnDto = service.createSubProject(dto);
        Assertions.assertEquals(expectedDto, returnDto);

    }

    @Test
    public void testUpdateSubProject_updateSubProjectDtoNull() {
        UpdateSubProjectDto updateSubProjectDto = new UpdateSubProjectDto();
        ProjectDto dto = new ProjectDto();
        ProjectDto parentProject = new ProjectDto();
        parentProject.setId(2L);
        parentProject.setName("ПарентПрожект1");
        dto.setId(1L);
        dto.setStatus(ProjectStatus.CREATED);
        dto.setName("Проект1");
        dto.setParentProject(parentProject);
        dto.setVisibility(ProjectVisibility.PUBLIC);
        dto.setDescription("Описание1");
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);
        dto.setOwnerId(10L);

        ProjectDto expectedDto = new ProjectDto();
        ProjectDto expectedParentProject = new ProjectDto();
        expectedParentProject.setId(2L);
        expectedParentProject.setName("ПарентПрожект1");
        expectedDto.setId(1L);
        expectedDto.setStatus(ProjectStatus.CREATED);
        expectedDto.setName("Проект1");
        expectedDto.setParentProject(expectedParentProject);
        expectedDto.setVisibility(ProjectVisibility.PUBLIC);
        expectedDto.setDescription("Описание1");
        expectedDto.setCreatedAt(now);
        expectedDto.setUpdatedAt(now);
        expectedDto.setOwnerId(10L);

        ProjectDto returnDto = service.updateSubProject(dto, updateSubProjectDto);
        verify(repository, times(0)).save(captorCreate.capture());
        Assertions.assertEquals(expectedDto, returnDto);
    }

    @Test
    public void testUpdateSubProject_saveArg() {

        UpdateSubProjectDto updateSubProjectDto = new UpdateSubProjectDto();
        updateSubProjectDto.setStatus(ProjectStatus.COMPLETED);
        updateSubProjectDto.setVisibility(ProjectVisibility.PRIVATE);
        dto = new ProjectDto();
        ProjectDto parentProject = new ProjectDto();
        parentProject.setId(2L);
        parentProject.setName("ПарентПрожект1");
        dto.setId(1L);
        dto.setStatus(ProjectStatus.CREATED);
        dto.setName("Проект1");
        dto.setParentProject(parentProject);
        dto.setVisibility(ProjectVisibility.PUBLIC);
        dto.setDescription("Описание1");
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);
        dto.setOwnerId(10L);

        ProjectDto expectedDto = new ProjectDto();
        ProjectDto expectedParentProject = new ProjectDto();
        expectedParentProject.setId(2L);
        expectedParentProject.setName("ПарентПрожект1");
        expectedDto.setId(1L);
        expectedDto.setStatus(ProjectStatus.COMPLETED);
        expectedDto.setName("Проект1");
        expectedDto.setParentProject(expectedParentProject);
        expectedDto.setVisibility(ProjectVisibility.PRIVATE);
        expectedDto.setDescription("Описание1");
        expectedDto.setCreatedAt(now);
        expectedDto.setUpdatedAt(now);
        expectedDto.setOwnerId(10L);
        Project expectedProject = mapper.toEntity(expectedDto);

        List<UpdateSubProjectParam> params = new ArrayList<>(
                List.of(updateSubProjectStatus, updateSubProjectVisibility)
        );
        when(updateSubProjectParams.stream()).thenReturn(params.stream());
        when(updateSubProjectStatus.isExecutable(any())).thenReturn(true);
        when(updateSubProjectVisibility.isExecutable(any())).thenReturn(true);
        Mockito.doAnswer((p) -> {
            dto.setStatus(updateSubProjectDto.getStatus());
            return null;
        }).when(updateSubProjectStatus).execute(any(), any());
        Mockito.doAnswer((p) -> {
            dto.setVisibility(updateSubProjectDto.getVisibility());
            return null;
        }).when(updateSubProjectVisibility).execute(any(), any());

        service.updateSubProject(dto, updateSubProjectDto);
        verify(repository, times(1)).save(captorCreate.capture());
        Assertions.assertEquals(expectedProject, captorCreate.getValue());
    }

    @Test
    public void testUpdateSubProject_return() {

        UpdateSubProjectDto updateSubProjectDto = new UpdateSubProjectDto();
        updateSubProjectDto.setStatus(ProjectStatus.COMPLETED);
        updateSubProjectDto.setVisibility(ProjectVisibility.PRIVATE);
        dto = new ProjectDto();
        ProjectDto parentProject = new ProjectDto();
        parentProject.setId(2L);
        parentProject.setName("ПарентПрожект1");
        dto.setId(1L);
        dto.setStatus(ProjectStatus.CREATED);
        dto.setName("Проект1");
        dto.setParentProject(parentProject);
        dto.setVisibility(ProjectVisibility.PUBLIC);
        dto.setDescription("Описание1");
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());
        dto.setOwnerId(10L);

        ProjectDto expectedDto = new ProjectDto();
        ProjectDto expectedParentProject = new ProjectDto();
        expectedParentProject.setId(2L);
        expectedParentProject.setName("ПарентПрожект1");
        expectedDto.setId(1L);
        expectedDto.setStatus(ProjectStatus.COMPLETED);
        expectedDto.setName("Проект1");
        expectedDto.setParentProject(expectedParentProject);
        expectedDto.setVisibility(ProjectVisibility.PRIVATE);
        expectedDto.setDescription("Описание1");
        expectedDto.setCreatedAt(LocalDateTime.now());
        expectedDto.setUpdatedAt(LocalDateTime.now());
        expectedDto.setOwnerId(10L);
        Project expectedProject = mapper.toEntity(expectedDto);

        List<UpdateSubProjectParam> params = new ArrayList<>(
                List.of(updateSubProjectStatus, updateSubProjectVisibility)
        );
        when(updateSubProjectParams.stream()).thenReturn(params.stream());
        when(updateSubProjectStatus.isExecutable(any())).thenReturn(true);
        when(updateSubProjectVisibility.isExecutable(any())).thenReturn(true);
        doAnswer((p) -> {
            dto.setVisibility(updateSubProjectDto.getVisibility());
            return null;
        }).when(updateSubProjectVisibility).execute(any(), any());
        doAnswer((p) -> {
            dto.setStatus(updateSubProjectDto.getStatus());
            return null;
        }).when(updateSubProjectStatus).execute(any(), any());
        when(repository.save(any())).thenReturn(expectedProject);
        ProjectDto returnDto = service.updateSubProject(dto, updateSubProjectDto);
        Assertions.assertEquals(expectedDto, returnDto);
    }

    @Test
    public void testGetSubProject_NotHaveChildrenProject() {
        dto.setId(1L);
        when(repository.getProjectById(any())).thenReturn(mapper.toEntity(dto));
        List<ProjectDto> dtos = service.getSubProject(dto, null);
        List<ProjectDto> expectedDtos = new ArrayList<>();
        Assertions.assertEquals(expectedDtos, dtos);
    }

    @Test
    public void testGetSubProject_return() {
        dto.setId(1L);
        SubProjectFilterDto subProjectFilter = new SubProjectFilterDto();
        subProjectFilter.setNamePattern("подпроект");
        subProjectFilter.setStatus(ProjectStatus.CREATED);
        SubProjectFilter f1 = new SubProjectNameFilter();
        SubProjectFilter f2 = new SubProjectStatusFilter();
        SubProjectFilter f3 = new SubProjectVisibilityFilter();
        service = new ProjectService(
                repository, mapper, null, new ArrayList<>(List.of(f1, f2, f3))
        );

        Project child1 = new Project();
        child1.setName("проект1");
        child1.setStatus(ProjectStatus.CREATED);
        child1.setVisibility(ProjectVisibility.PUBLIC);
        Project child2 = new Project();
        child2.setName("подпроект1");
        child2.setStatus(ProjectStatus.COMPLETED);
        child2.setVisibility(ProjectVisibility.PUBLIC);
        Project child3 = new Project();
        child3.setName("подпроект2");
        child3.setStatus(ProjectStatus.CREATED);
        child3.setVisibility(ProjectVisibility.PRIVATE);
        Project child4 = new Project();
        child4.setName("подпроект3");
        child4.setStatus(ProjectStatus.CREATED);
        child4.setVisibility(ProjectVisibility.PUBLIC);
        List<Project> children = new ArrayList<>(List.of(child1, child2, child3, child4));
        Project project = new Project();
        project.setChildren(children);

        when(repository.getProjectById(any())).thenReturn(project);
        List<ProjectDto> returnDtos = service.getSubProject(dto, subProjectFilter);
        List<ProjectDto> expectedDtos = new ArrayList<>(List.of(mapper.toDto(child4)));
        Assertions.assertEquals(expectedDtos, returnDtos);
    }
}
