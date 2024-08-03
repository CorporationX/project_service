package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.FilterSubProjectDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;
import faang.school.projectservice.mapper.project.ProjectMapperImpl;
import faang.school.projectservice.mapper.subproject.SubProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.moment.MomentService;
import faang.school.projectservice.service.project.filter.ProjectFilter;
import faang.school.projectservice.service.project.updater.ProjectUpdater;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.service.subproject.filter.SubProjectFilter;
import faang.school.projectservice.service.subproject.filter.SubProjectNameFilter;
import faang.school.projectservice.service.subproject.filter.SubProjectStatusFilter;
import faang.school.projectservice.validator.project.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceForSubprojectTest {
    @InjectMocks
    private ProjectService projectService;
    @Mock
    private ProjectValidator validator;
    @Spy
    private SubProjectMapperImpl subProjectMapper;
    @Spy
    private ProjectMapperImpl projectMapper;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private MomentService momentService;
    @Mock
    private UserContext userContext;
    @Spy
    private SubProjectNameFilter nameFilter = new SubProjectNameFilter();
    @Spy
    private SubProjectStatusFilter statusFilter = new SubProjectStatusFilter();
    private List<SubProjectFilter> subProjectFilters = List.of(nameFilter, statusFilter);
    private List<ProjectFilter> projectFilters = new ArrayList<>();
    private List<ProjectUpdater> projectUpdaters = new ArrayList<>();
    @Mock
    private S3Service s3Service;

    private Long parentProjectId = 1L;
    private Long ownerId = 100L;
    private String name = "SubProject name";
    private String description = "SubProject description";

    private Project parentProject = Project.builder()
            .id(parentProjectId)
            .build();
    private CreateSubProjectDto subProjectDto = CreateSubProjectDto.builder()
            .parentProjectId(parentProjectId)
            .name(name)
            .description(description)
            .build();
    private ProjectDto projectDto = ProjectDto.builder()
            .parentProjectId(parentProjectId)
            .name(name)
            .description(description)
            .status(ProjectStatus.CREATED)
            .build();

    @BeforeEach
    void setUp() {
        projectService = new ProjectService(
                projectRepository, projectMapper, projectFilters, projectUpdaters,
                userContext, validator, subProjectMapper, momentService, subProjectFilters, s3Service);
        lenient().when(validator.getProjectAfterValidateId(parentProjectId)).thenReturn(parentProject);
        lenient().when(userContext.getUserId()).thenReturn(ownerId);
    }

    @Test
    public void testCreateSubProject() {
        ProjectDto result = projectService.createSubProject(subProjectDto);

        assertEquals(projectDto, result);
        verify(projectRepository).save(any());
    }

    @Test
    public void testUpdateSubProject() {
        UpdateSubProjectDto updateDto = UpdateSubProjectDto.builder()
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        when(validator.getProjectAfterValidateId(parentProjectId)).thenReturn(parentProject);
        when(projectRepository.save(any())).thenReturn(parentProject);
        ProjectDto result = projectService.updateSubProject(parentProjectId, updateDto);

        assertEquals(ProjectStatus.IN_PROGRESS, result.getStatus());
    }

    @Test
    public void testGetFilteredSubProjects() {
        String goodName = "Project of team Minotaur";
        String badName = "Project of team Manticora";
        ProjectStatus goodStatus = ProjectStatus.CREATED;
        ProjectStatus badStatus = ProjectStatus.CANCELLED;

        prepareChildrenProjectsToFilterTest(parentProject, goodName, badName, goodStatus, badStatus);
        when(validator.userHasAccessToProject(any(), any())).thenReturn(true);

        FilterSubProjectDto filterDto = FilterSubProjectDto.builder()
                .namePattern("Minotaur")
                .statusPattern("CREATED")
                .build();

        List<ProjectDto> result = projectService.getFilteredSubProjects(parentProjectId, filterDto);

        assertEquals(1, result.size());
        assertEquals(goodName, result.get(0).getName());
        assertEquals(goodStatus, result.get(0).getStatus());
    }

    private void prepareChildrenProjectsToFilterTest(
            Project parentProject, String goodName, String badName, ProjectStatus goodStatus, ProjectStatus badStatus) {
        Project goodProject = Project.builder()
                .name(goodName)
                .status(goodStatus)
                .build();

        Project goodNameProject = Project.builder()
                .name(goodName)
                .status(badStatus)
                .build();

        Project goodStatusProject = Project.builder()
                .name(badName)
                .status(goodStatus)
                .build();

        Project badProject = Project.builder()
                .name(badName)
                .status(badStatus)
                .build();

        parentProject.setChildren(List.of(goodProject, goodNameProject, goodStatusProject, badProject));
    }
}