package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.SubProjectFilterDto;
import faang.school.projectservice.filter.SubProjectFilter;
import faang.school.projectservice.filter.SubProjectNameFilter;
import faang.school.projectservice.filter.SubProjectStatusFilter;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.SubProjectValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

import static faang.school.projectservice.model.ProjectStatus.COMPLETED;
import static faang.school.projectservice.model.ProjectStatus.CREATED;
import static faang.school.projectservice.model.ProjectStatus.IN_PROGRESS;
import static faang.school.projectservice.model.ProjectVisibility.PRIVATE;
import static faang.school.projectservice.model.ProjectVisibility.PUBLIC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;

    private ProjectRepository projectRepository;
    private ProjectMapperImpl projectMapper;
    private MomentService momentService;
    private List<SubProjectFilter> filters;
    private SubProjectValidator validator;


    @Captor
    private ArgumentCaptor<Project> captor;

    private Long parentId;
    private Long projectId;
    private Project parent;

    @BeforeEach
    void setUp() {
        projectRepository = Mockito.mock(ProjectRepository.class);
        projectMapper = Mockito.spy(ProjectMapperImpl.class);
        momentService = Mockito.mock(MomentService.class);
        validator = Mockito.mock(SubProjectValidator.class);

        SubProjectNameFilter subProjectNameFilter = Mockito.mock(SubProjectNameFilter.class);
        SubProjectStatusFilter subProjectStatusFilter = Mockito.mock(SubProjectStatusFilter.class);
        filters = List.of(subProjectNameFilter, subProjectStatusFilter);

        projectService = new ProjectService(projectRepository, projectMapper, momentService, filters, validator);

        parentId = 1L;
        projectId = 2L;

        parent = Project.builder()
                .id(1L)
                .visibility(PRIVATE)
                .children(new ArrayList<>())
                .build();
        parent.getChildren().add(Project.builder().status(COMPLETED).build());
    }

    @Test
    void testCreateSubProject() {
        Project parent = Project.builder().id(1L).visibility(PUBLIC).build();
        CreateSubProjectDto subProjectDto = CreateSubProjectDto.builder().name("name").visibility(PUBLIC).build();
        Project projectToCreate = projectMapper.toModel(subProjectDto);
        projectToCreate.setParentProject(parent);
        projectToCreate.setStatus(CREATED);

        when(projectRepository.getProjectById(parentId)).thenReturn(parent);
        List<Project> children = List.of(projectToCreate);

        projectService.createSubProject(parentId, subProjectDto);
        assertEquals(children, parent.getChildren());
        verify(projectRepository, times(1)).save(captor.capture());
        assertEquals(projectToCreate, captor.getValue());
    }

    @Test
    void testCreateSubProjectWhenParentNotExist() {
        when(projectRepository.getProjectById(parentId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                () -> projectService.createSubProject(parentId, new CreateSubProjectDto()));
    }


    @Test
    void testUpdateSubProject() {
        Project projectToUpdate = Project.builder()
                .visibility(PRIVATE)
                .status(IN_PROGRESS)
                .parentProject(parent).build();
        ProjectDto projectDto = ProjectDto.builder()
                .name("name")
                .description("desc")
                .visibility(PRIVATE)
                .status(COMPLETED).build();

        parent.getChildren().add(projectToUpdate);

        when(projectRepository.getProjectById(projectId)).thenReturn(projectToUpdate);
        when(projectRepository.getProjectById(parentId)).thenReturn(parent);
        when(validator.isAllSubProjectsCompleted(parent)).thenReturn(true);

        projectService.updateSubProject(projectId, projectDto);
        MomentDto momentDto = MomentDto.builder()
                .title("Проект со всеми подзадачами выполенен")
                .projectId(projectId)
                .build();
        verify(momentService, times(1)).createMoment(momentDto);
        verify(projectMapper, times(1)).toDto(projectToUpdate);
    }

    @Test
    void testUpdateSubProjectWhenProjectNotExist() {
        when(projectRepository.getProjectById(projectId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                () -> projectService.updateSubProject(projectId, new ProjectDto()));
    }

    @Test
    void testGetSubProjectsWithPrivateVisibility() {
        Project project = Project.builder().visibility(PRIVATE).build();
        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        assertThrows(IllegalArgumentException.class,
                () -> projectService.getSubProjects(projectId, new SubProjectFilterDto()));
    }

    @Test
    void testGetSubProjects() {
        Project project1 = Project.builder().visibility(PUBLIC).build();
        Project project2 = Project.builder().visibility(PUBLIC).build();
        Project project3 = Project.builder().visibility(PRIVATE).build();
        List<Project> projects = new ArrayList<>();
        projects.add(project1);
        projects.add(project2);
        projects.add(project3);
        Project project = Project.builder().visibility(PUBLIC).children(projects).build();

        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        List<ProjectDto> actual = projectService.getSubProjects(projectId, new SubProjectFilterDto());

        assertEquals(2, actual.size());
    }
}
