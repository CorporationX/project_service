package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.resource.ResourceReadDto;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ResourceMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.imageprocessing.ImageProcessingUtils;
import faang.school.projectservice.service.s3.AmazonS3Service;
import faang.school.projectservice.validator.project.ProjectValidator;
import faang.school.projectservice.validator.project.ResourceValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ResourceValidator resourceValidator;

    @Mock
    private ProjectValidator projectValidator;

    @Mock
    private AmazonS3Service amazonS3Client;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Spy
    private ResourceMapperImpl resourceMapper;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private ImageProcessingUtils imageProcessingUtils;

    @Mock
    private UserContext userContext;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private ProjectService projectService;

    private final String KEY = "key";
    private final Long USER_ID = 1L;
    private final Long RESOURCE_ID = 1L;
    private final Long PROJECT_ID = 1L;
    private final Project project = Project.builder()
            .id(PROJECT_ID)
            .name("projectName")
            .ownerId(USER_ID)
            .coverImageId("coverImageId")
            .storageSize(BigInteger.ONE)
            .galleryFileKeys(new ArrayList<>())
            .resources(new ArrayList<>())
            .build();
    private final ProjectDto projectDto = ProjectDto.builder()
            .id(PROJECT_ID)
            .coverImageId(null)
            .build();

    @BeforeEach
    void setUp() {
        projectService = new ProjectService(
                resourceValidator,
                projectValidator,
                amazonS3Client,
                resourceRepository,
                projectRepository,
                resourceMapper,
                teamMemberRepository,
                userContext,
                projectMapper,
                imageProcessingUtils);
    }

    @Test
    void shouldSuccessGetProjects() {
        List<Project> expectedProjects = List.of(project);
        when(projectRepository.findAllById(anyList())).thenReturn(expectedProjects);

        List<Project> result = projectRepository.findAllById(List.of(PROJECT_ID));
        assertEquals(expectedProjects, result);
    }

    @Test
    void shouldReturnsEmptyListIfProjectsAreNotExist() {
        when(projectRepository.findAllById(anyList())).thenReturn(List.of());

        List<Project> result = projectRepository.findAllById(List.of(PROJECT_ID));
        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateResourceIfFileIsImage() {
        String expectedFolder = project.getId() + project.getName();
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(teamMemberRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(Optional.of(TeamMember.builder().userId(USER_ID).build()));
        when(amazonS3Client.uploadFile(file, expectedFolder)).thenReturn(KEY);
        when(projectRepository.save(project)).thenReturn(project);
        when(file.getSize()).thenReturn(1L);
        when(file.getName()).thenReturn("file.png");
        when(file.getContentType()).thenReturn("image");
        Resource expectedResource = Resource
                .builder()
                .id(RESOURCE_ID)
                .name(file.getName())
                .key(KEY)
                .size(BigInteger.valueOf(file.getSize()))
                .createdAt(LocalDateTime.now())
                .type(ResourceType.IMAGE)
                .build();
        when(resourceRepository.save(any())).thenReturn(expectedResource);

        ResourceReadDto expectedDto = projectService.createResource(PROJECT_ID, file);
        assertEquals(KEY, expectedDto.key());
        assertEquals(file.getName(), expectedDto.name());
        assertNotNull(expectedDto.createdAt());
        assertNotNull(expectedDto.size());
        assertNotNull(expectedDto.type());
        assertTrue(project.getGalleryFileKeys().contains(KEY));
    }

    @Test
    void testCreateResourceIfFileIsNotImage() {
        String expectedFolder = project.getId() + project.getName();
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(teamMemberRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(Optional.of(TeamMember.builder().userId(USER_ID).build()));
        when(amazonS3Client.uploadFile(file, expectedFolder)).thenReturn(KEY);
        when(projectRepository.save(project)).thenReturn(project);
        when(file.getSize()).thenReturn(1L);
        when(file.getName()).thenReturn("file.txt");
        when(file.getContentType()).thenReturn("text/plain");
        Resource expectedResource = Resource
                .builder()
                .id(RESOURCE_ID)
                .name(file.getName())
                .key(KEY)
                .size(BigInteger.valueOf(file.getSize()))
                .createdAt(LocalDateTime.now())
                .type(ResourceType.IMAGE)
                .build();
        when(resourceRepository.save(any())).thenReturn(expectedResource);

        ResourceReadDto expectedDto = projectService.createResource(PROJECT_ID, file);
        assertEquals(KEY, expectedDto.key());
        assertEquals(file.getName(), expectedDto.name());
        assertNotNull(expectedDto.createdAt());
        assertNotNull(expectedDto.size());
        assertNotNull(expectedDto.type());
        assertFalse(project.getGalleryFileKeys().contains(KEY));
    }

    @Test
    void testCreateResourceThrowExceptionIfProjectNotExists() {
        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> projectService.createResource(PROJECT_ID, file));
    }

    @Test
    void testSuccessGetGallery() {
        Resource resource = Resource
                .builder()
                .id(RESOURCE_ID)
                .name(file.getName())
                .key(KEY)
                .size(BigInteger.valueOf(file.getSize()))
                .createdAt(LocalDateTime.now())
                .type(ResourceType.IMAGE)
                .build();
        project.getResources().add(resource);
        project.getGalleryFileKeys().add(KEY);
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));

        assertEquals(project.getResources().size(), projectService.getGallery(PROJECT_ID).size());
    }

    @Test
    void testGetGalleryThrowExceptionIfResourcesAreEmpty() {
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));

        assertThrows(DataValidationException.class, () -> projectService.getGallery(PROJECT_ID));
    }

    @Test
    void testSuccessDeleteResource() {
        Resource resource = Resource.builder()
                .id(RESOURCE_ID)
                .name("file.png")
                .key(KEY)
                .size(BigInteger.ONE)
                .createdAt(LocalDateTime.now())
                .type(ResourceType.IMAGE)
                .status(ResourceStatus.ACTIVE)
                .build();

        project.getResources().add(resource);
        project.getGalleryFileKeys().add(KEY);

        TeamMember user = TeamMember.builder()
                .userId(USER_ID)
                .build();

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
        when(teamMemberRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(Optional.of(user));
        when(resourceRepository.save(any(Resource.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(resourceMapper.toDto(any(Resource.class))).thenAnswer(invocation -> {
            Resource res = invocation.getArgument(0);
            return new ResourceReadDto(
                    String.valueOf(res.getId()), res.getName(), res.getKey(),
                    res.getSize(), res.getType(), res.getCreatedAt(),
                    res.getUpdatedBy() != null ? res.getUpdatedBy().getUserId() : null,
                    PROJECT_ID
            );
        });

        ResourceReadDto result = projectService.deleteResource(PROJECT_ID, RESOURCE_ID);
        verify(amazonS3Client).deleteFile(KEY);
        verify(projectRepository).save(project);
        assertNull(resource.getKey());
        assertEquals(BigInteger.ZERO, resource.getSize());
        assertEquals(ResourceStatus.DELETED, resource.getStatus());
        assertEquals(USER_ID, result.updatedById());
    }

    @Test
    void testDeleteResourceIfAccessDenied() {
        Resource resource = Resource.builder()
                .id(RESOURCE_ID)
                .name("file.png")
                .key(KEY)
                .size(BigInteger.ONE)
                .createdAt(LocalDateTime.now())
                .type(ResourceType.IMAGE)
                .status(ResourceStatus.ACTIVE)
                .build();

        TeamMember user = TeamMember.builder()
                .userId(USER_ID)
                .roles(List.of(TeamRole.DEVELOPER))
                .build();

        project.getResources().add(resource);
        project.getGalleryFileKeys().add(KEY);

        when(userContext.getUserId()).thenReturn(2L);
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
        when(teamMemberRepository.findByUserIdAndProjectId(2L, PROJECT_ID)).thenReturn(Optional.of(user));

        assertThrows(AccessDeniedException.class, () -> projectService.deleteResource(PROJECT_ID, RESOURCE_ID));
    }

    @Test
    void testDeleteResourceThrowExceptionIfResourceNotExists() {
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));

        assertThrows(EntityNotFoundException.class,
                () -> projectService.deleteResource(PROJECT_ID, RESOURCE_ID));

        verify(amazonS3Client, times(0)).deleteFile(KEY);
        verify(resourceRepository, times(0)).deleteById(RESOURCE_ID);
        verify(projectRepository, times(0)).save(project);
    }

    @Test
    void testAddingValidProjectCover() {
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(imageProcessingUtils.resizeImage(file)).thenReturn(new byte[0]);
        when(imageProcessingUtils.convertByteToMultipartFile(any(), any(), any())).thenReturn(file);
        when(amazonS3Client.uploadFile(any(MultipartFile.class), anyString())).thenReturn("s3-key");
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectDto);

        ProjectDto result = projectService.addProjectCover(PROJECT_ID, file);

        assertEquals(projectDto, result);
        verify(resourceValidator).validateResource(file);
        verify(resourceValidator).checkFileSize(file.getSize());
        verify(resourceValidator).checkIsFileImage(file);
        verify(amazonS3Client).uploadFile(any(MultipartFile.class), anyString());
        verify(projectRepository).save(project);
    }

    @Test
    void testDeletingProjectCover() {
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectDto);

        ProjectDto result = projectService.deleteProjectCover(PROJECT_ID);

        verify(amazonS3Client).deleteFile("coverImageId");
        verify(projectRepository).save(project);
        verify(projectMapper).toDto(project);
        Assertions.assertNull(project.getCoverImageId());
    }
}