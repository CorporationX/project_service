package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.s3.S3ServiceImpl;
import faang.school.projectservice.util.CoverHandler;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProjectServiceTest {
    @InjectMocks
    private ProjectService projectService;
    @Mock
    private ProjectRepository projectRepository;

    @Spy
    private ProjectMapperImpl projectMapper;

    @Mock
    private ProjectValidator projectValidator;
    @Mock
    private S3ServiceImpl s3service;
    @Mock
    private CoverHandler coverHandler;
    @Mock
    private Project projectMock;
    @Mock
    private MultipartFile multipartFileMock;
    private ProjectDto projectDto;
    private Project project;
    private String key;
    private String folder;
    private Long projectId;

    @BeforeEach
    void setUp() {
        key = "key";
        folder = "projectId_1_projectName_Project1";
        projectId = 1L;
        projectDto = ProjectDto.builder()
                .name("Project1")
                .description("Description1")
                .ownerId(1L)
                .visibility("PUBLIC")
                .build();

        project = Project.builder()
                .id(1L)
                .name("Project1")
                .description("Description1")
                .ownerId(1L)
                .visibility(ProjectVisibility.valueOf("PUBLIC"))
                .build();

        ProjectFilter projectFilter = mock(ProjectFilter.class);
        List<ProjectFilter> projectFilters = List.of(projectFilter);
    }

    @Test
    void testCreateNewProject() {
        projectService.createProject(projectDto);
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void testGetProjectById() {
        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        projectService.findProjectById(1L);
        verify(projectRepository).getProjectById(projectId);
    }

    @Test
    void testGetAllProject() {
        projectService.getAllProject();
        verify(projectRepository).findAll();
    }

    @Test
    void testAddCoverProject() {
        when(projectRepository.getProjectById(1L)).thenReturn(projectMock);
        when(s3service.uploadFile(multipartFileMock, folder)).thenReturn(key);
        when(projectRepository.save(projectMock)).thenReturn(projectMock);
        when(projectMock.getId()).thenReturn(projectId);
        when(projectMock.getName()).thenReturn("Project1");

        projectService.addCoverProject(projectId, multipartFileMock);

        verify(projectRepository).getProjectById(projectId);
        verify(s3service).uploadFile(multipartFileMock, folder);
        verify(projectRepository).save(projectMock);
    }

    @Test
    void testGetProjectCover() {
        InputStream inputStream = mock(InputStream.class);
        when(s3service.downloadFile(key)).thenReturn(inputStream);
        when(projectRepository.getProjectById(projectId)).thenReturn(projectMock);
        when(projectMock.getCoverImageId()).thenReturn(key);
        projectService.getProjectCover(projectId);
        verify(s3service).downloadFile(key);
    }

    @Test
    void testDeleteCover() {
        when(projectRepository.getProjectById(projectId)).thenReturn(projectMock);
        when(projectMock.getCoverImageId()).thenReturn(key);
        when(projectRepository.save(projectMock)).thenReturn(projectMock);
        doNothing().when(s3service).deleteFile(key);

        projectService.deleteCover(projectId);

        verify(projectRepository).getProjectById(projectId);
        verify(projectMock).getCoverImageId();
        verify(projectRepository).save(projectMock);
        verify(s3service).deleteFile(key);
    }
}