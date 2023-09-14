package faang.school.projectservice.service;

import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.util.FileStore;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @InjectMocks
    private ResourceService resourceService;

    @Mock
    private TeamMemberService teamMemberService;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private ResourcesValidator resourcesValidator;

    @Mock
    private ProjectService projectService;

    @Mock
    private ResourceMapper resourceMapper;

    @Mock
    private FileStore fileStore;

    private Project project;

    private Project projectNewStorageSize;

    private ResourceDto resourceDto;

    @Mock
    private MultipartFile file;

    private final long userId = 1L;

    private TeamMember teamMember;

    private final Resource resource = new Resource();

    private Resource resourceFill = new Resource();



    @BeforeEach
    void setUp() {
        project = Project
                .builder()
                .id(1L)
                .name("project")
                .storageSize(BigInteger.valueOf(1L))
                .build();
        projectNewStorageSize = Project
                .builder()
                .id(1L)
                .name("project")
                .storageSize(BigInteger.valueOf(175382L))
                .build();

        resourceDto = ResourceDto.builder().projectId(1L).build();

        teamMember = TeamMember.builder().id(1L).roles(new ArrayList<>(List.of(TeamRole.DEVELOPER))).build();

        resourceFill = Resource
                .builder()
                .name("start.jpg")
                .key("1_project/start.jpg")
                .size(BigInteger.valueOf(175381L))
                .allowedRoles(new ArrayList<>(List.of(TeamRole.DEVELOPER)))
                .type(ResourceType.IMAGE)
                .status(ResourceStatus.ACTIVE)
                .createdBy(teamMember)
                .updatedBy(teamMember)
                .project(project)
                .build();

    }

    @Test
    void testUploadFile() {
        Mockito.when(projectService.getProjectEntityById(resourceDto.getProjectId())).thenReturn(project);

        Mockito.when(file.getOriginalFilename()).thenReturn("start.jpg");
        Mockito.when(file.getContentType()).thenReturn("image/jpeg");
        Mockito.when(file.getSize()).thenReturn(175381L);

        Mockito.when(teamMemberService.getTeamMemberByUserIdAndProjectId(userId, 1L)).thenReturn(teamMember);
        resource.setProject(Project.builder().id(1L).build());
        Mockito.when(resourceMapper.toEntity(resourceDto)).thenReturn(resource);

        resourceService.uploadFile(resourceDto, file, userId);

        Mockito.verify(fileStore).uploadFile(file, resource.getKey());
        Mockito.verify(resourceRepository).save(resource);

        Mockito.verify(projectService).saveProject(projectNewStorageSize);
    }

    @Test
    void testUpdateFile() {
        Mockito.when(projectService.getProjectEntityById(resourceDto.getProjectId())).thenReturn(project);

        resource.setId(1L);
        Mockito.when(resourceRepository.findById(1L)).thenReturn(Optional.ofNullable(resource));
        Mockito.when(teamMemberService.getTeamMemberByUserIdAndProjectId(userId, 1L)).thenReturn(teamMember);
        teamMember.setId(2L);
        resource.setUpdatedBy(teamMember);

        resourceService.updateFile(1L, resourceDto, file, userId);

        Mockito.verify(resourceRepository).save(resource);

    }

    @Test
    void testUpdateFileThrowException() {
        assertThrows(EntityNotFoundException.class, () -> resourceService.updateFile(1L, resourceDto, file, userId));
    }

    @Test
    void testDeleteFile() {
        resourceFill.setId(1L);

        Mockito.when(resourceRepository.findById(1L)).thenReturn(Optional.ofNullable(resourceFill));
        Mockito.when(projectService.getProjectEntityById(resourceDto.getProjectId())).thenReturn(project);

        resourceService.deleteResource(1L, userId);

        resourceFill.setKey(null);
        resourceFill.setSize(null);
        resourceFill.setStatus(ResourceStatus.DELETED);

        Mockito.verify(resourceRepository).save(resourceFill);
        Mockito.verify(projectService).saveProject(project);
    }
}