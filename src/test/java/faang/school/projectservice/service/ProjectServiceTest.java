package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.s3.S3Service;
import faang.school.projectservice.service.imageprocessing.ImageProcessingUtils;
import faang.school.projectservice.validator.project.FileValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private ProjectRepository projectRepository;
    @Mock
    private ProjectService projectService;
    @Mock
    private S3Service s3Service;
    @Mock
    private ProjectMapper projectMapper;
    @Mock
    private FileValidator validator;
    @Mock
    private ImageProcessingUtils imageProcessingUtils;
    @Mock
    private MultipartFile file;


    private final Long projectId = 1L;
    private final Project project = Project.builder()
            .id(projectId)
            .coverImageId("coverImageId")
            .build();
    private final ProjectDto projectDto = ProjectDto.builder()
            .id(projectId)
            .coverImageId(null)
            .build();

    @BeforeEach
    void setUp() {
        projectService = new ProjectService(projectRepository,
                s3Service,
                projectMapper,
                validator,
                imageProcessingUtils);
    }

    @Test
    public void shouldSuccessGetProject() {
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));

        Project result = projectService.getProject(projectId);
        assertEquals(project, result);
    }

    @Test
    public void shouldThrowEntityNotFoundExceptionIfProjectNotExists() {
        when(projectRepository.findById(anyLong())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> projectService.getProject(projectId));
    }

    @Test
    public void shouldSuccessGetProjects() {
        List<Project> expectedProjects = List.of(project);
        when(projectRepository.findAllById(anyList())).thenReturn(expectedProjects);

        List<Project> result = projectRepository.findAllById(List.of(projectId));
        assertEquals(expectedProjects, result);
    }

    @Test
    public void shouldReturnsEmptyListIfProjectsAreNotExist() {
        when(projectRepository.findAllById(anyList())).thenReturn(List.of());

        List<Project> result = projectRepository.findAllById(List.of(projectId));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testAddingValidProjectCover() {
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(imageProcessingUtils.resizeImage(file)).thenReturn(new byte[0]);
        when(imageProcessingUtils.convertByteToMultipartFile(any(), any(), any())).thenReturn(file);
        when(s3Service.uploadFile(anyString(), any(MultipartFile.class))).thenReturn("s3-key");
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectDto);

        ProjectDto result = projectService.addProjectCover(projectId, file);

        assertEquals(projectDto, result);
        verify(validator, times(1)).validateFile(file);
        verify(validator, times(1)).checkFileSize(file.getSize());
        verify(validator, times(1)).checkIsFileImage(file);
        verify(s3Service, times(1)).uploadFile(anyString(), any(MultipartFile.class));
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    public void testDeletingProjectCover() {
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectDto);

        ProjectDto result = projectService.deleteProjectCover(projectId);

        verify(s3Service, times(1)).deleteFile("coverImageId");
        verify(projectRepository, times(1)).save(project);
        verify(projectMapper, times(1)).toDto(project);
        Assertions.assertNull(project.getCoverImageId());
    }
}