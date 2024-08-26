package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.subprojectdto.SubProjectDto;
import faang.school.projectservice.dto.subprojectdto.SubProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.filter.subprojectfilter.SubProjectFilter;
import faang.school.projectservice.filter.subprojectfilter.SubProjectNameFilter;
import faang.school.projectservice.filter.subprojectfilter.SubProjectStatusFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.subproject.SubProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.publisher.ProjectViewMessagePublisher;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.s3.S3ServiceImpl;
import faang.school.projectservice.validator.ProjectValidator;
import faang.school.projectservice.validator.SubProjectValidator;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectValidator projectValidator;
    @Mock
    private SubProjectValidator subProjectValidator;
    @Spy
    private ProjectMapper projectMapper;
    @Mock
    private SubProjectMapper subProjectMapper;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private List<ProjectFilter> filters;
    @Mock
    private MomentService momentService;
    @Mock
    private SubProjectNameFilter subProjectNameFilter;
    @Mock
    private SubProjectStatusFilter subProjectStatusFilter;

    private List<SubProjectFilter> subProjectFilters;

    @InjectMocks
    private ProjectService projectService;

    Long userId = 1L;
    Long projectId = 1L;
    Project project;
    ProjectDto projectDto;
    ProjectFilterDto projectFilterDto;
    long childId = 1L;
    long subProjectId = 2L;
    Project childProject;
    Project subProject;
    SubProjectDto subProjectDto;
    SubProjectFilterDto subProjectFilterDto;
    TeamMember teamMember;
    Team team;
    List<Team> teams;
    List<TeamMember> teamMembers;
    long momentId = 1L;
    long subProjectIdOne = 3L;
    MomentDto momentDto;
    S3ServiceImpl s3ServiceImpl;
    ProjectViewMessagePublisher projectViewMessagePublisher;
    UserContext userContext;
    Moment moment;


    @BeforeEach
    void setUp() {
        project = Project.builder()
                .name("test name")
                .description("test description")
                .build();
        projectDto = ProjectDto.builder()
                .name("test name")
                .description("test description")
                .build();
        projectFilterDto = ProjectFilterDto.builder()
                .build();
        subProjectFilterDto = SubProjectFilterDto.builder()
                .build();
        subProjectFilters = new ArrayList<>();
        subProjectFilters.add(subProjectNameFilter);
        subProjectFilters.add(subProjectStatusFilter);
        childProject = Project.builder()
                .id(childId)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        teamMember = TeamMember.builder()
                .userId(userId)
                .build();
        team = Team.builder()
                .teamMembers(teamMembers)
                .build();
        teams = List.of(team, team);
        momentDto = MomentDto.builder()
                .id(momentId)
                .name("Выполнены все подпроекты")
                .projectIds(List.of(subProjectId, subProjectIdOne))
                .userIds(List.of(userId))
                .build();
        subProject = Project.builder()
                .id(subProjectId)
                .name("test name")
                .parentProject(project)
                .children(List.of(childProject))
                .status(ProjectStatus.COMPLETED)
                .visibility(ProjectVisibility.PUBLIC)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        moment = Moment.builder()
                .build();
        subProjectDto = SubProjectDto.builder()
                .id(subProjectId)
                .name("test name")
                .parentProjectId(projectId)
                .status(ProjectStatus.COMPLETED)
                .visibility(ProjectVisibility.PUBLIC)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        projectService = new ProjectService(subProjectValidator,
                subProjectMapper,
                momentService,
                projectValidator,
                projectMapper,
                projectRepository,
                subProjectFilters,
                filters,
                s3ServiceImpl,
                projectViewMessagePublisher,
                userContext);
    }

    @Test
    void createWithProjectNameExistsShouldTrowsException() {
        doThrow(DataValidationException.class).when(projectValidator).validateCreation(userId, projectDto);
        assertThrows(DataValidationException.class, () -> projectService.create(userId, projectDto));
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void createWithValidParametersShouldMapToEntity() {
        when(projectMapper.toEntity(projectDto)).thenReturn(project);
        projectService.create(userId, projectDto);
        verify(projectMapper).toEntity(projectDto);
    }

    @Test
    void createWithValidParametersShouldSetOwnerId() {
        when(projectMapper.toEntity(projectDto)).thenReturn(project);
        projectService.create(userId, projectDto);
        Assertions.assertEquals(1L, project.getOwnerId());
    }

    @Test
    void createWithValidParametersShouldSetStatus() {
        when(projectMapper.toEntity(projectDto)).thenReturn(project);
        projectService.create(userId, projectDto);
        Assertions.assertEquals(ProjectStatus.CREATED, project.getStatus());
    }

    @Test
    void createWithValidParametersAndVisibilityNotExistsShouldSetDefaultVisibility() {
        when(projectMapper.toEntity(projectDto)).thenReturn(project);
        projectService.create(userId, projectDto);
        Assertions.assertEquals(ProjectVisibility.PUBLIC, project.getVisibility());
    }

    @Test
    void createWithValidParametersShouldSaveProject() {
        when(projectMapper.toEntity(projectDto)).thenReturn(project);
        projectService.create(userId, projectDto);
        verify(projectRepository).save(project);
    }

    @Test
    void createWithValidParametersShouldReturnProjectDto() {
        when(projectMapper.toEntity(projectDto)).thenReturn(project);
        projectService.create(userId, projectDto);
        verify(projectMapper).toDto(projectRepository.save(project));
    }

    @Test
    void updateWithNotValidParametersShouldThrowException() {
        doThrow(DataValidationException.class).when(projectValidator).validateUpdating(projectDto);
        Assertions.assertThrows(DataValidationException.class, () -> projectService.update(projectDto));
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void updateWithValidParametersShouldMapToEntity() {
        when(projectMapper.toEntity(projectDto)).thenReturn(project);
        projectService.update(projectDto);
        verify(projectMapper).toEntity(projectDto);
    }

    @Test
    void updateWithValidParametersShouldSaveProject() {
        when(projectMapper.toEntity(projectDto)).thenReturn(project);
        projectService.update(projectDto);
        verify(projectRepository).save(project);
    }

    @Test
    void updateWithValidParametersShouldReturnProjectDto() {
        when(projectMapper.toEntity(projectDto)).thenReturn(project);
        projectService.update(projectDto);
        verify(projectMapper).toDto(projectRepository.save(project));
    }

    @Test
    void findProjectsWithFilterWhenInvokesShouldFindAllAvailable() {
        projectService.findProjectsWithFilter(userId, projectFilterDto);
        verify(projectRepository).findAllAvailableProjectsByUserId(1L);
    }

    @Test
    void findProjectsWithFilterWhenInvokesShouldMapToDtos() {
        projectService.findProjectsWithFilter(userId, projectFilterDto);
        verify(projectMapper).toDtos(anyList());
    }

    @Test
    void findByIdWithNotExistIdPatternShouldThrowException() {
        doThrow(DataValidationException.class).when(projectValidator).validateProjectFilterDtoForFindById(projectFilterDto);
        assertThrows(DataValidationException.class, () -> projectService.findById(userId, projectFilterDto));
    }

    @Test
    void findByIdWithNotExistProjectShouldThrowException() {
        projectFilterDto.setIdPattern(1L);
        Exception exception = assertThrows(DataValidationException.class,
                () -> projectService.findById(userId, projectFilterDto));
        assertEquals("Project not found", exception.getMessage());
    }

    @Test
    void findByIdWithValidParametersShouldReturnProjectDto() {
        projectFilterDto.setIdPattern(1L);
        when(projectRepository.findAvailableByUserIdAndProjectId(userId, 1L))
                .thenReturn(Optional.of(project));
        projectService.findById(userId, projectFilterDto);
        verify(projectMapper).toDto(any(Project.class));
    }

    @Nested
    @DisplayName("createSubProject")
    class createSubProject {

        @Test
        void testValidateParametersShouldThrowException() {
            project.setId(projectId);
            when(projectRepository.getProjectById(projectId)).thenReturn(project);
            doThrow(DataValidationException.class).when(subProjectValidator)
                    .checkAllValidationsForCreateSubProject(subProjectDto, project);
            Assertions.assertThrows(DataValidationException.class,
                    () -> projectService.createSubProject(subProjectDto));
        }

        @Test
        void testConversionToEntity() {
            when(subProjectMapper.toEntity(subProjectDto)).thenReturn(subProject);
            projectService.createSubProject(subProjectDto);
            verify(subProjectMapper).toEntity(subProjectDto);
        }

        @Test
        void testSave() {
            when(subProjectMapper.toEntity(subProjectDto)).thenReturn(subProject);
            projectService.createSubProject(subProjectDto);
            verify(projectRepository).save(subProject);
        }

        @Test
        void testConversionToDto() {
            when(subProjectMapper.toEntity(subProjectDto)).thenReturn(subProject);
            projectService.createSubProject(subProjectDto);
            verify(subProjectMapper).toDto(projectRepository.save(subProject));
        }
    }

    @Test
    void testUpdateProject() {
        when(subProjectMapper.toEntity(subProjectDto)).thenReturn(subProject);
        when(momentService.getMoment(subProject)).thenReturn(moment);
        when(projectRepository.save(subProject)).thenReturn(subProject);
        projectService.updateSubProject(subProjectDto);
    }

    @Nested
    @DisplayName("getAllFilteredSubprojectsOfAProject")
    class getAllFilteredSubprojectsOfAProject {

        @Test
        void testGetProject() {
            when(projectRepository.getProjectById(subProjectId)).thenReturn(subProject);
            projectService.getAllFilteredSubprojectsOfAProject(subProjectFilterDto, subProjectId);
            verify(projectRepository).getProjectById(subProjectId);
        }

        @Test
        void testFilterChildProject() {
            when(projectRepository.getProjectById(subProjectId)).thenReturn(subProject);
            when(subProjectFilters.get(0).isApplicable(subProjectFilterDto)).thenReturn(true);
            when(subProjectFilters.get(1).isApplicable(subProjectFilterDto)).thenReturn(true);
            when(subProjectFilters.get(0).apply(any(), any())).thenReturn(Stream.of(subProject));
            when(subProjectFilters.get(1).apply(any(), any())).thenReturn(Stream.of(subProject));
            when(subProjectMapper.toDto(subProject)).thenReturn(subProjectDto);
            projectService.getAllFilteredSubprojectsOfAProject(subProjectFilterDto, subProjectId);
        }
    }
}