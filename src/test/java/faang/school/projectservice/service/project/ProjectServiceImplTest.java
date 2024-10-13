package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.model.mapper.subproject.ProjectMapperImpl;
import faang.school.projectservice.model.entity.Project;
import faang.school.projectservice.model.entity.TeamMember;
import faang.school.projectservice.publisher.ProjectViewEventPublisher;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.file.FileCompressor;
import faang.school.projectservice.service.S3Service;
import faang.school.projectservice.service.impl.project.ProjectServiceImpl;
import faang.school.projectservice.validator.project.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;

import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceImplTest {

    @Mock
    private ProjectValidator validator;

    @Spy
    private ProjectMapperImpl projectMapper;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TeamMemberRepository memberRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private FileCompressor fileCompressor;

    @Mock
    private ProjectViewEventPublisher projectViewEventPublisher;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Captor
    private ArgumentCaptor<Project> projectCaptor;

    private final long projectId = 1L;
    private final long userId = 1L;
    private MultipartFile file;
    private String key = "security_key";

    @Value("${image.maxWidth}")
    private int maxWidth;

    @Value("${image.maxLength}")
    private int maxLength;

    @BeforeEach
    void setup() {
        file = new MockMultipartFile("someFile", "original", "jpg", new byte[10000]);
    }

    @Test
    void testGetProjectOk() {
        when(userContext.getUserId()).thenReturn(1L);

        projectService.getProject(1L);

        verify(validator).validateProject(anyLong());
    }

    @Test
    void testUploadCover() {
        Project project = Project.builder()
                .id(projectId)
                .name("Test Project")
                .storageSize(BigInteger.ZERO)
                .maxStorageSize(BigInteger.valueOf(2000000))
                .build();

        when(memberRepository.findByUserIdAndProjectId(anyLong(), anyLong())).thenReturn(TeamMember.builder().build());
        when(projectRepository.getProjectById(anyLong())).thenReturn(project);
        when(fileCompressor.resizeImage(file, maxWidth, maxLength)).thenReturn(file);
        when(s3Service.uploadFile(any(MultipartFile.class), anyString())).thenReturn(key);
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        projectService.uploadCover(projectId, file, userId);

        verify(projectRepository).save(projectCaptor.capture());

        assertEquals(key, projectCaptor.getValue().getCoverImageId());
    }
}
