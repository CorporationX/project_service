package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.util.FileStore;
import faang.school.projectservice.validator.ResourcesValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {
    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private ResourcesValidator resourcesValidator;

    @Mock
    private TeamMemberService teamMemberService;

    private TeamMember teamMember;

    @Mock
    private ProjectService projectService;

    @Mock
    private ResourceMapper resourceMapper;

    @Mock
    private FileStore fileStore;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private ResourceService resourceService;

    private Project project;
    private Project newStorageCapacity;

    private Resource resource;
    private ResourceDto resourceDto;

    @BeforeEach
    void setUp() {
        project = Project.builder()
                .id(1L)
                .name("Faang")
                .storageSize(BigInteger.valueOf(2_097_151_000))
                .build();
        newStorageCapacity = Project.builder()
                .id(5L)
                .name("New Faang")
                .storageSize(BigInteger.valueOf(2_097_152_000))
                .build();

        resourceDto = ResourceDto.builder()
                .projectId(1L)
                .build();

        teamMember = TeamMember.builder()
                .id(1L)
                .roles(new ArrayList<>(List.of(TeamRole.DEVELOPER)))
                .build();

        resource = Resource.builder()
                .name("TestName")
                .key("TestKey")
                .size(BigInteger.valueOf(2_097_151_000))
                .allowedRoles(new ArrayList<>(List.of(TeamRole.DEVELOPER)))
                .type(ResourceType.IMAGE)
                .status(ResourceStatus.ACTIVE)
                .createdBy(teamMember)
                .updatedBy(teamMember)
                .project(project)
                .build();
    }

    @Test
    void uploadFileTest() {
        when(teamMemberService.findByUserIdAndProjectId(1L, 1L)).thenReturn(teamMember);
        when(projectService.getProjectByIdFromRepo(resourceDto.getProjectId())).thenReturn(project);

        when(multipartFile.getOriginalFilename()).thenReturn("TestName");
        when(multipartFile.getContentType()).thenReturn("image/png");
        when(multipartFile.getSize()).thenReturn(2_097_151_000L);

        resource.setProject(Project.builder().id(1L).build());

        when(resourceMapper.toEntity(resourceDto)).thenReturn(resource);

        resourceService.uploadFile(resourceDto, multipartFile, 1L);

        verify(fileStore).uploadFile(multipartFile, resource.getKey());
        verify(resourceRepository).save(resource);
        verify(projectService).saveProject(project);
    }
}
