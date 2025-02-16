package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.resource.ResourceReadDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ResourceMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private ProjectMapper projectMapper;

    @Mock
    private ImageProcessingUtils imageProcessingUtils;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private ProjectService projectService;


//    private final Long projectId = 1L;
//    private final Project project = Project.builder()
//            .id(projectId)
//            .coverImageId("coverImageId")
//            .build();


    private final String KEY = "key";
    private final Long RESOURCE_ID = 1L;
    private final Long PROJECT_ID = 1L;
    private final Project project = Project.builder()
            .id(PROJECT_ID)
            .name("projectName")
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
                projectMapper,
                imageProcessingUtils);
    }

    @Test
    public void shouldSuccessGetProjects() {
        List<Project> expectedProjects = List.of(project);
        when(projectRepository.findAllById(anyList())).thenReturn(expectedProjects);

        List<Project> result = projectRepository.findAllById(List.of(PROJECT_ID));
        assertEquals(expectedProjects, result);
    }

    @Test
    public void shouldReturnsEmptyListIfProjectsAreNotExist() {
        when(projectRepository.findAllById(anyList())).thenReturn(List.of());

        List<Project> result = projectRepository.findAllById(List.of(PROJECT_ID));
        assertTrue(result.isEmpty());
    }

    @Test
    void testUploadResourceToGallery() {
        String expectedFolder = project.getId() + project.getName();
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
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

        ResourceReadDto expectedDto = projectService.uploadResourceToGallery(PROJECT_ID, file);
        assertEquals(KEY, expectedDto.key());
        assertEquals(file.getName(), expectedDto.name());
        assertNotNull(expectedDto.createdAt());
        assertNotNull(expectedDto.size());
        assertNotNull(expectedDto.type());
    }

    @Test
    void testUploadResourceToGalleryThrowExceptionIfProjectNotExists() {
        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> projectService.uploadResourceToGallery(PROJECT_ID, file));
    }

    @Test
    void testSuccessGetAllProjectResources() {
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

        assertEquals(project.getResources().size(), projectService.getAllProjectResources(PROJECT_ID).size());
    }

    @Test
    void testGetAllProjectResourcesThrowExceptionIfResourcesAreEmpty() {
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));

        assertThrows(DataValidationException.class, () -> projectService.getAllProjectResources(PROJECT_ID));
    }

    @Test
    void testSuccessDeleteResourceFromGallery() {
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
        when(projectRepository.save(project)).thenReturn(project);

        projectService.deleteResourceFromGallery(PROJECT_ID, RESOURCE_ID);
        verify(amazonS3Client).deleteFile(KEY);
        verify(resourceRepository).deleteById(RESOURCE_ID);
        verify(projectRepository).save(project);
    }

    @Test
    void testDeleteResourceFromGalleryThrowExceptionIfResourceNotExists() {
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));

        assertThrows(EntityNotFoundException.class,
                () -> projectService.deleteResourceFromGallery(PROJECT_ID, RESOURCE_ID));

        verify(amazonS3Client, times(0)).deleteFile(KEY);
        verify(resourceRepository, times(0)).deleteById(RESOURCE_ID);
        verify(projectRepository, times(0)).save(project);
    }

    @Test
    public void testAddingValidProjectCover() {
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
    public void testDeletingProjectCover() {
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