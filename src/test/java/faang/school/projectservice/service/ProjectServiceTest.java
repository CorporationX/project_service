package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.audit.AuditorAwareImpl;
import faang.school.projectservice.config.s3.S3Properties;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.project.ProjectReadDto;
import faang.school.projectservice.dto.project.SubProjectCreateDto;
import faang.school.projectservice.dto.project.SubProjectFilterDto;
import faang.school.projectservice.dto.project.SubProjectUpdateDto;
import faang.school.projectservice.exception.AuthenticationException;
import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.filter.subproject.SubProjectFilter;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.validator.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Spy
    private ProjectMapperImpl projectMapper;

    @Mock
    private MomentRepository momentRepository;

    @Mock
    private ProjectValidator projectValidator;

    @Mock
    private List<SubProjectFilter> subProjectFilter;

    @Mock
    private S3Service S3service;

    @Mock
    private S3Properties s3Properties;

    @Mock
    private ProjectPdfService projectPdfService;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private AuditorAwareImpl auditorAware;

    @InjectMocks
    private ProjectService projectService;

    private Project parentProject;
    private Project publicSubProject1;
    private Project publicSubProject2;
    private Project project;
    private Long userId = 1L;
    private Long projectId = 1L;

    @BeforeEach
    public void beforeEach() {
        parentProject = new Project();
        parentProject.setId(1L);

        publicSubProject1 = new Project();
        publicSubProject1.setId(2L);
        publicSubProject1.setVisibility(ProjectVisibility.PUBLIC);
        publicSubProject1.setName("A Project");
        publicSubProject1.setStatus(ProjectStatus.COMPLETED);

        publicSubProject2 = new Project();
        publicSubProject2.setId(3L);
        publicSubProject2.setVisibility(ProjectVisibility.PUBLIC);
        publicSubProject2.setName("B Project");
        publicSubProject2.setStatus(ProjectStatus.IN_PROGRESS);

        parentProject.setChildren(Arrays.asList(publicSubProject1, publicSubProject2));

        project = Project.builder()
                .id(projectId)
                .ownerId(userId)
                .build();

    }

    @Test
    public void testCreate() {
        SubProjectCreateDto createDto = new SubProjectCreateDto();
        Project subProject = projectMapper.toEntity(createDto);

        projectService.create(createDto);
        verify(projectRepository, Mockito.times(1)).save(subProject);
    }

    @Test
    public void testUpdate() {
        Project project = Project.builder().id(1L).build();
        SubProjectUpdateDto updateDto = new SubProjectUpdateDto();
        updateDto.setId(1L);

        when(projectRepository.findById(updateDto.getId())).thenReturn(Optional.of(project));
        projectMapper.updateEntityFromDto(updateDto, project);

        projectService.update(updateDto);
        verify(projectRepository, Mockito.times(1)).save(project);
    }

    @Test
    public void testGetSubProjects() {
        SubProjectFilterDto filterDto = new SubProjectFilterDto();
        when(projectRepository.findById(1L)).thenReturn(Optional.of(parentProject));

        List<ProjectReadDto> subProjects = projectService.getSubProjects(1L, filterDto);

        assertEquals(0, subProjects.size());
    }

    @Test
    void testGenerateProjectPresentation_NullAuditor() {
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.empty());
        assertThrows(AuthenticationException.class,
                () -> projectService.generateProjectPresentation(userId));
    }

    @Test
    void testGenerateProjectPresentation_UserNotOwner() {
        project.setOwnerId(userId + 1);
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(userId));
        when(projectRepository.findById(userId)).thenReturn(Optional.of(project));
        assertThrows(BusinessException.class,
                () -> projectService.generateProjectPresentation(userId));
    }

    @Test
    void testGenerateProjectPresentation_SuccessCase() {
        String taskName = "task";
        String userName = "user";
        String ownerName = "owner";
        String projectName = "project";
        String description = "description";
        List<Task> tasks = List.of(
                Task.builder().name(taskName).build()
        );
        List<TeamMember> teamMembers = List.of(
                TeamMember.builder()
                        .nickname(userName)
                        .roles(List.of(
                                TeamRole.DEVELOPER,
                                TeamRole.DESIGNER
                        ))
                        .build()
        );
        List<Team> teams = List.of(
                Team.builder().teamMembers(teamMembers).build()
        );
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(userId));
        when(projectRepository.findById(userId)).thenReturn(Optional.of(project));
        when(userServiceClient.getUser(userId))
                .thenReturn(new UserDto(userId, ownerName, ""));
        project.setName(projectName);
        project.setStatus(ProjectStatus.IN_PROGRESS);
        project.setDescription(description);
        project.setTasks(tasks);
        project.setTeams(teams);
        ArgumentCaptor<String> fileKeyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        String fileKey = "project/" + projectId + "/presentation.pdf";

        projectService.generateProjectPresentation(projectId);
        verify(projectPdfService, atLeastOnce()).generateProjectPresentation(any());
        verify(S3service, atLeastOnce()).putFileInStore(fileKeyCaptor.capture(), any());
        assertEquals(
                fileKey,
                fileKeyCaptor.getValue()
        );
        verify(projectRepository, atLeastOnce()).save(projectCaptor.capture());
        assertEquals(
                fileKey,
                projectCaptor.getValue().getPresentationFileKey()
        );
    }

    @Test
    void testGetPresentationFileKey() {
        String fileKey = "project/" + projectId + "/presentation.pdf";
        String endpoint = "http://localhost:9000";
        String bucketName = "project-service";
        String expectedUrl = endpoint + "/" + bucketName + "/" + fileKey;
        project.setPresentationFileKey(fileKey);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(s3Properties.getEndpoint()).thenReturn(endpoint);
        when(s3Properties.getBucketName()).thenReturn(bucketName);
        project.setPresentationFileKey(fileKey);
        assertEquals(
                expectedUrl,
                projectService.getPresentationFileKey(projectId)
        );
    }
}
