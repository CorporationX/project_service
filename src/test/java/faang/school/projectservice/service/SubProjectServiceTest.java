package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.IllegalSubProjectsStatusException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.filter.project.ProjectNameFilter;
import faang.school.projectservice.filter.project.ProjectStatusFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubProjectServiceTest {
    @Captor
    private ArgumentCaptor<Project> projectArgumentCaptor;

    @InjectMocks
    private SubProjectService subProjectService;
    @Mock
    private ProjectValidator projectValidator;
    @Mock
    private ProjectMapper projectMapper;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private MomentService momentService;

    private CreateSubProjectDto createSubProjectDto;
    private Project parentProject;
    private ProjectFilterDto projectFilterDto;
    private Project project;
    private Project subProjectFirst;
    private ProjectDto subProjectFirstDto;

    @BeforeEach
    public void setUp() {
        long parentProjectId = 1L;
        long projectId = 2L;
        long teamMemberId = 3L;

        subProjectFirst = Project.builder()
                .name("ProjectName")
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .children(new ArrayList<>()).build();

        Project subProjectSecond = Project.builder()
                .name("not")
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .children(new ArrayList<>()).build();

        List<Project> subProjects = List.of(
                subProjectFirst,
                subProjectSecond
        );

        subProjectFirstDto = ProjectDto.builder()
                .name(subProjectFirst.getName())
                .build();

        TeamMember teamMember = TeamMember.builder()
                .id(teamMemberId)
                .build();

        Team team = Team.builder()
                .teamMembers(List.of(teamMember))
                .build();

        project = Project.builder()
                .id(projectId)
                .build();

        parentProject = Project.builder()
                .id(parentProjectId)
                .teams(List.of(team))
                .children(subProjects)
                .build();

        createSubProjectDto = CreateSubProjectDto.builder()
                .parentProjectId(parentProject.getId())
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        projectFilterDto = ProjectFilterDto.builder()
                .name("ProjectName")
                .projectStatus(ProjectStatus.IN_PROGRESS)
                .build();


        List<ProjectFilter> projectFilters = List.of(
                new ProjectNameFilter(),
                new ProjectStatusFilter()
        );
        subProjectService = new SubProjectService(momentService, projectRepository, projectValidator,
                projectMapper, projectFilters);
    }

    @Test
    @DisplayName("testing createSubProject")
    public void testCreateSubProjectWithProjectRepositoryMethodExecution() {
        when(projectRepository.getProjectById(parentProject.getId())).thenReturn(parentProject);
        when(projectMapper.toEntity(createSubProjectDto)).thenReturn(project);
        subProjectService.createSubProject(createSubProjectDto);
        verify(projectRepository, times(1))
                .getProjectById(createSubProjectDto.getParentProjectId());
        verify(projectValidator, times(1))
                .validateSubProjectVisibility(parentProject.getVisibility(), createSubProjectDto.getVisibility());
        verify(projectRepository, times(1)).save(projectArgumentCaptor.capture());
    }

    @Test
    @DisplayName("testing updateSubProject with non appropriate Status")
    public void testUpdateSubProjectWithNonAppropriateStatus() {
        ProjectDto projectDto = ProjectDto.builder()
                .status(ProjectStatus.COMPLETED).build();
        when(projectRepository.getProjectById(parentProject.getId())).thenReturn(parentProject);
        when(projectRepository.getAllSubProjectsFor(parentProject.getId())).thenReturn(parentProject.getChildren());
        assertThrows(IllegalSubProjectsStatusException.class,
                () -> subProjectService.updateProject(parentProject.getId(), projectDto));
    }

    @Test
    @DisplayName("testing updateSubProject with changing visibility to private and status to completed")
    public void testUpdateSubProjectWithVisibilityPrivateChangeStatusCompleted() {
        ProjectDto projectDto = ProjectDto.builder()
                .visibility(ProjectVisibility.PRIVATE)
                .status(ProjectStatus.COMPLETED).build();
        when(projectRepository.getProjectById(parentProject.getId())).thenReturn(parentProject);
        when(projectRepository.getAllSubProjectsFor(parentProject.getId())).thenReturn(parentProject.getChildren());
        parentProject.setVisibility(ProjectVisibility.PUBLIC);
        parentProject.getChildren().forEach(project -> {
            project.setVisibility(ProjectVisibility.PUBLIC);
            project.setStatus(ProjectStatus.COMPLETED);
        });

        subProjectService.updateProject(parentProject.getId(), projectDto);
        assertEquals(ProjectVisibility.PRIVATE, parentProject.getVisibility());
        assertEquals(ProjectVisibility.PRIVATE, parentProject.getChildren().get(0).getVisibility());
        assertEquals(ProjectStatus.COMPLETED, parentProject.getStatus());
    }

    @Test
    @DisplayName("testing updateSubProject with visibility to public changing and status to cancelled")
    public void testUpdateSubProjectWithVisibilityPublicStatusCancelled() {
        ProjectDto projectDto = ProjectDto.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .status(ProjectStatus.CANCELLED).build();
        when(projectRepository.getProjectById(parentProject.getId())).thenReturn(parentProject);
        parentProject.setVisibility(ProjectVisibility.PRIVATE);
        parentProject.getChildren().forEach(project -> {
            project.setVisibility(ProjectVisibility.PRIVATE);
            project.setStatus(ProjectStatus.CANCELLED);
        });

        subProjectService.updateProject(parentProject.getId(), projectDto);
        assertEquals(parentProject.getVisibility(), ProjectVisibility.PUBLIC);
        assertEquals(parentProject.getChildren().get(0).getVisibility(), ProjectVisibility.PRIVATE);
        assertEquals(parentProject.getStatus(), ProjectStatus.CANCELLED);
    }

    @Test
    @DisplayName("testing getSubProjects with selection correct subProject")
    public void testGetSubProjects() {
        when(projectRepository.getProjectById(parentProject.getId())).thenReturn(parentProject);
        when(projectMapper.toDto(subProjectFirst)).thenReturn(subProjectFirstDto);
        List<ProjectDto> selectedSubProjects = subProjectService.getSubProjects(parentProject.getId(), projectFilterDto);
        assertEquals(1, selectedSubProjects.size());
    }
}
